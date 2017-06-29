/**
 * 
 */
package com.ftsafe.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * 基本思想,Channel都注册到selector让他处理;一个selector一般处理一类事件
 * 服务端NIO逻辑:处理2类IO事件,1accept事件,使用一个selector.2read/write事件,使用一个selector。每种selector一个线程
 * 1,主线程:创建accept事件selector,开启accept线程:循环调用select()方法等待IO事件，逻辑见4
 * 2,主线程:ServerSocketChannel,注册到1中的accept事件selector
 * ==============
 * 3,主线程:创建read/write事件selector,开启rw线程:循环调用select()方法(select()前需要synchronized)..,处理rw事件,注意业务逻辑最好使用线程池,避免影响主线程接收IO事件
 * 4,1中accept事件逻辑：打开对应的SocketChannel,注册到3中rw事件selector
 *
 */
public class NIOServerSocketDemo {
	
	public static void main(String[] args) throws Exception {
		NIOServerSocketDemo demo = new NIOServerSocketDemo();
		demo.init();
		demo.starAcceptThread();
		demo.starRWThread();
		demo.starWriteThread();
		
		//挂起主线程
		new CountDownLatch(1).await();
	}
	
	ServerSocketChannel ssc = null;
	Selector acpSelector = null;
	Selector rwSelector = null;
	Selector writeSelector = null;
	int SERVER_PORT = 8888;
	Object lock = new Object();
	
	public void init(){
		try {
			acpSelector = Selector.open();
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(SERVER_PORT));
			ssc.configureBlocking(false);
			ssc.register(acpSelector, SelectionKey.OP_ACCEPT);
			//这之后开启accept线程starAcceptThread()

			rwSelector = Selector.open();
			writeSelector = Selector.open();

			System.err.println("init..");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void starWriteThread(){
		new Thread(new Runnable() {
			
			public void run() {
				System.err.println("starWriteThread..");
				while(true){
					try {
						/**
						 * Selector.select()之前先判断下同步,避免阻塞Selector上的register
						 */
						synchronized (lock) {
						}
						int readyChannels = writeSelector.select();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
						if(readyChannels == 0){
							continue;
						}
						
						Set<SelectionKey> selectedKeys = writeSelector.selectedKeys();//IO事件就绪的channel集合//非线程安全
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
						while(keyIterator.hasNext()) {
						    SelectionKey key = keyIterator.next();
						    keyIterator.remove();//先remove
						    if(key.isReadable()) {
						    	System.err.println("a channel is ready for reading");
						    } else if (key.isWritable()) {
						        // a channel is ready for writing
						    	System.err.println("---------------------------------------");
						    	SocketChannel selectedChannel = (SocketChannel)key.channel();
						    	selectedChannel.configureBlocking(false);
						    	String id = selectedChannel.getLocalAddress()+"=="+selectedChannel.getRemoteAddress();
						    	System.err.println(id+"a channel is ready for writing");
						    	
						    	ByteBuffer buf = (ByteBuffer)key.attachment();
						    	//上次写到的位置
						    	while(buf.hasRemaining()) {
						    		selectedChannel.write(buf);//是否阻塞?
								}
						    	//TODO//关闭selectedChannel?
						    	System.err.println(id+"服务端..响应结果");
						    	System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						    } else if (key.isAcceptable()) {
						    	System.err.println("a connection was accepted by a ServerSocketChannel.");
						    } else if (key.isConnectable()) {
						    	System.err.println("a connection was established with a remote server.");
						    }
						    
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
	
	
	public void starRWThread(){
		new Thread(new Runnable() {
			
			public void run() {
				System.err.println("starRWThread..");
				while(true){
					try {
						/**
						 * rwSelector.select()之前先判断下同步,避免阻塞rwSelector上的register
						 */
						synchronized (lock) {
						}
						int readyChannels = rwSelector.select();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
						if(readyChannels == 0){
							continue;
						}
						
						Set<SelectionKey> selectedKeys = rwSelector.selectedKeys();//IO事件就绪的channel集合//非线程安全
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
						while(keyIterator.hasNext()) {
						    SelectionKey key = keyIterator.next();
						    keyIterator.remove();//先remove
						    if(key.isReadable()) {
						    	/**
						    	 * 测试发现客户端收到响应后调用Channel.close(),那么服务端Channel会不断收到key.isReadable,但其实没有数据可以read
						    	 */
						    	// a channel is ready for reading
						    	System.err.println("+++++++++++++++++++++++++++");
						    	SocketChannel selectedChannel = (SocketChannel)key.channel();
						    	selectedChannel.configureBlocking(false);
						    	
						    	String id = selectedChannel.getLocalAddress()+"=="+selectedChannel.getRemoteAddress();

						    	System.err.println("a channel is ready for reading "+id);
						    	StringBuffer stringBuffer = (StringBuffer)key.attachment();
						    	ByteBuffer readBuf = ByteBuffer.allocate(256);
								while (selectedChannel.read(readBuf) > 0) {// 数据入Buffer
									readBuf.flip();
									String r = new String(readBuf.array(), 0, readBuf.limit());// 从Buffer取数据
									System.err.println(id+" read:" + r);
									stringBuffer.append(r);
									readBuf.clear();
								}
								//IO完成后,业务逻辑可以使用线程池处理
								//假设是http协议,这里需要解析数据,直到http包格式完整
								//stringBuffer是否协议完整,完整则可以关闭Channel
								if(stringBuffer.length()>20){//这里模拟协议:报文长度20
									System.err.println(id+" 完成协议:"+stringBuffer);
									/**
									 * //处理业务逻辑:比如调用方法,生成响应html,写回客户端(注册到写selector,attach要写的buffer,让selector处理)
									 */
									try {
										System.err.println(id+"服务端..处理业务逻辑");
										Thread.sleep(1000*3);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//返回响应到客户端
									/**如果客户端收到响应后调用Channel.close(),那么服务端Channel会不断收到key.isReadable????注释掉就不会**/
									/**
									 * 需要锁,wakeup()后才能register,避免wakeup()后被其他线程select()再阻塞
									 */
									//注册写操作,不直接selectedChannel.write(buf);//selectedChannel.write是否阻塞?
									synchronized (lock) {
										writeSelector.wakeup();//
										SelectionKey selectionKey = selectedChannel.register(writeSelector, SelectionKey.OP_WRITE);//注册到selector,该channel有IO事件时,会通知selector
										ByteBuffer buf = ByteBuffer.allocate(48);
										buf.clear();
										buf.put(id.getBytes());
										buf.flip();
										selectionKey.attach(buf);
									}
									System.err.println(id+"服务端..注册写事件");
									System.err.println("=============================");
									//关闭Channel?
									//selector中注销该Channel?
								}
						    } else if (key.isWritable()) {
						        // a channel is ready for writing
						    	System.err.println("a channel is ready for writing");
						    } else if (key.isAcceptable()) {
						    	System.err.println("a connection was accepted by a ServerSocketChannel.");
						    } else if (key.isConnectable()) {
						    	System.err.println("a connection was established with a remote server.");
						    }
						    
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
	
	public void starAcceptThread(){
		new Thread(new Runnable() {
			
			public void run() {
				System.err.println("starAcceptThread..");
				while(true){
					try {
						/**
						 * 这里可以不用同步,只有一个ServerSocketChannel,已经注册到acpSelector
						 */
						int readyChannels = acpSelector.select();//一般在循环调用//阻塞,直到有一个channel准备就绪(IO事件就绪)
						if(readyChannels == 0){
							continue;
						}
						
						Set<SelectionKey> selectedKeys = acpSelector.selectedKeys();//IO事件就绪的channel集合//非线程安全
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
						while(keyIterator.hasNext()) {
						    SelectionKey key = keyIterator.next();
						    if(key.isAcceptable()) {
						        // a connection was accepted by a ServerSocketChannel.
						    	System.err.println("a connection was accepted by a ServerSocketChannel.");
						    	ServerSocketChannel selectedChannel = (ServerSocketChannel)key.channel();
						    	SocketChannel newConnection = selectedChannel.accept();
						    	newConnection.configureBlocking(false);
						    	synchronized (lock) {
						    		rwSelector.wakeup();
						    		//注册读事件
						    		newConnection.register(rwSelector, SelectionKey.OP_READ, new StringBuffer());
							    	
							    	//写事件//TODO
								}
						    	
						    }
						    keyIterator.remove();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
	
	
	
	

}
