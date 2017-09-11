/**
 * 
 */
package com.ftsafe.oom;

import java.util.ArrayList;

public class OOMDemo {
	
	
	
	public static void main(String[] args) throws Throwable {
//		heapSpace();
		
//		stackOverflow();
		
//		stackOverByThread();
	}
	
	/**
	 * 运行参数:-Xss20M
	 * java进程内存为操作系统限制.
	 * 栈总内存=java进程限制-xmx-MaxPermSize
	 * 线程数=栈总内存/栈大小,Xss越大,创建线程越少
	 * java.lang.OutOfMemoryError: unable to create new native thread
	 */
	public static void stackOverByThread(){
		//注意window可能死机!!!!!!!!!
		while(true){
			new Thread(new Runnable() {
				public void run() {
					while(true){
						try {
							Thread.sleep(1000*60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
	
	
	private static int stackLenth = 1;
	/**
	 * 运行参数:-Xss128k
	 * java.lang.StackOverflowError
	 * 栈深度,栈大小,有关
	 */
	public static void stackOverflow(){
		stackLenth++;//测试栈深度,改变栈大小Xss,会影响栈深度
		try {
			stackOverflow();//嵌套调用自己
		} catch (Throwable e) {
			System.err.println("stackLenth:"+stackLenth);
//			throw e;
		}
		//Xss128k,栈深度1000左右,一般程序够用
		//Xss512k,栈深度5458左右
	}
	
	/**
	 * 运行参数:-Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
	 * dump文件在项目根目录,使用eclipse memory analyzer分析
	 * 内存中的对象是否必须,可分为
	 * 1内存溢出:对象是必须的.需要内存大于设置内存.尝试减少内存消耗或增加内存
	 * 2内存泄露:对象不是必须.未能gc,gcroots路径
	 * java.lang.OutOfMemoryError: Java heap space
	 */
	public static void heapSpace(){
		ArrayList<OOMDemo> list = new ArrayList<OOMDemo>();
		while(true){
			OOMDemo obj = new OOMDemo();
			list.add(obj);
		}
	}

}
