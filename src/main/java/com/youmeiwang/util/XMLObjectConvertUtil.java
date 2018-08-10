package com.youmeiwang.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLObjectConvertUtil {

	/**
	 * 将String类型的xml转换成Map
	 */
	public static TreeMap<String, String> praseXMLToMap(String xml) {
		TreeMap<String, String> map = null;
		try {
			map = new TreeMap<String, String>();
			SAXReader reader = new SAXReader();
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
			Document document = reader.read(inputStream);
			// 获得所有的节点
			Element root = document.getRootElement();
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = root.elementIterator(); iterator.hasNext();) {
				Element elm = (Element) iterator.next();
				// 将节点名称与节点内容放进map集合中
				map.put(elm.getName(), elm.getText());
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
	}

	/**
	 * 将Map转换成String类型的xml
	 */
	public static String praseMapToXML(Map<String, String> map) {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		// 创建根目录
		Element root = document.addElement("xml");
		// 遍历所有的key
		for (String key : map.keySet()) {
			// 获得key所对应的value
			String value = map.get(key);
			if (value == null) {
				value = "";
			}
			// 去掉空格
			value = value.trim();
			// 创建子节点
			Element filed = root.addElement(key);
			// 将key所对应的value放入node中
			filed.setText(value);
		}
		String xml = document.asXML();
		return xml;
	}
}
