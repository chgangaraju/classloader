package com.imaginea.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CompilingClassLoader extends ClassLoader {

	private byte[] getBytes(String filename) throws IOException {
		File file = new File(filename);
		long len = file.length();
		byte raw[] = new byte[(int) len];
		FileInputStream fin = new FileInputStream(file);
		int r = fin.read(raw);
		fin.close();
		if (r != len)
			throw new IOException("Can't read all, " + r + " != " + len);
		return raw;
	}

	private boolean compile(String javaFile) throws IOException {
		System.out.println("CCL: Compiling " + javaFile + "...");
		Process p = Runtime.getRuntime().exec("javac " + javaFile);
		try {
			p.waitFor();
		} catch (InterruptedException ie) {
			System.out.println(ie);
		}
		int ret = p.exitValue();
		return ret == 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class clas = null;
		clas = findLoadedClass(name);
		String fileStub = name.replace('.', '/');
		String javaFilename = fileStub + ".java";
		String classFilename = fileStub + ".class";
		File javaFile = new File(javaFilename);
		File classFile = new File(classFilename);
		if (javaFile.exists()
				&& (!classFile.exists() || javaFile.lastModified() > classFile
						.lastModified())) {
			try {
				if (!compile(javaFilename) || !classFile.exists()) {
					throw new ClassNotFoundException("Compile failed: "
							+ javaFilename);
				}
			} catch (IOException ie) {
				throw new ClassNotFoundException(ie.toString());
			}
		}
		try {
			byte raw[] = getBytes(classFilename);
			clas = defineClass(name, raw, 0, raw.length);
		} catch (IOException ie) {

		}
		if (clas == null) {
			clas = findSystemClass(name);
		}
		if (resolve && clas != null)
			resolveClass(clas);
		if (clas == null)
			throw new ClassNotFoundException(name);
		return clas;
	}
}
