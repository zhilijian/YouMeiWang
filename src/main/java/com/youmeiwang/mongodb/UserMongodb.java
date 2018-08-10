package com.youmeiwang.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.entity.User;

@Component
public class UserMongodb implements UserDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void addUser(User user) {
		mongoTemplate.save(user);
	}
	
	@Override
	public void removeUser(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, User.class);
	}
	
	@Override
	public void batchRemoveUser(String condition, String[] values) {
		for (String value : values) {
			Query query = new Query(Criteria.where(condition).is(value));
			mongoTemplate.remove(query, User.class);
		}
	}

	@Override
	public void updateUser(User user) {
		Query query = new Query(Criteria.where("userID").is(user.getUserID()));
		Update update = new Update();
		update.set("username", user.getUsername());
		update.set("nickname", user.getNickname());
		update.set("phone", user.getPhone());
		update.set("portrait", user.getPortrait());
		update.set("Alipay", user.getAlipay());
		update.set("QQ", user.getQQ());
		update.set("email", user.getEmail());
		update.set("youbiAmount", user.getYoubiAmount());
		update.set("balance", user.getBalance());
		update.set("VIPKind", user.getVIPKind());
		update.set("memberKind", user.getMemberKind());
		update.set("memberExpirationTime", user.getMemberExpirationTime());
		update.set("verifyingWork", user.getVerifyingWork());
		update.set("verifiedWork", user.getVerifiedWork());
		update.set("notPassWork", user.getNotPassWork());
		update.set("collectWork", user.getCollectWork());
		update.set("downWork", user.getDownWork());
		update.set("ApplyForOriginal", user.getApplyForOriginal());
		update.set("verifyMessage", user.getVerifyMessage());
	
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public User queryUser(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, User.class);
	}
	
	@Override
	public Long getAmount(String condition, String value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		return mongoTemplate.count(query, User.class);
	}
	
	@Override
	public Long getAmount(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).regex(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).in(value2));
		}
		if (value3 != null) {
			query.addCriteria(Criteria.where(condition3).is(value3));
		}
		return mongoTemplate.count(query, User.class);
	}

	@Override
	public List<User> userList(String condition, String value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userList(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3, Integer page, Integer size) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).regex(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).in(value2));
		}
		if (value3 != null) {
			query.addCriteria(Criteria.where(condition3).is(value3));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, User.class);
	}

}
