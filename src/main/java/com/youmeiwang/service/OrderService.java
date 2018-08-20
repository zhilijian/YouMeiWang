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
		SimpleDateFormat format = new  SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		order.setOutTradeNo(format.format(date) + userID + UUID.randomUUID().toString().substring(22, 32));
		order.setUserID(userID);
		order.setProductID(workID);
		order.setTotalFee(money);
		order.setStartTime(System.currentTimeMillis());
		order.setPayType(payType);
		order.setPayStatus("WAIT_BUYER_PAY");
		Work work = workDao.queryWork("workID", workID);
		if (work == null) {
			order.setBody("充值");
		} else {
			order.setBody(work.getWorkName());
		}
		if (money == null) {
			order.setTotalFee(work.getPrice());
		} else {
			order.setTotalFee(money);
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
