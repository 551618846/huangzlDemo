/**
 * 
 */
package com.ftsafe.sync;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class CyclicBarrierDemo {
	
	private volatile boolean done = false;//是否完成
	
	int parties = 3;//子任务数
	
	//可重复
	final CyclicBarrier cb = new CyclicBarrier(parties, new Runnable() {
		
		public void run() {
			System.err.println("all done");
			//这里一般 拿到各个线程的分结果,合并出最终结果,判断是否完成
			Random rd = new Random();
			if(rd.nextBoolean()){//模拟任务是否完成
				done = true;
			}
			
		}
	});
	
	
	public CyclicBarrierDemo() {
		for(int i=0;i<parties;i++){
			final int t = i;
			new Thread(new Runnable() {
				
				public void run() {
					while(!done){//使用while+done标志
						try {
//							cb.await();
							Thread.sleep(3000);
							System.err.println(t+" done.");
							int index = cb.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (BrokenBarrierException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	public static void main(String[] args) {
		CyclicBarrierDemo cbd = new CyclicBarrierDemo();

	}

}
