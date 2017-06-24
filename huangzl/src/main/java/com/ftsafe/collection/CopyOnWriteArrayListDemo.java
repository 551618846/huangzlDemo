/**
 * 
 */
package com.ftsafe.collection;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * 先行发生:
 *
 */
public class CopyOnWriteArrayListDemo {
	
	public static void main(String[] args) {
		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
		
		list.addIfAbsent(null);
		
		
	}

}
