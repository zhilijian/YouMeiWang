package meikuu.repertory.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import meikuu.domain.entity.user.User;
import meikuu.repertory.dao.UserDao;

@Component
public class UserMongodb implements UserDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void addUser(User user) {
		mongoTemplate.save(user);
	}
	
	@Override
	public void removeUser(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, User.class);
	}
	
	@Override
	public void updateUser(User user) {
		Query query = new Query(Criteria.where("userID").is(user.getUserID()));
		Update update = new Update();
		update.set("username", user.getUsername());
		update.set("nickname", user.getNickname());
		update.set("fullname", user.getFullname());
		update.set("phone", user.getPhone());
		update.set("portrait", user.getPortrait());		
		update.set("alipay", user.getAlipay());
		update.set("qq", user.getQq());
		update.set("email", user.getEmail());
		update.set("shareVIPTime", user.getShareVIPTime());
		update.set("originalVIPTime", user.getOriginalVIPTime());
		update.set("companyVIPTime", user.getCompanyVIPTime());
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public void setUser(String condition, Object value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public User queryUser(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public Long getAmount(String condition, Object value) {
		Query query = new Query();
		if (condition != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		return mongoTemplate.count(query, User.class);
	}
	
	@Override
	public Long getAmount(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify) {
		Query query = new Query();
		if (condition != null && !"".equals(condition.trim())) {
			Criteria criteria1 = Criteria.where("userID").regex(condition);
			Criteria criteria2 = Criteria.where("username").regex(condition);
			Criteria criteria3 = Criteria.where("nickname").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (VIPKind != null) {
			query.addCriteria(Criteria.where("vipKind").is(VIPKind));
		}
		if (memberKind != null) {
			query.addCriteria(Criteria.where("memberKind").is(memberKind));
		}
		if (isVerify) {
			query.addCriteria(Criteria.where("applyForOriginal").is(1));
		}
		return mongoTemplate.count(query, User.class);
	}
	
	@Override
	public List<User> userlist() {
		Query query = new Query();
		query.addCriteria(Criteria.where("vipKind").ne(0));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userlist(String condition, String value, Integer page, Integer size) {
		Query query = new Query();
		if (condition != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userlist(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify, Integer page, Integer size) {
		Query query = new Query();
		if (condition != null && !"".equals(condition.trim())) {
			Criteria criteria1 = Criteria.where("userID").regex(condition);
			Criteria criteria2 = Criteria.where("username").regex(condition);
			Criteria criteria3 = Criteria.where("nickname").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (VIPKind != null) {
			query.addCriteria(Criteria.where("vipKind").is(VIPKind));
		}
		if (memberKind != null) {
			query.addCriteria(Criteria.where("memberKind").is(memberKind));
		}
		if (isVerify) {
			query.addCriteria(Criteria.where("applyForOriginal").is(1));
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, User.class);
	}
}
