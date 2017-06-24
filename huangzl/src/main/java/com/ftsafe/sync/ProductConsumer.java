/**
 * 
 */
package com.ftsafe.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductConsumer {
	
	private boolean consume = true;
	
	synchronized public void product(){
		
		while(!consume){//if?while?由于notify可能唤醒生产者,需要继续wait(),所以while.如果使用两个Condition,则if即可
			try {
				wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		System.err.println("product");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		consume = false;
//		notify();//如果有多个生产者线程,notify可能唤醒还是生产者
		notifyAll();
	}
	
	synchronized public void consume(){
		while(consume){//if?while?
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.err.println("===consume");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		consume = true;
//		notify();////如果有多个消费者线程,notify可能唤醒还是消费者,导致所有线程都是wait()
		notifyAll();
	}
	
	
	public void producters(){
		for(int i=0;i<2;i++){//多个producter线程,必须notifyAll()
			Thread producter = new Thread(new Runnable() {
				
				public void run() {
					while(true){
						product();
					}
				}
			});
			producter.start();
		}
		
	}
	
	public void consumers(){
		for(int i=0;i<2;i++){//多个consumer线程,必须notifyAll()
			Thread consumer = new Thread(new Runnable() {
				
				public void run() {
					while(true){
						consume();
					}
				}
			});
			consumer.start();
		}
		
		
	}
	

	public static void main(String[] args) {
		ProductConsumer pc = new ProductConsumer();
		pc.producters();
		pc.consumers();
		

	}
	
	/**
	 * 以上程序多个consumer线程+多个product线程,且只notify(),导致所有线程wait(),jstack查看如下
	 * in Object.wait()表示线程持有锁,然后调用wait()进入WAITING状态
	 * - waiting on <0x00000000d799acd0>
	 * - locked <0x00000000d799acd0>
	 * 
	 * C:\Users\Administrator>jstack 4256
2017-04-23 13:31:11
Full thread dump Java HotSpot(TM) 64-Bit Server VM (24.51-b03 mixed mode):

"DestroyJavaVM" prio=6 tid=0x0000000001cfe800 nid=0x2b14 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Thread-3" prio=6 tid=0x000000000c34b800 nid=0x28bc in Object.wait() [0x000000000d1ae000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at java.lang.Object.wait(Object.java:503)
        at com.ftsafe.sync.ProductConsumer.consume(ProductConsumer.java:33)
        - locked <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at com.ftsafe.sync.ProductConsumer$2.run(ProductConsumer.java:71)
        at java.lang.Thread.run(Thread.java:744)

"Thread-2" prio=6 tid=0x000000000c345800 nid=0x24f4 in Object.wait() [0x000000000d01f000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at java.lang.Object.wait(Object.java:503)
        at com.ftsafe.sync.ProductConsumer.consume(ProductConsumer.java:33)
        - locked <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at com.ftsafe.sync.ProductConsumer$2.run(ProductConsumer.java:71)
        at java.lang.Thread.run(Thread.java:744)

"Thread-1" prio=6 tid=0x000000000c344800 nid=0x5dc in Object.wait() [0x000000000ce1f000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at java.lang.Object.wait(Object.java:503)
        at com.ftsafe.sync.ProductConsumer.product(ProductConsumer.java:14)
        - locked <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at com.ftsafe.sync.ProductConsumer$1.run(ProductConsumer.java:56)
        at java.lang.Thread.run(Thread.java:744)

"Thread-0" prio=6 tid=0x000000000c344000 nid=0x864 in Object.wait() [0x000000000cc2f000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at java.lang.Object.wait(Object.java:503)
        at com.ftsafe.sync.ProductConsumer.product(ProductConsumer.java:14)
        - locked <0x00000000d799acd0> (a com.ftsafe.sync.ProductConsumer)
        at com.ftsafe.sync.ProductConsumer$1.run(ProductConsumer.java:56)
        at java.lang.Thread.run(Thread.java:744)

"Service Thread" daemon prio=6 tid=0x000000000c326800 nid=0x9fc runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" daemon prio=10 tid=0x000000000c323000 nid=0x18d4 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" daemon prio=10 tid=0x000000000abdd000 nid=0x26ec waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Attach Listener" daemon prio=10 tid=0x000000000abdb800 nid=0x1be0 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Signal Dispatcher" daemon prio=10 tid=0x000000000ab82000 nid=0x170c runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Finalizer" daemon prio=8 tid=0x000000000ab68800 nid=0x19d8 in Object.wait() [0x000000000becf000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d7905568> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:135)
        - locked <0x00000000d7905568> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:151)
        at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:189)

"Reference Handler" daemon prio=10 tid=0x000000000ab5f800 nid=0x1fc4 in Object.wait() [0x000000000bd5f000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000d79050f0> (a java.lang.ref.Reference$Lock)
        at java.lang.Object.wait(Object.java:503)
        at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:133)
        - locked <0x00000000d79050f0> (a java.lang.ref.Reference$Lock)

"VM Thread" prio=10 tid=0x000000000ab5c000 nid=0x14c8 runnable

"GC task thread#0 (ParallelGC)" prio=6 tid=0x0000000001d4d800 nid=0x23e4 runnable

"GC task thread#1 (ParallelGC)" prio=6 tid=0x0000000001d4f800 nid=0x1d30 runnable

"GC task thread#2 (ParallelGC)" prio=6 tid=0x0000000001d51000 nid=0x1260 runnable

"GC task thread#3 (ParallelGC)" prio=6 tid=0x0000000001d52800 nid=0x1c30 runnable

"VM Periodic Task Thread" prio=10 tid=0x000000000c33f800 nid=0x11f4 waiting on condition

JNI global references: 114
	 * 
	 */

}
