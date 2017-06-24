package com.ftsafe.clz;


public class TestFinally {

	
	public static void main(String[] args) throws Throwable {
		System.err.println(test());

	}
	
	static int test() throws Throwable{
		int i = 0;
		try {
			i = 2;
			if(true){
				throw new Exception();
//				throw new Throwable();
			}
			return i;
		} catch (Exception e) {
			i = 4;
			System.err.println("catch:" + i);
			return i;
		}finally{
			i=8;
			System.err.println("finally:" + i);
		}
	}

}
