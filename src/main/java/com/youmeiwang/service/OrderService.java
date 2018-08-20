package com.youmeiwang.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.OrderDao;
import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.Work;

@Service
public class OrderService {

	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private OrderDao orderDao;
	
	public Order createOrder(String userID, String workID, Integer money, String payType) {
		Order order = new Order();
		order.setOutTradeNo(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + userID + UUID.randomUUID().toString().substring(22, 32));
		order.setUserID(userID);
		order.setProductID(workID);
		order.setTotalFee(money);
		order.setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		order.setPayType(payType);
		order.setPayStatus("NOTPAY");
		Work work = workDao.queryWork("workID", workID);
		if (work == null && money != null) {
			order.setBody("充值");
			order.setTotalFee(money);
		} else {
			order.setBody(work.getWorkName());
			order.setTotalFee(work.getPrice());
		}
		orderDao.addOrder(order);
		return order;
	}
	
	public void removeOrder(String condition, Object value) {
		orderDao.removeOrder(condition, value);
	}
	
	public void updateOrder(Order order) {
		orderDao.updateOrder(order);
	}
	
	public void setOrder(String condition, Object value1, String target, Object value2) {
		orderDao.setOrder(condition, value1, target, value2);
	}

	public Order queryOrder(String condition, Object value) {
		return orderDao.queryOrder(condition, value);
	}
}
