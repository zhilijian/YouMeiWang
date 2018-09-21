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
	public void removeUser(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, User.class);
	}
	
	@Override
	public void updateUser(User user) {
		Query query = new Query(Criteria.where("userID").is(user.getUserID()));
		Update update = new Update();
		update.set("username", user.getUsername());
		update.set("nickname", user.getNickname());
		if (user.getFullname() != null) {
			update.set("fullname", user.getFullname());
		}
		if (user.getPhone() != null) {
			update.set("phone", user.getPhone());
		}
		if (user.getPortrait() != null) {
			update.set("portrait", user.getPortrait());		
		}
		if (user.getAlipay() != null) {
			update.set("alipay", user.getAlipay());
		}
		if (user.getQq() != null) {
			update.set("qq", user.getQq());
		}
		if (user.getEmail() != null) {
			update.set("email", user.getEmail());
		}
		if (user.getYoubiAmount() != null) {
			update.set("youbiAmount", user.getYoubiAmount());
		}
		if (user.getBalance() != null) {
			update.set("balance", user.getBalance());
		}
		if (user.getVipKind() != null) {
			update.set("vipKind", user.getVipKind());
		}
		if (user.getMemberKind() != null) {
			update.set("memberKind", user.getMemberKind());
		}
		if (user.getMemberKind() != null) {
			update.set("memberKind", user.getMemberKind());
		}
		if (user.getVerifyingWork() != null) {
			update.set("verifyingWork", user.getVerifyingWork());
		}
		if (user.getVerifiedWork() != null) {
			update.set("verifiedWork", user.getVerifiedWork());
		}
		if (user.getNotPassWork() != null) {
			update.set("notPassWork", user.getNotPassWork());
		}
		if (user.getCollectWork() != null) {
			update.set("collectWork", user.getCollectWork());
		}
		if (user.getDownWork() != null) {
			update.set("downWork", user.getDownWork());
		}
		if (user.getPurchaseWork() != null) {
			update.set("purchaseWork", user.getPurchaseWork());
		}
		if (user.getApplyForOriginal() != null) {
			update.set("applyForOriginal", user.getApplyForOriginal());
		}
		if (user.getVerifyMessage() != null) {
			update.set("verifyMessage", user.getVerifyMessage());
		}
		if (user.getCommissionRate() != null) {
			update.set("commissionRate", user.getCommissionRate());
		}
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
	public Long getAmount(String condition, Object value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
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
	public List<User> userlist() {
		Query query = new Query();
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userList(String condition, Object value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userList(String condition, Integer VIPKind, Integer memberKind, Integer applyForOriginal) {
		Query query = new Query();
		if (condition != null && !"".equals(condition)) {
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
		if (applyForOriginal != null) {
			query.addCriteria(Criteria.where("applyForOriginal").is(applyForOriginal));
		}
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> userlist(String condition1, Object value1, String condition2, String value2) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null && !"".equals(value2)) {
			query.addCriteria(Criteria.where(condition2).regex(value2));
		}
		return mongoTemplate.find(query, User.class);
	}


}
