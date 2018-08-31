package com.youmeiwang.mongodb;

import java.util.List;
import java.util.Map;

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
		update.set("body", order.getBody());
		update.set("totalFee", order.getTotalFee());
		update.set("startTime", order.getStartTime());
		update.set("payStatus", order.getPayStatus());
		update.set("attach", order.getAttach());
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

	@Override
	public List<Order> orderList(Integer searchType, String condition, String value, 
			Map<String, Object> conditions,Integer page, Integer size) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).is(value));
			}
			break;
		case 2:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).regex(value));
			}
			break;
		case 3:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).in(value));
			}
			break;
		default:
			break;
		}
		if (conditions != null) {
			for (String key : conditions.keySet()) {
				query.addCriteria(Criteria.where(key).is(conditions.get(key)));
			}
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Order.class);
	}

	@Override
	public List<Order> orderList(List<Map<String, Object>> conditions, Integer page, Integer size) {
		Query query = new Query();
		if (conditions != null) {
			for (Map<String, Object> map : conditions) {
				if (map == null) {
					continue;
				}
				
				switch ((int)map.get("searchType")) {
				case 1:
					query.addCriteria(Criteria.where((String)map.get("condition")).is(map.get("value")));
					break;
				case 2:
					query.addCriteria(Criteria.where((String)map.get("condition")).regex((String)map.get("value")));
					break;
				case 3:
					query.addCriteria(Criteria.where((String)map.get("condition")).in(map.get("value")));
					break;
				case 4:
					query.addCriteria(Criteria.where((String)map.get("condition")).ne(map.get("value")));
					break;
				case 5:
					query.addCriteria(Criteria.where((String)map.get("condition")).gte(map.get("value")));
					break;
				case 6:
					query.addCriteria(Criteria.where((String)map.get("condition")).gt(map.get("value")));
					break;
				case 7:
					query.addCriteria(Criteria.where((String)map.get("condition")).lte(map.get("value")));
					break;
				case 8:
					query.addCriteria(Criteria.where((String)map.get("condition")).lt(map.get("value")));
					break;
				default:
					break;
				}
			}
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Order.class);
	}

	@Override
	public List<Order> orderList(String condition, String value, Integer payType,
			String payStatus, Long startTime, Long endTime, Integer page, Integer size) {
		Query query = new Query();
		if (value != null && !"".equals(value)) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		if (payType != null) {
			query.addCriteria(Criteria.where("payType").is(payType));
		}
		if (payStatus != null && !"".equals(payStatus)) {
			query.addCriteria(Criteria.where("payStatus").is(payStatus));
		}
		if (startTime != null) {
			query.addCriteria(Criteria.where("endTime").gte(startTime));
		}
		if (endTime != null) {
			query.addCriteria(Criteria.where("endTime").is(endTime));
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Order.class);
	}

	
}
