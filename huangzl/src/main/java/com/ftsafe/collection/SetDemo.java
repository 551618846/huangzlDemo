/**
 * 
 */
package com.ftsafe.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class SetDemo {
	//注：如果将可变对象用作 set 元素，那么必须极其小心。如果对象是 set 中某个元素，以一种影响 equals 比较的方式改变对象的值，那么 set 的行为就是不确定的。此项禁止的一个特殊情况是不允许某个 set 包含其自身作为元素。
	
	/**Set主要接口:retainAll交集,contains包含,add,remove,iterator,没有get
	*/
	/***********基本set实现HashSet,LinkedHashSet******************/
	//使用hashMap实现.add,remove,contains,size性能不错.避免rehash.无序
	static HashSet<String> set = new HashSet<String>();

	//迭代顺序按插入顺序,使用LinkedHashMap实现,其他同HashSet//维护着一个运行于所有条目的双重链接列表
	static LinkedHashSet<String> lhs = new LinkedHashSet<String>();
	
	
	/*******有序(SortedSet,NavigableSet)实现:TreeSet**********************/
	//底层TreeMap(NavigableMap)实现,有序,log(n) time cost for the basic operations (add, remove and contains). 
	//SortedSet操作增加了:first,last,subSet,tailSet,headSet
	//NavigableSet操作增加了:higher,lower,headSet,tailSet,subSet等
	//set 维护的顺序（无论是否提供了显式比较器）必须与 equals 一致。即元素的equals方法要和Comparable实现逻辑一致，否则以Comparable顺序判断是否元素相同
	static TreeSet<String> ts = new TreeSet<String>();
	
	/******线程安全版,无新接口*****************************/
	//log(n) time cost for the containsKey, get, put and remove 
	// NavigableSet,SortedSet,即类似TreeSet,线程安全版
	static ConcurrentSkipListSet<String> css = new ConcurrentSkipListSet<String>();
	
	//线程安全,set大小通常保持很小，只读操作远多于可变操作，需要在遍历期间防止线程间的冲突
	//复制整个基础数组，所以可变操作（add、set 和 remove 等等）的开销很大
	//迭代器不支持可变 remove 操作
	//底层CopyOnWriteArrayList
	static CopyOnWriteArraySet<String> cows = new CopyOnWriteArraySet<String>();
	
	
	public static void sort(List<vo> list){
		Collections.sort(list, new Comparator<vo>() {

			public int compare(vo o1, vo o2) {
				if(o1.updateTime == o2.updateTime){
					return 0;
				}
				return o1.updateTime>o2.updateTime?1:-1;
			}
			
		});
	}
	
	
	
	
	public static void main(String[] args) {
		TreeSet<vo> tree = new TreeSet<vo>(new Comparator<vo>() {

			public int compare(vo o1, vo o2) {
				if(o1.updateTime == o2.updateTime){
					return 0;
				}
				return o1.updateTime>o2.updateTime?1:-1;
			}
			
		});
		vo v1 = new vo("v1");
		tree.add(v1);
		vo v2 = new vo("v2");
		tree.add(v2);
		vo v3= new vo("v3");
		tree.add(v3);
		System.err.println("TreeSet:"+tree);//按顺序
		
		//////////////////
		vo vl = tree.first();
		vl.update();
		System.err.println(vl);
		
		List<vo> list = new ArrayList<vo>(tree);
		Collections.sort(list, new Comparator<vo>() {

			public int compare(vo o1, vo o2) {
				if(o1.updateTime == o2.updateTime){
					return 0;
				}
				return o1.updateTime>o2.updateTime?1:-1;
			}
			
		});
		
		System.err.println("Collections.sort list:"+list);//按顺序
		////////////////////////////
		
		
		vo v = tree.first();
		v.update();
		System.err.println(v);
		System.err.println("000000000=="+v.equals(v1));
		
		tree.add(v);//!!TreeSet使用Comparable判断元素是的相同（本来是equals）：TreeSet:[v1, v2, v3, v1]
		System.err.println("TreeSet:"+tree);//按顺序
		
		
		
		
		
		set.add("x");
		set.add("c");
		set.add("v");
		System.err.println("HashSet:"+set);//无序
		
		lhs.add("a");
		lhs.add("s");
		lhs.add("d");
		System.err.println("LinkedHashSet:"+lhs);//按插入顺序
		lhs.clear();
		lhs.addAll(set);//按原set顺序
		System.err.println("LinkedHashSet:"+lhs);
		
		ts.add("x");
		ts.add("d");
		ts.add("f");
		System.err.println("TreeSet:"+ts);//按顺序
	}

}



class vo {//implements Comparable<vo>{
	long updateTime;
	String name;
	public vo(String name) {
		this.updateTime=System.nanoTime();
		this.name=name;
	}
	
	public void update(){
		this.updateTime=System.nanoTime();
	}
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof vo){
			System.err.println(name+"------------"+((vo) obj).name);
			return name.equals(((vo) obj).name);
		}
		System.err.println("=======");
		return super.equals(obj);
	}
	
//	public int compareTo(vo o) {
//		if(o.updateTime == this.updateTime){
//			return 0;
//		}
//		return this.updateTime>o.updateTime?1:-1;
//	}
}
