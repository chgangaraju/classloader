package com.imaginea.classloader;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gangaraju
 * Date: 28/8/13
 */
public class Test {
    public static void main(String [] args) throws Exception {
       JarFileLoader loader=JarFileLoader.loadJars(new File("/home/gangaraju/jar/"));
       CCLRun.startApplication(loader,"com.imaginea.AwtExample");
System.out.print("hi");

    }
}
