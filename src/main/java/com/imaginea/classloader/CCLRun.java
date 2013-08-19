package com.imaginea.classloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class CCLRun extends Thread {
	public void run() {
		try {
			System.out.println(CCLRun.class.getClassLoader());
			System.out.println(CCLRun.class.getClassLoader().getParent());
			String mainClass = this.getClass().getName();
			CompilingClassLoader ccl = new CompilingClassLoader();
			Class<?> clas = ccl.loadClass(mainClass);
			Class<?> mainArgType[] = { (new String[0]).getClass() };
			Method main = clas.getMethod("main", mainArgType);
			Object argsArray[] = { (Object) null };
			main.invoke(null, argsArray);
			System.out.println("completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {

		// while (true) {
		// Thread myThread = new CCLRun();
		// Thread.sleep(5000);
		// System.out.println("started");
		// myThread.start();
		// new Test();
		// Thread.sleep(5000);
		// Reflections reflections = new
		// Reflections("com.imaginea.classloader");
		Class<?>[] classes = getClasses("com.imaginea.classloader");
		for (int i = 0; i < classes.length; i++) {
			System.out.println(classes[i]);
		}
		// System.out.println("restarting...");
		// myThread.stop();
		// }

	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
