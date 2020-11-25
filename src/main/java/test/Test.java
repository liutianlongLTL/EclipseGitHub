package test;

import java.io.File;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		File f1 = new File("E:\\Python\\PythonWorkspace\\json转换xml测试\\111111111111.jpg");
		File f2 = new File("E:\\Python\\PythonWorkspace\\json转换xml测试\\222222222222.jpg");
		System.out.println(f1.getAbsolutePath());
		try {
			System.out.println(f1.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(f1.getPath());
		System.out.println(f1.toPath());
		System.out.println(f1.pathSeparator);
		System.out.println(f1.pathSeparatorChar);
	} 
}
