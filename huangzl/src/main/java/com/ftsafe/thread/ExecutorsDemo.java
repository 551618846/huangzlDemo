/**
 * 
 */
package com.ftsafe.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class ExecutorsDemo {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(new Runnable() {
			
			public void run() {
				
				
			}
		});
	}

}
