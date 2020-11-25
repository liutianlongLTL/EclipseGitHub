package test;

import java.io.File;

public class Sum {
	
	static String[] time = { "晴天（上午）\\", "晚上\\", "下午（阴天）\\", "雨天\\" };
	static String[] place = { "奥奇丽路口\\", "电脑城路口\\", "四中路口\\", "太阳广场路口\\", "文澜路路口\\" };
	
	public static void main(String[] args) {
		
		String Path = "E:\\Python\\PythonWorkspace\\天网7k截图\\";
		int sum = 0;
		for (int i = 0; i < time.length; i++) {
			for (int j = 0; j < place.length; j++) {
				System.out.println(Path + time[i] + place[j]);
				sum += f(Path + time[i] + place[j]);
			}
		}
		System.out.println(sum);
	}
	
	public static int f(String s) {
		File file = new File(s);
		File[] files = file.listFiles();
		return files.length;
	}
}
