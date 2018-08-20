package com.youmeiwang.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.OrderDao;
import com.youmeiwang.entity.Order;

@Component
public class OrderMongodb implements OrderDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addOrder(Order order) {
		mongoTemplate.insert(order);
	}

	@Override
	public void removeOrder(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Order.class);
	}

	@Override
	public void updateOrder(Order order) {
		Query query = new Query(Criteria.where("outTradeNo").is(order.getOutTradeNo()));
		Update update = new Update();
		update.set("productID", order.getProductID());
		update.set("body", order.getBody());
		update.set("totalFee", order.getTotalFee());
		update.set("timeStart", order.getStartTime());
		mongoTemplate.updateFirst(query, update, Order.class);
	}
	
	@Override
	public void setOrder(String condition, Object value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, Order.class);
	}
	
	@Override
	public Order queryOrder(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Order.class);
	}

	
}
