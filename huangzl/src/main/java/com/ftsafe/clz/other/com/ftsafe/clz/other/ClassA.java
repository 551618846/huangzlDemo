package com.ftsafe.clz.other;
/**
 * 
 */


/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class ClassA {
	
	public static String getString(){
		System.err.println("ClassA1.getString() run:"+ClassA1.getString()+",getClassLoader:"+ClassA1.class.getClassLoader());
		return "xxxzzz";
	}

}
