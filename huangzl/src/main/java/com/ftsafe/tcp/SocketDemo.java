/**
 * 
 */
package com.ftsafe.tcp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class SocketDemo {
	
	public static void main(String[] args) throws Exception {
//		readByteBlocking();
		readLineBlocking();
	}
	
	public static void readLineBlocking() throws Exception{
		String host = "localhost";
		int port = 8888;
		Socket so = new Socket(host, port);
//		so.setSoTimeout(2);//java.net.SocketTimeoutException: Read timed out
		InputStream ins = so.getInputStream();
		InputStreamReader reader = new InputStreamReader(ins);
		
		BufferedReader br = new BufferedReader(reader);
		StringBuffer sb = new StringBuffer();
		String line = null;
		while( (line = br.readLine()) != null){//阻塞直到能读到行,或者对方Socket.close。或者各种timeout抛异常
			sb.append(line);
			System.err.println("read:"+line+":");//部分byte不是可视字符
		}
		System.err.println(sb);
		
		so.close();
	}
	
	
	public static void readByteBlocking() throws Exception{
		String host = "localhost";
		int port = 8888;
		Socket so = new Socket(host, port);
		so.setSoTimeout(2);//java.net.SocketTimeoutException: Read timed out
		InputStream ins = so.getInputStream();
		StringBuffer sb = new StringBuffer();
		int c = 0;
		while( (c = ins.read()) != -1){//什么时候阻塞?阻塞直到能读到下一个字节(next byte),或者对方Socket.close。或者各种timeout抛异常
			sb.append((char)c);
			System.err.println("read:"+c);
		}
		System.err.println(sb);
		
		so.close();
	}

}
