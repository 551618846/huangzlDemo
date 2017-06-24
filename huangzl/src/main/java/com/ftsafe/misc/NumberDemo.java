/**
 * 
 */
package com.ftsafe.misc;

import java.math.BigDecimal;


/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 * long最大值2^63-1,Long.MAX_VALUE
 * float的指数范围为-128~+127，而double的指数范围为-1024~+1023
 * float,1bit（符号位） 8bits（指数位） 23bits（尾数位）,最大值2^127
 * double,1bit（符号位） 11bits（指数位） 52bits（尾数位）,最大值2^1023
 * 2的64次方:递归实现,double保存,long溢出
 */
public class NumberDemo {

	public static void main(String[] args) {
		
		double d_2_62 = powerOf2(62);
		System.err.println(d_2_62);;
		System.err.println(new Double(d_2_62).longValue());;
		//2的64次方
		double d_2_64 = powerOf2(64);
		System.err.println(d_2_64);;
		System.err.println(new Double(d_2_64).toString());;
		System.err.printf("%f",new Double(d_2_64));
		System.err.println();;
		System.err.println("BigDecimal:"+new BigDecimal(d_2_64));
		
//		long r = 2<<61;
//		System.err.println((long)r);
		
		long one = 1L;
		System.err.println(one <<32);
		
		long _2_62 = 4611686018427387904L;
		System.err.println(one <<62);//4611686018427387904L
		
		System.err.println(_2_62*2);
		System.err.println(Long.MAX_VALUE);//2<sup>63</sup>-1.
		//final long MAX_VALUE = 0x7fffffffffffffffL;
		
		//float：
        //1bit（符号位） 8bits（指数位） 23bits（尾数位）
		//double：
        //1bit（符号位） 11bits（指数位） 52bits（尾数位）
		//float的指数范围为-128~+127，而double的指数范围为-1024~+1023，并且指数位是按补码的形式来划分的。
		//其中负指数决定了浮点数所能表达的绝对值最小的非零数；而正指数决定了浮点数所能表达的绝对值最大的数，也即决定了浮点数的取值范围。
		//float的范围为-2^128 ~ +2^127，也即-3.40E+38 ~ +3.40E+38；double的范围为-2^1024 ~ +2^1023，也即-1.79E+308 ~ +1.79E+308。
		
		
		double _1 = 1.0;
		System.err.println(Double.MAX_VALUE );
//		System.err.println(_1<<64);//编译错误The operator << is undefined for the argument type(s) double, int
		System.err.println(_1 * Long.MAX_VALUE);
	}
	
	
	private static double powerOf2(double d){
		if(d==1){
			return 2;
		}else{
			return powerOf2(d-1)*2;
		}
	}
	
	

}
