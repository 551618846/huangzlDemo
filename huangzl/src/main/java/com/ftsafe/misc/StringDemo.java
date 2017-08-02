/**
 * 
 */
package com.ftsafe.misc;


/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class StringDemo {

	
	public static void main(String[] args) throws Exception {
//		charUseByte();
		ipTotalCount();
	}
	
	public static void ipTotalCount() throws Exception{
		//ip个数:256*256*256*256=2的32次方,42亿多
		System.err.println(256*256*256*256L);//4294967296
		//一个ip地址,用字符串保存,需要字节:xxx.xxx.xxx.xxx,java英文字符使用一个字节,总15个字节
		System.err.println("xxx.xxx.xxx.xxx".getBytes().length);
		//全部ip需要字节
		System.err.println(15*256*256*256*256L);//全部ip字符串需要字节64424509440,644亿多字节
		//内存,1G=1024*1024*1024
		System.err.println(1024*1024*1024L);//内存,1G=1 073 741 824字节,10亿多字节
		//所以需要...g内存才能存所有ip地址
		//可以把ip地址hash为整数,需要4个字节,160亿多字节,需要16G
		/**一个20G的文件,保存ip,要求找出出现次数最多的10个ip,硬件限制,2m内存,其他不限**/
		
		//有一个1G大小的一个文件，里面每一行是一个词，词的大小不超过16个字节，内存限制大小是1M。返回频数最高的100个词。
		//16个字节,
		System.err.println(16*8*2L);
		System.err.println();
		
	}
	
	public static void charUseByte() throws Exception{
		String s = "z";//utf-8使用1个字节,iso-8859-1使用一个字节
//		String s = "中";//utf-8使用3个字节,iso-8859-1使用1个字节
		System.err.println(s.getBytes("utf-8").length);
		System.err.println(s.getBytes("iso-8859-1").length);
	}
	
	
	
}
