package com.youmeiwang.dao;

import com.youmeiwang.entity.Order;

public interface OrderDao {
	
	public void addOrder(Order order);
	
	public void removeOrder(String condition, Object value);
	
	public void updateOrder(Order order);
	
	public void setOrder(String condition, Object value1, String target, Object value2);

	public Order queryOrder(String condition, Object value);
}
