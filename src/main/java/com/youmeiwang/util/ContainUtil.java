package com.youmeiwang.util;

import java.util.Arrays;

public class ContainUtil {

	public static boolean hasNumber(Integer[] numArr, Integer targetValue) {
		if (numArr == null) {
			return false;
		}
		return Arrays.asList(numArr).contains(targetValue);
    }
	
	public static boolean hasString(String[] strArr, String targetValue) {
		if (strArr == null) {
			return false;
		}
		return Arrays.asList(strArr).contains(targetValue);
    }
}
