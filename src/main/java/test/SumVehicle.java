package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class SumVehicle {	// 统计车辆类型和对应的数量
	static String[] time = { "晴天（上午）\\", "晚上\\", "下午（阴天）\\", "雨天\\" };
	static String[] place = { "奥奇丽路口\\", "电脑城路口\\", "四中路口\\", "太阳广场路口\\", "文澜路路口\\" };
	static int sum; 			// 记录文件总数量
	static int error; 			// 记录空文件
	static int vehicle; 		// 识别出来车的数量
	static ArrayList<String> list = new ArrayList<String>();
	static int[] vehicleSum = new int[20];
	
	public static void main(String[] args) {

		String savePath = "E:\\Python\\PythonWorkspace\\天网7k截图json\\";

		for (int i = 0; i < time.length; i++) {
			for (int j = 0; j < place.length; j++) {
				System.out.println(savePath + time[i] + place[j]);
				sumVehicle(savePath + time[i] + place[j]);
			}
		}

		System.out.println("一共有" + sum + "个图片文件");
		System.out.println("其中有" + error + "个空文件或识别错误文件");
		System.out.println("一共识别出" + vehicle + "辆车");
		System.out.println("以下是车型类型数量");
		for(int i = 0; i < list.size(); i ++) {
			System.out.println(list.get(i) + "  有   " + vehicleSum[i] + "个");
		}
		int s = 0;
		for (int i : vehicleSum) {
			s += i;
		}
		System.out.println(s);
	}

	public static void sumVehicle(String path) {	// 统计
		File file = new File(path);			// 获得图片文件夹
		File[] files = file.listFiles();	// 图片集
		System.out.println(files.length);

		for (int i = 0; i < files.length; i++) {
			File temp = new File(path + files[i].getName()); // 构建图片路径并创建文件

			try {
				Scanner in = new Scanner(temp);
				System.out.println("扫描到第" + (++sum) + "个文件");
				System.out.println(files[i].getName());

				if(!in.hasNext()) {		// 文件为空直接暂时这次循环，进行下一次循环
					error++;
					continue;
				}
				
				StringBuilder sb = new StringBuilder(""); // 拼接内容
				while (in.hasNext()) {
					String s = in.next();
					sb.append(s);
				} 

				JSONObject jsonObject = new JSONObject(sb.toString());
				if (jsonObject.has("vehicleRecognitionTarget")) { // 有可能未识别出车辆, 不存在该属性
					
					JSONArray jsonArray = jsonObject.getJSONArray("vehicleRecognitionTarget");
					for (Object object : jsonArray) {
						vehicle++;								// 每有一辆车就自增1
						
						JSONObject t = new JSONObject(object.toString());
						if(list.contains(t.get("vehicleType"))) {   // 判断车是否存在，存在数量加1
							vehicleSum[list.indexOf(t.get("vehicleType"))] ++;
						} else {							// 不存在则加入列表中，数量初始化加1
							list.add((String)t.get("vehicleType"));
							vehicleSum[list.indexOf(t.get("vehicleType"))] ++;
						}
						
					}

				} else {
					error++;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

	}

}
