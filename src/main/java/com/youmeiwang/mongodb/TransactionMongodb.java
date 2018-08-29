package com.youmeiwang.mongodb;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.TransactionDao;
import com.youmeiwang.entity.Transaction;

@Component
public class TransactionMongodb implements TransactionDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addTransaction(Transaction transaction) {
		mongoTemplate.save(transaction);
	}

	@Override
	public void removeTransaction(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Transaction.class);
	}

	@Override
	public void removeTransaction(Map<String, Object> conditions) {
		Query query = new Query();
		if (conditions != null) {
			for (String key : conditions.keySet()) {
				query.addCriteria(Criteria.where(key).is(conditions.get(key)));
			}
		}
		mongoTemplate.remove(query, Transaction.class);
	}

	@Override
	public void updateTransaction(Transaction transaction) {
		Query query = new Query(Criteria.where("transactionID").is(transaction.getTransactionID()));
		Update update = new Update();
		update.set("money", transaction.getMoney());
		update.set("reason", transaction.getReason());
		update.set("operateTime", transaction.getOperateTime());
		update.set("currency", transaction.getCurrency());
		mongoTemplate.updateFirst(query, update, Transaction.class);
	}

	@Override
	public void setTransaction(String condition, Object value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, Transaction.class);
	}

	@Override
	public Transaction queryTransaction(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Transaction.class);
	}

	@Override
	public Transaction queryTransaction(Map<String, Object> conditions) {
		Query query = new Query();
		if (conditions != null) {
			for (String key : conditions.keySet()) {
				query.addCriteria(Criteria.where(key).is(conditions.get(key)));
			}
		}
		return mongoTemplate.findOne(query, Transaction.class);
	}

	@Override
	public Long getAmount(Integer searchType, String condition, Object value) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).is(value));
			}
			break;
		case 2:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).regex((String)value));
			}
			break;
		case 3:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).in(value));
			}
			break;
		default:
			break;
		}
		return mongoTemplate.count(query, Transaction.class);
	}

	@Override
	public Long getAmount(Integer searchType, Map<String, Object> conditions) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (conditions != null) {
				for (String key : conditions.keySet()) {
					query.addCriteria(Criteria.where(key).is(conditions.get(key)));
				}
			}
			break;
		case 2:
			if (conditions != null) {
				for (String key : conditions.keySet()) {
					query.addCriteria(Criteria.where(key).regex((String)conditions.get(key)));
				}
			}
			break;
		case 3:
			if (conditions != null) {
				for (String key : conditions.keySet()) {
					query.addCriteria(Criteria.where(key).in(conditions.get(key)));
				}
			}
			break;
		default:
			break;
		}
		return mongoTemplate.count(query, Transaction.class);
	}

	@Override
	public List<Transaction> transactionList(Integer searchType, String condition, Object value, Integer page, Integer size) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).is(value));
			}
			break;
		case 2:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).regex((String)value));
			}
			break;
		case 3:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).in(value));
			}
			break;
		default:
			break;
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Transaction.class);
	}

	@Override
	public List<Transaction> transactionList(Integer searchType, String condition, Object value,
			Map<String, Object> conditions, Integer page, Integer size) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).is(value));
			}
			break;
		case 2:
			if (value != null && !("").equals(value)) {
				query.addCriteria(Criteria.where(condition).regex((String)value));
			}
			break;
		case 3:
			if (value != null && !("").equals(value)) {
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
		return mongoTemplate.find(query, Transaction.class);
	}
}
