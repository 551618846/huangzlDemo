/**
 * 
 */
package com.ftsafe.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * 
 * 普通map:HashMap
 * 排序:SortedMap接口,实现ConcurrentSkipListMap, TreeMap
 * 搜索:NavigableMap(扩展SortedMap)接口,实现ConcurrentSkipListMap, TreeMap
 * 并发:ConcurrentMap,实现ConcurrentHashMap, ConcurrentSkipListMap
 *
 */
public class MapDemo {
	
	//初始容量,加载因子,容量,实际大小//加载因子越大,填满的元素越多,空间利用率高了,但:冲突的机会加大了
	
	/**********基本实现HashMap,LinkedHashMap*****************/
	//hash实现,基本操作（get 和 put）提供稳定的性能,两个参数影响其性能：初始容量 和加载因子,避免rehash
	static Map<String, String> hash = new HashMap<String, String>(8, 0.75F);
	
	//对比hashmap,key的迭代顺序按插入顺序排序,或按访问顺序排序//其性能很可能比 HashMap稍逊一筹,除了迭代//维护着一个运行于所有条目的双重链接列表
	static Map<String, String> lh = new LinkedHashMap<String, String>(8, 0.75F);
	static Map<String, String> lhm = new LinkedHashMap<String, String>(8, 0.75F, true);//按访问顺序排序
	
	/**********有序(SortedMap,NavigableMap)实现TreeMap*****************/
	//红黑树A Red-Black tree based {@link NavigableMap} implementation.
	//key有序(默认自然序, 或指定Comparator)
	//实现SortedMap接口,接口增加:firstKey,lastKey,headMap,tailMap,subMap之类
	//实现NavigableMap接口(继承SortedMap),接口增加:lowerKey/Entry,higherKey/Entry,headMap,tailMap,subMap之类
	// containsKey、get、put 和 remove 操作提供受保证的 log(n) 
	static TreeMap<String, String> tree = new TreeMap<String, String>();
	
	/************线程安全版ConcurrentHashMap,ConcurrentSkipListMap*******************
	**实现ConcurrentMap接口,接口增加:putIfAbsent(K key, V value)。remove(Object key, Object value) 。replace(K key, V value)。 replace(K key, V oldValue, V newValue) 
	*/
	//hashmap的线程安全版,concurrencyLevel控制无争用并发更新的数量.注意rehash
	ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<String, String>();
	
	//NavigableMap, SortedMap,即类似treemap,线程安全版
	//containsKey、get、put、remove 操作及其变体提供预期平均 log(n) 时间开销
	static ConcurrentSkipListMap<String, String> crtNavigable = new ConcurrentSkipListMap<String, String>();
	
	
	WeakHashMap<String, String> weak = new WeakHashMap<String, String>();

	static String key = "b";
	static String value = "bbb";
	
	static{
		hash.put(key, value);
		hash.put("a", "aaa");
		hash.put("c", "ccc");
		hash.put("z", "zzz");
		hash.put("1", "111");
		
		System.err.println("hashMap:"+hash);
	}
	
	
	public static void main(String[] args) {
//		tree();
		naviga();
		
	}
	
	
	
	//搜索
	private static void naviga(){
		
		//NavigableMap
		NavigableMap<String, String> naviga = new TreeMap<String, String>();
		naviga.putAll(hash);
		System.err.println("NavigableMap:"+naviga);
		System.err.println("key:"+key);
		System.err.println("ceilingKey:"+naviga.ceilingKey(key));
		System.err.println("higherKey:"+naviga.higherKey(key));
		
		System.err.println("floorKey:"+naviga.floorKey(key));
		System.err.println("lowerKey:"+naviga.lowerKey(key));
		
		System.err.println("headMap:"+naviga.headMap(key, true));
		
		System.err.println("tailMap:"+naviga.tailMap(key, true));
		
		System.err.println("subMap:"+naviga.subMap(key, key));
		
	}
	
	//排序
	private static void tree(){
		
		//红黑树A Red-Black tree based {@link NavigableMap} implementation.
		TreeMap<String, String> tree = new TreeMap<String, String>();
		tree.putAll(hash);
		System.err.println("treeMap:"+tree);
		System.err.println("key:"+key);
		System.err.println("treeMap keySet:"+tree.keySet());
		
//		System.err.println("ceilingKey:"+tree.ceilingKey(key));
//		System.err.println("higherKey:"+tree.higherKey(key));
//		
//		System.err.println("floorKey:"+tree.floorKey(key));
//		System.err.println("lowerKey:"+tree.lowerKey(key));
//		
//		System.err.println("headMap:"+tree.headMap(key, true));
//		System.err.println("tailMap:"+tree.tailMap(key, true));
//		System.err.println("subMap:"+tree.subMap(key, key));
	}
	

}
