package meikuu.repertory.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import meikuu.domain.entity.pay.Transaction;
import meikuu.repertory.dao.TransactionDao;

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
	public List<Transaction> transactionlist(String condition, Integer currency, Integer reason, Long startTime, Long endTime) {
		Query query = new Query();
		if (condition != null && !"".equals(condition)) {
			Criteria criteria1 = Criteria.where("userID").regex(condition);
			Criteria criteria2 = Criteria.where("username").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2));
		}
		if (currency != null) {
			query.addCriteria(Criteria.where("currency").is(currency));
		}
		if (reason != null) {
			query.addCriteria(Criteria.where("reason").is(reason));
		}
		if (startTime != null || endTime != null) {
			Criteria criteria = Criteria.where("operateTime");
			if (startTime != null) {
				criteria = criteria.gte(startTime);
			}
			if (endTime != null) {
				criteria = criteria.lte(endTime);
			}
			query.addCriteria(criteria);
		}
		return mongoTemplate.find(query, Transaction.class);
	}
}
