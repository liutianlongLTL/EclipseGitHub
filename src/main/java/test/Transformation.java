package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;


public class Transformation {
	
	static Map<String, String> map = new HashMap<String, String>();
	
	static int width = 1920, height = 1126;			// 统一分辨率根据图片情况变化
	//static int width = 1920, height = 1080;			// 统一分辨率根据图片情况变化
	static int number = 34827;						// 文件起始标志
	
	public static void main(String[] args) {
		initMap(); 									// 程序开始前就初始化map
		
		String jsonbasePath = "E:\\Python\\PythonWorkspace\\天网7k截图json\\雨天\\文澜路路口";
		String xmlPath = "E:\\Python\\PythonWorkspace\\天网7k截图xml\\雨天\\文澜路路口";
		String imagePath = "E:\\Python\\PythonWorkspace\\天网7k截图缩放改名\\雨天\\文澜路路口";
		
		File jsonPathFile = new File(jsonbasePath);
		File[] jsonFiles = jsonPathFile.listFiles();
		int sum = 0;												// 统计成功转换的个数
		for(File jsonfile : jsonFiles) {
			String jsonName = jsonfile.getName();					// 获得json文件名字
			List<Vehicle> list = listVehicle(jsonfile.getPath());	// 传入json文件路径，获得json车辆对象
			list = transformation(list);							// 将json车辆对象转换成xml车辆对象
			
			if(list.size() == 0) continue;							// 如果xml车辆个数为0，就暂停这次循环
			
			String content = getContent(list);
			
			if(saveXml(jsonName, content, xmlPath, imagePath)) {	// 成功就加1，失败就输出信息
				sum ++;
			} else {
				System.out.println("转换失败");
			}
		}
		System.out.println(number);
		System.out.println(sum);
	}
	
	/**
	 * 	初始化map用于平台和人工之间的车辆类型转换
	 */
	public static void initMap() {	// 初始化map，k为平台车型，v为人工车型
		map.put("largeBus", "Bus");					// 大型客车		公共汽车  
		map.put("truck", "Minivan");				// 货车			小型货车			
		map.put("vehicle", "Sedan");				// 轿车			轿车
		map.put("van", "Microbus");					// 面包车		面包车（微型客车）
		map.put("buggy", "Minivan");				// 小货车		小型货车
		map.put("twoWheelVehicle", "motorcycle");	// 二轮车		摩托车,指2轮摩托
		map.put("threeWheelVehicle", "tricycle");	// 三轮车		三轮车
		map.put("SUVMPV", "SUV");					// SUV/MPV		越野车
		map.put("mediumBus", "Microbus");			// 中型客车		面包车（微型客车）
		map.put("motorVehicle", "Sedan");			// 机动车		轿车
		map.put("nonmotorVehicle", "bicycle");		// 非机动车		自行车
		map.put("smallCar", "Sedan");				// 小型轿车		轿车
		map.put("miniCar", "Sedan");				// 微型轿车		轿车
		map.put("pickupTruck", "Truck");			// 皮卡车		卡车
	}
	
	/**
	 * 提取json文件里的车辆信息
	 * @param jsonPath	json文件完整路径
	 * @return
	 */
	public static List<Vehicle> listVehicle(String jsonPath){
		List<Vehicle> list = new LinkedList<Vehicle>();
		File jsonFile = new File(jsonPath);
		
		StringBuilder info = new StringBuilder("");			// 保存文件内容
		try {												// 读取json文件内容
			Scanner in = new Scanner(jsonFile);
			if(!in.hasNext()) return list;					// 如果文件是空的，直接返回
			
			while(in.hasNext()) info.append(in.next());		// 拼接json文件的内容
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject = new JSONObject(info.toString());
		if(jsonObject.has("vehicleRecognitionTarget")) {		// 包含该字符串表示json文件内容正常
			JSONArray jsonArray = jsonObject.getJSONArray("vehicleRecognitionTarget");
			
			for(int i = 0; i < jsonArray.length(); i ++) {		// 遍历json数组
				JSONObject vehicle = jsonArray.getJSONObject(i);// 将每个json数组都转成jsonObject
				String vehicleType = vehicle.getString("vehicleType"); // 获得车辆类型
				
				JSONObject Rect = vehicle.getJSONObject("Rect");// 获得矩阵对象
				double xmin = Rect.getDouble("x");				// 获得左上角x坐标
				double ymin = Rect.getDouble("y");				// 获得左上角y坐标
				double xmax = xmin + Rect.getDouble("width");	// 计算右下角x坐标
				double ymax = ymin + Rect.getDouble("height");	// 计算右下角y坐标
				
				Vehicle v = new Vehicle();						// 开始存放入list中
				v.setVehicleType(vehicleType);
				v.setXmin(xmin);
				v.setYmin(ymin);
				v.setXmax(xmax);
				v.setYmax(ymax);
				list.add(v);
			}
		}
		
		return list;
	}
	
	/**
	 * 	将json的车辆信息 转换成 xml文件对应的车辆信息
	 * @param list 	json车辆信息
	 * @return
	 */
	public static List<Vehicle> transformation(List<Vehicle> list){
		List<Vehicle> res = new LinkedList<Vehicle>();
		
		for(Vehicle vehicle : list) {
			if(map.containsKey(vehicle.getVehicleType())) { // 如果车型包含在map中就加入res中
				Vehicle v = new Vehicle();
				v.setVehicleType(map.get(vehicle.getVehicleType()));
				v.setXmin(vehicle.getXmin() * width);
				v.setYmin(vehicle.getYmin() * height);
				v.setXmax(vehicle.getXmax() * width);
				v.setYmax(vehicle.getYmax() * height);
				res.add(v);
			}
		}
		
		return res;
	}
 
	/**
	 *	将xml文件车辆信息转换成xml文件内容
	 * @param list
	 * @return
	 */
	public static String getContent(List<Vehicle> list) {
		StringBuilder content = new StringBuilder();
		String FilePath = "/home/wzxy/wzgacv/mmdetection/data/voc/voc2007_bit_vehicle_all/Annotations";
		String FileName = "vehicle_" + number;
		
		content.append("<annotation>" + "\n");
		
		content.append("\t<folder>" + FilePath + "</folder>" + "\n");
		content.append("\t<filename>"+ FileName +"</filename>" + "\n");		// 文件名称
		content.append("\t<path>" + FilePath + "/" + FileName + ".jpg" + "</path>" + "\n");	// 图片路径
		content.append("\t<source>" + "\n");
		content.append("\t\t<database>Unknown</database>" + "\n");
		content.append("\t</source>" + "\n");
		
		content.append("\t<size>" + "\n");
		content.append("\t\t<width>" + width + "</width>" +"\n");
		content.append("\t\t<height>" + height + "</height>" +"\n");
		content.append("\t\t<depth>3</depth>" +"\n");
		content.append("\t</size>" + "\n");
		content.append("\t<segmented>0</segmented>" + "\n");
		
		for(Vehicle vehicle : list) {
			content.append("\t<object>" + "\n");				// 添加车辆信息开始标志
			content.append("\t\t<name>" + vehicle.getVehicleType() + "</name>\n");	// 添加名称
			content.append("\t\t<pose>Unspecified</pose>\n");
			content.append("\t\t<truncated>0</truncated>\n");
			content.append("\t\t<difficult>0</difficult>\n");
			content.append("\t\t<bndbox>\n");					// 开始添加坐标
			content.append("\t\t\t<xmin>" + (int)vehicle.getXmin() + "</xmin>\n");
			content.append("\t\t\t<ymin>" + (int)vehicle.getYmin() + "</ymin>\n");
			content.append("\t\t\t<xmax>" + (int)vehicle.getXmax() + "</xmax>\n");
			content.append("\t\t\t<ymax>" + (int)vehicle.getYmax() + "</ymax>\n");
			content.append("\t\t</bndbox>\n");
			content.append("\t</object>" + "\n");				// 添加车辆信息结束标志
		}
		
		content.append("</annotation>" + "\n");
		
		return content.toString();
	}
	
	/**
	 * 	新建一个xml文件，将content内容保存到xml文件中，同时修改原图的名字
	 * @param jsonName	json文件名
	 * @param content	xml内容
	 * @param xmlPath	xml所要存放的文件夹
	 * @param imagePath json文件对应的原图片文件夹
	 * @return
	 */
	public static boolean saveXml(String jsonName, String content, String xmlPath, String imagePath) {
		String finalName = "vehicle_" + number;				// 定义最终统一的名字
		
		String imageOriginalName = jsonName.substring(0, jsonName.length() - 5); 
									// 获得图片的原名，去掉    .json   ，所以去掉5个字符
		String imageRename = finalName + ".jpg";			// 修改后的名字
		
		if(rename(imagePath, imageOriginalName, imageRename)) {	// 如果图片重命名成功，就新建xml文件
			String xmlName = finalName + ".xml";				// 定义xml文件名字
			File xmlFile = new File(xmlPath + "\\" + xmlName);	// 用两个反斜杠隔开，构建xml文件路径
			if(xmlFile.exists()) xmlFile.delete();				// 如果文件已经存在就删除
			try {
				PrintWriter pw = new PrintWriter(xmlFile);
				pw.print(content);								// 写入数据
				pw.flush();
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else return false;									// 重命名失败就直接返回false
		
		number ++;					// 该程序结束后，需要自增1，用于下一个文件
		return true;
	}
	
	/**
	 * 修改名字，将imagePath文件夹下的原名为imageOriginalName的图片改为imageRename
	 * @param imagePath
	 * @param imageOriginalName
	 * @param imageRename
	 */
	public static boolean rename(String imagePath, String imageOriginalName, String imageRename) {
		File f1 = new File(imagePath + "\\" + imageOriginalName);
		File f2 = new File(imagePath + "\\" + imageRename);
		return f1.renameTo(f2);
	}
	
}

class Vehicle{
	String vehicleType;			// 车辆类型
	double xmin;				// 左上角横坐标
	double ymin;				// 左上角纵坐标
	double xmax;				// 右下角横坐标
	double ymax;				// 右下角纵坐标
	
	@Override
	public String toString() {
		return "Vehicle [vehicleType=" + vehicleType + ", xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax
				+ ", ymax=" + ymax + "]";
	}
	
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public double getXmin() {
		return xmin;
	}
	public void setXmin(double xmin) {
		this.xmin = xmin;
	}
	public double getYmin() {
		return ymin;
	}
	public void setYmin(double ymin) {
		this.ymin = ymin;
	}
	public double getXmax() {
		return xmax;
	}
	public void setXmax(double xmax) {
		this.xmax = xmax;
	}
	public double getYmax() {
		return ymax;
	}
	public void setYmax(double ymax) {
		this.ymax = ymax;
	}
	
}