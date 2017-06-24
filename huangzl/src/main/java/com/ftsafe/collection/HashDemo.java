/**
 * 
 */
package com.ftsafe.collection;

import java.util.HashMap;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * hash是一个int值,对length取模,则可以定位:index = h & (length-1),其中length为2的n次方,length-1的2进制为n-1位1, h & (length-1)运算即保留h的后n-1位
 * 
 *
 */
public class HashDemo {
	
	public static void main(String[] args) {
		System.err.println("getenv:"+System.getenv("workid"));
		System.err.println("getenv:"+System.getenv("SYSTEM_WORK_ID"));
		
		String a = "xxxzz";
		System.err.println(a.hashCode());
		
		System.err.println(new HashDemo().hashCode());
		System.err.println(new Object().hashCode());
		
//		HashMap<K, V>
	}

}
