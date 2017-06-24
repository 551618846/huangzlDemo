/**
 * 
 */
package com.ftsafe.tcp;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class ServerSocketDemo {
	
	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket(8888);
		
		boolean run = true;
		while(run){
			final Socket accept = ss.accept();
			new Thread(new Runnable() {
				public void run() {
					try {
						int getSoTimeout = accept.getSoTimeout();
						System.err.println("getSoTimeout:"+getSoTimeout);
//						accept.setSoTimeout(timeout);//必须在进入阻塞操作之前启用该选项才能生效。
						OutputStream out = accept.getOutputStream();
						
						int i=99;
						while(i<99999){
							out.write(i);//注意，超过255会被截取,即截取2进制末尾8bit,一个byte
//							out.write(b);
							System.err.println(Thread.currentThread()+" write:"+i+":"+(byte)i);
							Thread.sleep(3000);//长期等待异常?//java.net.SocketException: Connection reset by peer: socket write error
							//Thread.sleep模拟网络阻塞,数据包未到达客户端
							i++;
							/*if(i > 105){
								out.write("\n".getBytes());//对方readLine才能返回
							}*/
						}
						accept.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
		}
		
		ss.close();
		
	}

}
