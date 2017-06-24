/**
 * 
 */
package com.ftsafe.sync;

import java.util.concurrent.Semaphore;

/**
 * 信号量,使用场景:控制访问资源的线程数量
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class SemaphoreDemo {
	
	class Pool{
		private int permits = 3;
		private Semaphore smp = new Semaphore(permits);
		
		private Object[] list = new Object[permits];
		private boolean[] statList = new boolean[permits];
		
		public Pool() {
			for(int i=0;i<permits;i++){
				list[i] = new Object();
				statList[i] = true;
			}
		}
		
		Object get() throws InterruptedException{
			//获得smp,再获取资源,逻辑上可以保证获得smp,即可获得资源
			smp.acquire();
			try {
				return getEnableObject();
			} catch (Exception e) {
				smp.release();//获取过程异常,则释放smp
			}
			return null;
		}
		
		void release(Object object){
			//返回资源,成功返回后才释放smp
			if(releaseObjectEnable(object)){
				smp.release();
			}
		}
		
		private synchronized Object getEnableObject(){
			//数组操作必须保证线程安全
			//获取一个可用资源
			for(int i=0;i<permits;i++){
				if(statList[i]){
					statList[i] = false;
					return list[i];
				}
			}
			return null;
		}
		
		private synchronized boolean releaseObjectEnable(Object object){
			//数组操作必须保证线程安全
			for(int i=0;i<permits;i++){
				if(list[i] == object){
					System.err.println(Thread.currentThread().getName()+" release "+object);
					statList[i] = true;
					return true;
				}
			}
			return false;
		}
	}
	
	

	public static void main(String[] args) {
		final Pool pool = new SemaphoreDemo().new Pool();
		
		for(int i=0;i<10;i++){
			new Thread(new Runnable() {
				
				public void run() {
					try {
						Object x = pool.get();
						System.err.println(Thread.currentThread().getName()+" get "+x);
						Thread.sleep(8000);
						
						pool.release(x);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
		}
	}

}
