/**
 * 
 */
package com.ftsafe.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class ListDemo {
	
	static ArrayList<String> al = new ArrayList<String>();
	
	static LinkedList<String> ll = new LinkedList<String>();
	
	static CopyOnWriteArrayList<String> cowal = new CopyOnWriteArrayList<String>();
	
	public static void main(String[] args) {
		al.add("z");
		al.add("c");
		al.add("v");
		System.err.println(al);
		
		
	}
	
	

}
