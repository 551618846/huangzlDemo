/**
 * 
 */
package com.ftsafe.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class FutureTaskDemo {
	
	public static void futureTaskRunnable(){
		FutureTask<String> ft = new FutureTask<String>(new Runnable() {
			
			public void run() {
				System.err.println("FutureTask Runnable run");
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "result");
		
		ft.run();
		try {
			System.err.println("ft.get() : "+ft.get());
			System.err.println("ft.get() : "+ft.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void futureTaskCallable(){
		FutureTask<String> ft = new FutureTask<String>(new Callable<String>() {

			public String call() throws Exception {
				System.err.println("FutureTask Callable call()");
				Thread.sleep(5000);
				return "Im result";
			}
			
			
		});
		
		ft.run();
		
		try {
			System.err.println("ft.get() : "+ft.get());
			System.err.println("ft.get() : "+ft.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	

	public static void main(String[] args) {
//		futureTaskCallable();
		futureTaskRunnable();
	}

}
