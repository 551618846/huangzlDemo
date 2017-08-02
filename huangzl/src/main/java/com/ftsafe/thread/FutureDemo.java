/**
 * 
 */
package com.ftsafe.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class FutureDemo {
	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception{
		FutureTask<Integer> future = new FutureTask<Integer>(new Callable<Integer>() {
			public Integer call() throws Exception {
				Thread.sleep(4000);
				System.err.println("call"+System.currentTimeMillis());
				return 1;
			}
		});
		new Thread(future).start();//必须
		
//		Thread.sleep(4000);
		System.err.println(System.currentTimeMillis()+":"+future.get());
		System.err.println(System.currentTimeMillis()+":"+future.get());
		
	}

}
