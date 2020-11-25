package test;

import java.io.File;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Check {	// 统计json文件中有多少辆车
	static String[] time = { "晴天（上午）\\", "晚上\\", "下午（阴天）\\", "雨天\\" };
	static String[] place = { "奥奇丽路口\\", "电脑城路口\\", "四中路口\\", "太阳广场路口\\", "文澜路路口\\" };
	static int[] sum = new int[30];
	static int cnt = 0;				// 显示扫描到第几个文件
	
	public static void main(String[] args) {
		String savePath = "E:\\Python\\PythonWorkspace\\天网7k截图json\\";
		
		for(int i = 0; i < time.length; i ++) {
			for(int j = 0; j < place.length; j ++) {
				String t = savePath + time[i] + place[j];
				System.out.println(t);
				check(t);
			}
		}
		System.out.println("扫描结束");
		for(int i = 0; i < sum.length; i ++) System.out.print(sum[i] + " ");
	}
	
	public static void check(String s) {
		File file = new File(s);				// json总文件
		File[] files = file.listFiles();		// 获得下面的子文件
		
		for(int i = 0; i < files.length; i ++) {
			File jsonFile = new File(s + files[i].getName());	// 获得json文件
			
			try {
				Scanner in = new Scanner(jsonFile);// 扫描json文件
				if(!in.hasNext()) continue;		// 文件是空，暂停本次循环
				
				StringBuilder sb = new StringBuilder("");
				while(in.hasNext()) {			// 读取内容
					sb.append(in.next());
				}
				
				JSONObject jsonObject = new JSONObject(sb.toString());
				if(jsonObject.has("vehicleRecognitionTarget")) {// 包含该String表示没有错误
					JSONArray jsonArray = jsonObject.getJSONArray("vehicleRecognitionTarget");
					sum[jsonArray.length()] ++;
					if(jsonArray.length() == 10) {
						System.out.println(jsonFile.getPath());
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
