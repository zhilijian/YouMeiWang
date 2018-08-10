package com.youmeiwang.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListUtil {

	public static boolean isHasString(List<String> list, String str) {
		for (String string : list) {
			if (string.equals(str)) {
				return true;
			} 
		}
		return false;
	}
	
	public static List<String> addString(List<String> list, String str) {
		list.add(str);
		return list;
	}
	
	public static List<String> addString(List<String> list1, List<String> list2){
		list1.removeAll(list2);
		list1.addAll(list2);
		return list1;
	}
	
	public static List<String> removeString(List<String> list, String str) {
		Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
        	String item = iter.next();
            if (item.equals(str)) {
                iter.remove();
            }
        }
		return list;
	}
	
	public static boolean isHasMap(List<Map<String, Object>> maplist, Map<String, Object> map1) {
		for (Map<String, Object> map2 : maplist) {
			if (map2.equals(map1)) {
				return true;
			} 
		}
		return false;
	}
	
	public static List<Map<String, Object>> addMap(List<Map<String, Object>> list, Map<String, Object> map) {
		list.add(map);
		return list;
	}
	
	public static List<Map<String, Object>> removeMap(List<Map<String, Object>> list, Map<String, Object> map) {
		Iterator<Map<String, Object>> iter = list.iterator();
        while (iter.hasNext()) {
        	Map<String, Object> item = iter.next();
            if (item.equals(map)) {
                iter.remove();
            }
        }
		return list;
	}
	
}
