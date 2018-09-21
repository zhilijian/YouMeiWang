package meikuu.repertory.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import meikuu.domain.entity.other.BBS;
import meikuu.repertory.dao.BBSDao;

@Component
public class BBSMongodb implements BBSDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addBBS(BBS bbs) {
		mongoTemplate.save(bbs);
	}

	@Override
	public void removeBBS(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, BBS.class);
	}

	@Override
	public void updateBBS(BBS bbs) {
		Query query = new Query(Criteria.where("bbsID").is(bbs.getBbsID()));
		Update update = new Update();
		update.set("userID", bbs.getUserID());
		update.set("workID", bbs.getWorkID());
		update.set("correctionType", bbs.getCorrectionType());
		update.set("comment", bbs.getComment());
		update.set("type", bbs.getType());
		
		mongoTemplate.updateFirst(query, update, BBS.class);
	}

	@Override
	public Long getBBSAmount(String condition, Object value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		return mongoTemplate.count(query, BBS.class);
	}
	
	@Override
	public Long getBBSAmount(String condition1, Object value1, String condition2, Object value2) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		return mongoTemplate.count(query, BBS.class);
	}
	
	@Override
	public BBS queryBBS(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, BBS.class);
	}
	
	@Override
	public BBS queryBBS(String condition1, Object value1, String condition2, Object value2) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.and(condition1).is(value1);
		criteria.and(condition2).is(value2);
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, BBS.class);
	}
	
	@Override
	public List<BBS> bbsList(String condition, Object value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, BBS.class);
	}

	@Override
	public List<BBS> bbsList(String condition1, Object value1, String condition2, 
			Object value2, Integer page, Integer size) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, BBS.class);
	}

	
}
