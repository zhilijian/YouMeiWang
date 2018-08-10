package com.youmeiwang.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.AdminDao;
import com.youmeiwang.entity.Admin;

@Component
public class AdminMongodb implements AdminDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addAdmin(Admin admin) {
		mongoTemplate.save(admin);
	}

	@Override
	public void removeAdmin(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Admin.class);
	}
	
	@Override
	public void batchRemoveAdmin(String condition, String[] values) {
		for (String value : values) {
			Query query = new Query(Criteria.where(condition).is(value));
			mongoTemplate.remove(query, Admin.class);
		}
	}
	
	@Override
	public void updateAdmin(Admin admin) {
		Query query = new Query(Criteria.where("adminID").is(admin.getAdminID()));
		Update update = new Update();
		update.set("adminname", admin.getAdminname());
		update.set("password", admin.getPassword());
		update.set("position", admin.getPosition());
		update.set("adminManager", admin.getAdminManager());
		update.set("workManager", admin.getWorkManager());
		update.set("homepageModule", admin.getHomepageModule());
		update.set("rechargeManager", admin.getRechargeManager());
		update.set("roleAuthority", admin.getRoleAuthority());
		mongoTemplate.updateFirst(query, update, Admin.class);
	}
	
	@Override
	public Admin queryAdmin(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Admin.class);
	}

	@Override
	public Admin queryAdmin(String condition1, String value1, String condition2, String value2) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.and(condition1).is(value1);
		criteria.and(condition2).is(value2);
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, Admin.class);
	}

	@Override
	public Long getAmount(String condition, String value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		return mongoTemplate.count(query, Admin.class);
	}
	
	@Override
	public List<Admin> adminList(String condition, String value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, Admin.class);
	}
}
