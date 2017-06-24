/**
 * 
 */
package com.ftsafe.clz;


/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class TestClassLoader extends ClassLoader {
	//本加载器符合双亲委派模型
	//自定义加载器的作用之一:多次创建类加载器,则可以重新加载某个类,相当于hotswap
	
	//myLoadByte返回的Class执行getClassLoader为TestClassLoader
	//特殊使用方式,直接加载class文件的字节数组,有可能当前运行时的默认加载器没有该class文件的加载权限,导致依赖类无法加载
	public Class myLoadByte(byte[] classByte){
		return defineClass(null, classByte, 0, classByte.length);
	}

}
