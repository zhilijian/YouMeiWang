package com.youmeiwang.util;

public class VerifyUtil {
	
	public static boolean isValidPhone(String phone) {
		String regex = "^(((13|14|15|18|17)\\d{9}))$";
		boolean isValid = phone.matches(regex);
		
		if (isValid) {
			return true;
		} else {
			return false;
		}
	}
	
}
