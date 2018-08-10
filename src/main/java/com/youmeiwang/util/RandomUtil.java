package com.youmeiwang.util;

import java.util.Random;

public class RandomUtil {
	
	public static String getRandomNumber(int num) {
		StringBuilder str = new StringBuilder();//定义变长字符串
		Random random = new Random();
		//随机生成数字，并添加到字符串
		for(int i = 0; i < num; i++){
			str.append(random.nextInt(10));
		}
		return str.toString();
	}
	
	public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i = 0; i < length; i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
}
