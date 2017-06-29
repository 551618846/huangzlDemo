/**
 * 
 */
package com.ftsafe.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * 客户端NIO逻辑:
 * 1,主线程:创建selector,循环调用select()方法等待IO事件(注意需要synchronized),处理IO事件,注意处理IO事件最好使用线程池,避免影响主线程接收IO事件
 * 2,创建连接:打开channel,注册到selector,attach标识该channel,即可.注意需要synchronized来selector.wakeup()和cssChan.register(..)
 */
public class NIOSocketDemo {
	final static String host = "localhost";
	final static int port = 8888;

	public static void main(String[] args) throws Exception {
		Object lock = new Object();
		Selector selector = startMainSelector(lock);
		connectionAndSend(selector, lock);
		
		/*Object lock = new Object();
		Selector selector = startMainSelector(lock);
		Thread.sleep(1000*5);
		//假设这是一个http客户端,会同时创建多个连接,分别请求js,css
		createRequet(selector,lock);
		createRequet(selector,lock);
		*/
		
//		readNoBlockingMode();
		
//		readBlockingMode();
	}
	
	public static void connectionAndSend(Selector selector,Object lock) throws Exception{
		//一般连接的逻辑:创建SocketChannel,注册到主selector
		SocketChannel cssChan = SocketChannel.open();
		cssChan.configureBlocking(false);// 非阻塞模式,读取不能用常规的while
		if (!cssChan.connect(new InetSocketAddress(host, port))) {
			// 不断地轮询连接状态，直到完成连接
			while (!cssChan.finishConnect()) {
				// 在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
				// 这里为了演示该方法的使用，只是一直打印"."
				System.out.print(".");
			}
		}
		System.err.println("cssChan finishConnect");
		
		/**
		 * 需要锁,wakeup()后才能register,避免wakeup()后被其他线程select()再阻塞
		 */
		synchronized (lock) {
			selector = selector.wakeup();//
			SelectionKey cssSelectionKey = cssChan.register(selector, SelectionKey.OP_READ);//注册到selector,该channel有IO事件时,会通知selector
			/*************测试发现,如果该selector已经调用阻塞的select()方法,那么register方法会阻塞!!!!!!!!!!!!!*/
			StringBuffer cssStringBuffer = new StringBuffer();
			//可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。
			cssSelectionKey.attach(cssStringBuffer);
			
			//注册写操作?
//			cssChan.register(selector, SelectionKey.OP_WRITE);
		}
		
		//写数据这样直接写还是注册到selector?//TODO
		String newData = "1234567890123456789";
		ByteBuffer buf = ByteBuffer.allocate(48);
		buf.clear();
		buf.put(newData.getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			cssChan.write(buf);//是否阻塞?
		}
		System.err.println("先写部分:"+newData);
		Thread.sleep(1000*10);
		
		buf.clear();
		buf.put("abcd".getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			cssChan.write(buf);//是否阻塞?
		}
		cssChan.write(buf);
		System.err.println("再写部分:"+newData);
		
	}
	
	
	public static void readNoBlockingMode() throws Exception{
		//客户端使用Selector好像没啥优势??
		final Selector selector = Selector.open();
		
		//一般连接的逻辑:创建SocketChannel,注册到主selector
		SocketChannel cssChan = SocketChannel.open();
		cssChan.configureBlocking(false);// 非阻塞模式,读取不能用常规的while
		if (!cssChan.connect(new InetSocketAddress(host, port))) {
			// 不断地轮询连接状态，直到完成连接
			while (!cssChan.finishConnect()) {
				// 在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
				// 这里为了演示该方法的使用，只是一直打印"."
				System.out.print(".");
			}
		}
		System.err.println("cssChan finishConnect");
		
		SelectionKey cssSelectionKey = cssChan.register(selector, SelectionKey.OP_READ);//注册到selector,该channel有IO事件时,会通知selector
		StringBuffer cssStringBuffer = new StringBuffer();
		//可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。
		cssSelectionKey.attach(cssStringBuffer);
		
		
		boolean flag = true;//运行标志
		while(flag){
			/**
			 * 一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。
			 * Selector必须先注册了通道,再select()
			 */
			int readyChannels = selector.select();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
			if(readyChannels == 0){
				continue;
			}
			
			Set<SelectionKey> selectedKeys = selector.selectedKeys();//IO事件就绪的channel集合//非线程安全
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
			while(keyIterator.hasNext()) {
			    SelectionKey key = keyIterator.next();
			    if(key.isAcceptable()) {
			        // a connection was accepted by a ServerSocketChannel.
			    	System.err.println("a connection was accepted by a ServerSocketChannel.");
			    } else if (key.isConnectable()) {
			        // a connection was established with a remote server.
			    	System.err.println("a connection was established with a remote server.");
			    } else if (key.isReadable()) {
			        // a channel is ready for reading
			    	System.err.println("a channel is ready for reading");
			    	
			    	//工作线程处理?
			    	SocketChannel selectedChannel = (SocketChannel)key.channel();
			    	
			    	//attachment,绑定到对应Channel的对象
			    	StringBuffer stringBuffer = (StringBuffer)key.attachment();
			    	
			    	ByteBuffer readBuf = ByteBuffer.allocate(256);
					while (selectedChannel.read(readBuf) > 0) {// 数据入Buffer
						readBuf.flip();
						String r = new String(readBuf.array(), 0, readBuf.limit());// 从Buffer取数据
						System.err.println("read:" + r);
						stringBuffer.append(r);
						readBuf.clear();
					}
					//假设是http协议,这里需要解析数据,直到http包格式完整
					//stringBuffer是否协议完整,完整则可以关闭Channel
					if(stringBuffer.length()>20){//这里模拟协议:报文长度20
						System.err.println("完成协议:"+stringBuffer);
						System.err.println("关闭Channel"+selectedChannel.getLocalAddress());
						selectedChannel.close();
						
						flag = false;//关闭Channel,设置运行标志
					}
			    	
			    } else if (key.isWritable()) {
			        // a channel is ready for writing
			    	System.err.println("a channel is ready for writing");
			    }
			    keyIterator.remove();
			}
			
			System.err.println("本次io完成,线程不会像原始socket一样阻塞,可处理其他事件");
		}
	}

	
	public static void createRequet(Selector selector,Object lock) throws Exception {
		//一般连接的逻辑:创建SocketChannel,注册到主selector
		SocketChannel cssChan = SocketChannel.open();
		cssChan.configureBlocking(false);// 非阻塞模式,读取不能用常规的while
		if (!cssChan.connect(new InetSocketAddress(host, port))) {
			// 不断地轮询连接状态，直到完成连接
			while (!cssChan.finishConnect()) {
				// 在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
				// 这里为了演示该方法的使用，只是一直打印"."
				System.out.print(".");
			}
		}
		System.err.println("cssChan finishConnect");
		
		/**
		 * 需要锁,wakeup()后才能register,避免wakeup()后被其他线程select()再阻塞
		 */
		synchronized (lock) {
			selector = selector.wakeup();//
			SelectionKey cssSelectionKey = cssChan.register(selector, SelectionKey.OP_READ);//注册到selector,该channel有IO事件时,会通知selector
			/*************测试发现,如果该selector已经调用阻塞的select()方法,那么register方法会阻塞!!!!!!!!!!!!!*/
			StringBuffer cssStringBuffer = new StringBuffer();
			//可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。
			cssSelectionKey.attach(cssStringBuffer);
			
			System.err.println("=========");
		}
		
	}

	public static Selector startMainSelector(final Object lock) throws Exception {
		//假设这是一个http客户端,会同时创建多个连接,分别读取js,css

		/**
		 * 一般main函数的逻辑:创建全局selector,循环监听Channel事件(selector.select()),处理事件(获取Channel,读写数据)
		 * 注意
		 */
		final Selector selector = Selector.open();
		
		/**
		 * 在新线程使用selector.select(),测试发现,select()阻塞后[selectNow()不会阻塞],其他线程的SocketChannel注册register到该selector会阻塞
		 * 所以,必须先selector.wakeup()再register()，而且要加锁，避免wakeup()后被其他线程select()再阻塞
		 */
		new Thread(new Runnable() {
			
			public void run() {
				try {
					while(true){
						/**
						 * 在selector.select()前先判断锁是否被持有,即是被register持有
						 */
						synchronized(lock){
						}
						
						int readyChannels = selector.select();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
//						int readyChannels = selector.selectNow();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
						if(readyChannels == 0){
							continue;
						}
						
						Set<SelectionKey> selectedKeys = selector.selectedKeys();//IO事件就绪的channel集合//非线程安全
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
						while(keyIterator.hasNext()) {
						    SelectionKey key = keyIterator.next();
						    if(key.isAcceptable()) {
						        // a connection was accepted by a ServerSocketChannel.
						    	System.err.println("a connection was accepted by a ServerSocketChannel.");
						    } else if (key.isConnectable()) {
						        // a connection was established with a remote server.
						    	System.err.println("a connection was established with a remote server.");
						    } else if (key.isReadable()) {
						        // a channel is ready for reading
						    	System.err.println("a channel is ready for reading");
						    	//工作线程处理?
						    	SocketChannel selectedChannel = (SocketChannel)key.channel();
						    	String id = selectedChannel.getLocalAddress()+" ";
						    	
						    	//attachment,绑定到对应Channel的对象
						    	StringBuffer stringBuffer = (StringBuffer)key.attachment();
						    	
						    	ByteBuffer readBuf = ByteBuffer.allocate(256);
						    	/**
						    	 * 本次事件需要循环read,直到流的末尾。并且可能有多次该Channel的IO事件，直到数据包完整
						    	 */
								while (selectedChannel.read(readBuf) > 0) {// 数据入Buffer
									readBuf.flip();
									String r = new String(readBuf.array(), 0, readBuf.limit());// 从Buffer取数据
									System.err.println(id+"...read:" + r);
									stringBuffer.append(r);
									readBuf.clear();
								}
								//假设是http协议,这里需要解析数据,直到http包格式完整
								//stringBuffer是否协议完整,完整则可以关闭Channel
								if(stringBuffer.length()>20){//这里模拟协议:报文长度20
									System.err.println(id+"...完成协议:"+stringBuffer);
									
									/**
									 * //客户端收完数据可以关闭Channel.使用线程池处理数据,一类操作使用一个线程池(对比旧io模式,线程池不用处理IO阻塞,效率好很多)
									 * //服务端一般需要处理业务逻辑,响应客户端
									 */
									System.err.println(id+"...关闭Channel"+selectedChannel.getLocalAddress());
									/**selectedChannel.close()服务端Channel会无限触发key.isReadable????注释掉就不会**/
//									selectedChannel.close();//TODO
									
								}
						    	
						    } else if (key.isWritable()) {
						        // a channel is ready for writing
						    	System.err.println("a channel is ready for writing");
						    }
						    keyIterator.remove();
						}
						
//						System.err.println("本次io完成,线程不会像原始socket一样阻塞,可处理其他事件");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}//end try
			}//end run
		}).start();
		
		return selector;
	}

	
	public static void readBlockingMode() throws Exception {
		SocketChannel clntChan = SocketChannel.open();
		// clntChan.configureBlocking(false);//非阻塞模式,读取不能用常规的while

		if (!clntChan.connect(new InetSocketAddress(host, port))) {
			// 不断地轮询连接状态，直到完成连接
			while (!clntChan.finishConnect()) {
				// 在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
				// 这里为了演示该方法的使用，只是一直打印"."
				System.out.print(".");
			}
		}

		System.err.println("finishConnect");
		StringBuffer stringBuffer = new StringBuffer();
		ByteBuffer readBuf = ByteBuffer.allocate(1024);

		while (clntChan.read(readBuf) > 0) {// 数据入Buffer
			readBuf.flip();
			String r = new String(readBuf.array(), 0, readBuf.limit());// 从Buffer取数据
			System.err.println("read:" + r);
			stringBuffer.append(r);
			readBuf.clear();
		}
		System.err.println(stringBuffer);
		clntChan.close();

	}
	
}
