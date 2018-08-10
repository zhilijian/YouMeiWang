package com.youmeiwang.wxpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

@Component
public class MongodbWXOrderDao implements WXOrderDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public WXOrder findWXOrderByOut_trade_no(String out_trade_no) {
		Query query = new Query(Criteria.where("out_trade_no").is(out_trade_no));
		return mongoTemplate.findOne(query, WXOrder.class);
	}

	@Override
	public void addWXOrder(WXOrder order) {
		mongoTemplate.insert(order);
	}

	@Override
	public Boolean updateWXOrderStatus(String out_trade_no, String status) {
		Query query = new Query(Criteria.where("out_trade_no").is(out_trade_no));
		Update update = new Update();
		update.set("status", status);
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, WXOrder.class);
		return writeResult.getN() > 0;
	}

	@Override
	public Boolean updateTransaction_id(String out_trade_no, String transaction_id) {
		Query query = new Query(Criteria.where("out_trade_no").is(out_trade_no));
		Update update = new Update();
		update.set("transaction_id", transaction_id);
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, WXOrder.class);
		return writeResult.getN() > 0;
	}

	@Override
	public Boolean updateDeliveTime(String out_trade_no, Long deliveTime) {
		Query query = new Query(Criteria.where("out_trade_no").is(out_trade_no));
		Update update = new Update();
		update.set("deliveTime", deliveTime);
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, WXOrder.class);
		return writeResult.getN() > 0;
	}
}
