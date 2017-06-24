/**
 * 
 */
package com.ftsafe.misc;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *调试技巧:
 *step into,step over,step return:debug模式下->工具栏
 *drop to frame:debug模式下->工具栏->drop to frame
 *跟踪变量:debug模式下->outline视图->右键实例变量->toggleWatchPoint
 *跟踪异常
 *
 */
public class DebugDemo {
	
	//跟踪变量:debug模式下->outline视图->右键实例变量->toggleWatchPoint
	private String attrWatchPoint = "a";
	
	private void stepInto(){
		System.err.println("stepInto");
		System.err.println("");
	}
	
	//重新执行方法(Frame):工具栏->drop to frame
	private void dropToFrame(){
		System.err.println("dropToFrame-start");
		stepInto();
		System.err.println("dropToFrame-end");
	}
	
	
	
	public static void main(String[] args) {
		DebugDemo dd = new DebugDemo();
		dd.attrWatchPoint = "b";
		
		dd.dropToFrame();
		
		dd.stepInto();
		
	}

}
