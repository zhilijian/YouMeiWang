package com.youmeiwang.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ListUtil {

	public static <E> List<E> addElement(List<E> list, E element) {
		list.add(element);
		return list;
	}
	
	public static <E> List<E> addList(List<E> list1, List<E> list2){
		list1.removeAll(list2);
		list1.addAll(list2);
		return list1;
	}
	
	public static <E> List<E> removeElement(List<E> list, E element) {
		Iterator<E> iter = list.iterator();
        while (iter.hasNext()) {
        	E item = iter.next();
            if (item.equals(element)) {
                iter.remove();
            }
        }
		return list;
	}
	
	// 删除List中重复元素，保持顺序
	public static <E> List<E> removeDuplicate(List<E> list) {
		Set<E> set = new HashSet<E>();
		List<E> newList = new LinkedList<E>();
		for (E element : list) {
			if (set.add(element)) {
				newList.add(element);
			}
		}
		return newList;
	}   
}
