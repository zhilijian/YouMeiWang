package meikuu.repertory.mongodb;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import meikuu.domain.entity.work.Topic;
import meikuu.repertory.dao.TopicDao;

@Component
public class TopicMongodb implements TopicDao{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void addTopic(Topic topic) {
		mongoTemplate.save(topic);
	}

	@Override
	public void removeTopic(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Topic.class);
	}

	@Override
	public void updateTopic(Topic topic) {
		Query query = new Query(Criteria.where("topicID").is(topic.getTopicID()));
		Update update = new Update();
		update.set("topicName", topic.getTopicName());
		update.set("picturePath", topic.getPicturePath());
		update.set("describe", topic.getDescribe());
		update.set("isRecommend", topic.getIsRecommend());
		update.set("createDate", topic.getCreateTime());
		update.set("browsed", topic.getBrowsed());
		update.set("collected", topic.getCollected());
		update.set("works", topic.getWorks());
		mongoTemplate.updateFirst(query, update, Topic.class);
	}
	
	@Override
	public void setTopic(String condition, String value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, Topic.class);
	}

	
	@Override
	public void changeIsRecommend(String topicID, Integer isRecommend) {
		Query query = new Query(Criteria.where("topicID").is(topicID));
		Update update = new Update();
		update.set("isRecommend", isRecommend);
		mongoTemplate.updateFirst(query, update, Topic.class);
	}
	
	@Override
	public Topic queryTopic(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Topic.class);
	}
	
	@Override
	public Long getTopicAmount() {
		Query query = new Query();
		return mongoTemplate.count(query, Topic.class);
	}
	
	@Override
	public Long getTopicAmount(String condition1, String value1, String condition2, Integer value2) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).regex(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		return mongoTemplate.count(query, Topic.class);
	}
	
	@Override
	public List<Topic> topiclist(boolean isRecommend) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isRecommend").is(isRecommend));
		return mongoTemplate.find(query, Topic.class);
	}

	@Override
	public List<Topic> topicList(Map<String, Object> conditions, Integer page, Integer size) {
		Query query = new Query();
		if (conditions != null) {
			for (String key : conditions.keySet()) {
				query.addCriteria(Criteria.where(key).is(conditions.get(key)));
			}
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Topic.class);
	}

	@Override
	public List<Topic> topicList(String condition1, String value1, String condition2, Integer value2, Integer page, Integer size) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).regex(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, Topic.class);
	}

	@Override
	public List<Topic> topiclist() {
		Query query = new Query();
		return mongoTemplate.find(query, Topic.class);
	}

	@Override
	public List<Topic> topiclist(String topicName, boolean isRecommend) {
		Query query = new Query();
		if (topicName != null && !"".equals(topicName.trim())) {
			query.addCriteria(Criteria.where("topicName").regex(topicName));
		}
		if (isRecommend) {
			query.addCriteria(Criteria.where("isRecommend").is(true));
		}
		return mongoTemplate.find(query, Topic.class);
	}

}
