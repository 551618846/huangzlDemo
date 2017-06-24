/**
 * 
 */
package com.ftsafe.clz;

import java.io.FileInputStream;

/**
 * @author <a href=mailto: zhenliang@ftsafe.com>zhenliang</a>
 *
 */
public class MyClassLoader extends ClassLoader {
	
	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
//			System.err.println(name);
			String fileName = name.substring(name.lastIndexOf(".") + 1)+".class";
//			System.err.println(fileName);
			
			
			FileInputStream is = new FileInputStream("F:\\workspace_ftsafe\\huangzl\\target\\other\\com\\ftsafe\\clz\\other\\"+fileName);
			
			byte[] b = new byte[is.available()];
			is.read(b);
			
//			System.err.println(b.length);
//			System.err.println(new String(b));
			return defineClass(name, b, 0, b.length);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassNotFoundException(name);
		}

//		return super.findClass(name);
	}

}
