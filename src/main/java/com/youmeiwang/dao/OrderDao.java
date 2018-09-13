package com.youmeiwang.dao;

import java.util.List;
import java.util.Map;

import com.youmeiwang.entity.Order;

public interface OrderDao {
	
	public void addOrder(Order order);
	
	public void removeOrder(String condition, Object value);
	
	public void updateOrder(Order order);
	
	public void setOrder(String condition, Object value1, String target, Object value2);

	public Order queryOrder(String condition, Object value);
	
	public List<Order> orderList(Integer searchType, String condition, String value, Map<String, Object> conditions, Integer page, Integer size);
	
	public List<Order> orderList(List<Map<String, Object>> conditions, Integer page, Integer size);
	
	public List<Order> orderlist(String condition, Integer payType, String payStatus, Long startTime, Long endTime);
}
