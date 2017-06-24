/**
 * 
 */
package com.ftsafe.sync;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class CountDownLatchDemo {
	
	

	public static void main(String[] args) {
		int times = 3;
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(times);
		
		for(int i=0;i<times;i++){
			new Thread(new Runnable() {
				
				public void run() {
					try {
						start.await();
						
						Thread.sleep(10000);
						System.err.println("doing until ...");
						
						done.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		
		//Thread等待直到此处可开始
		start.countDown();
		
		try {
			//main等待直到可结束
			done.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
