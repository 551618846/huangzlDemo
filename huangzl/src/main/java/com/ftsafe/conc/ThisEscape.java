/**
 * 
 */
package com.ftsafe.conc;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class ThisEscape {

	private int num=9;
	
	public ThisEscape() {
		new Thread(new Runnable() {
			public void run() {
				System.err.println("..."+this);
				System.err.println("..."+ThisEscape.this);
				System.err.println("..."+ThisEscape.this.num);
				//构造方法使this对象逸出
				//构造方法使用内部类,导致外部类this对象逃逸//内部类包含外部类的隐含引用
				//解决方法:把一些操作提到构造方法外,避免this逸出.这里可以把start方法提到实例方法
				//一般是私有构造方法+工厂方法
			}
		}).start();
	}
	
	public static void main(String[] args) {
		new ThisEscape();
	}
	
}
