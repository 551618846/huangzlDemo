/**
 * 
 */
package com.ftsafe.clz;

import java.io.FileInputStream;
import java.lang.reflect.Method;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class Test {

	public static void main(String[] args) throws Exception {
		//一个运行的java程序(main方法)默认的classloader是sun.misc.Launcher$AppClassLoader
		//如果代码里创建自定义的classloader加载某个类,那么级联的类也使用该classloader,见下例MyClassLoader
		System.err.println("默认ClassLoader:"+Test.class.getClassLoader());
		
		//设置项目的build path: 
		//src\main\java\com\ftsafe\clz 编译到 \target\classes
		//\src\main\java\com\ftsafe\clz\other编译到 \target\other
		
		//jvm在运行时classpath下寻找类,即\target\classes下寻找,由于前面设置,这里找不到类com/ftsafe/clz/other/ClassA
		/*ClassA a1 = new ClassA();
		System.err.println(a1.getString());*/
		
		
		//MyClassLoader实现中指定寻找类的目录,\target\other.模拟一下tomcat的webclassloader
		MyClassLoader loder = new MyClassLoader();
		Class clzA = loder.loadClass("com.ftsafe.clz.other.ClassA");//
//		System.err.println(clzA.newInstance().getClass());//class com.ftsafe.clz.other.ClassA
		//自定义的classloader加载某个类,那么级联的类也使用该classloader
		System.err.println("MyClassLoader.loadClass(com.ftsafe.clz.other.ClassA):"+clzA.getClassLoader());//com.ftsafe.clz.MyClassLoader@72ebbf5c
		//调用方法
		Method m = clzA.getMethod("getString");
		Object rt = m.invoke(clzA.newInstance());
		System.err.println("反射调用方法返回值:"+rt);
		System.err.println("==============================");
		
		//非正常:TestClassLoader.myLoadByte加载,但是getClassLoader为TestClassLoader
		//TestClassLoader.loadClass加载,getClassLoader为sun.misc.Launcher$AppClassLoader
		TestClassLoader tloader = new TestClassLoader();
		FileInputStream is = new FileInputStream("F:\\workspace_ftsafe\\huangzl\\target\\other\\com\\ftsafe\\clz\\other\\ClassA.class");
		byte[] classByte = new byte[is.available()];
		is.read(classByte);
		Class z = tloader.myLoadByte(classByte);
		/***com.ftsafe.clz.TestClassLoader@5f0ee5b8*/
		System.err.println("tloader.myLoadByte(ClassA.class字节数组):"+z.getClassLoader());
		/**sun.misc.Launcher$AppClassLoader@7692ed85*/
		System.err.println("tloader.loadClass(com.ftsafe.clz.MyClassLoader):"+tloader.loadClass("com.ftsafe.clz.MyClassLoader").getClassLoader());
		System.err.println("tloader.loadClass(com.ftsafe.clz.ClassB):"+tloader.loadClass("com.ftsafe.clz.ClassB").getClassLoader());
		System.err.println("==============================");
		//调用方法,抛出异常,
		/*Method m1 = z.getMethod("getString");
		//java.lang.ClassLoader抛出异常:java.lang.ClassNotFoundException: com.ftsafe.clz.other.ClassA1
		//因为方法接收者使用com.ftsafe.clz.TestClassLoader,双亲委派模型sun.misc.Launcher$AppClassLoader,不寻找\target\other
		Object rt1 = m1.invoke(z.newInstance());
		System.err.println("反射调用方法返回值:"+rt1);*/
		
		//setContextClassLoader使父classloader可以调用子classloader,需要父classloader支持?
		Thread.currentThread().setContextClassLoader(loder);
	}

}
