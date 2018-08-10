package com.youmeiwang.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.Work;

@Component
public class WorkMongodb  implements WorkDao{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Work addWork(Work work) {
		mongoTemplate.insert(work);
		return null;
	}
	
	@Override
	public void removeWork(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Work.class);
	}

	@Override
	public void updateWork(Work work) {
		Query query = new Query(Criteria.where("workId").is(work.getWorkID()));
		Update update = new Update();
		update.set("workName", work.getWorkName());
		update.set("authorID", work.getAuthorID());
		update.set("primaryClassification", work.getPrimaryClassification());
		update.set("secondaryClassification", work.getSecondaryClassification());
		update.set("reclassify", work.getReclassify());
		update.set("pattern", work.getPattern());
		update.set("hasTextureMapping", work.isHasTextureMapping());
		update.set("isBinding", work.isBinding());
		update.set("hasCartoon", work.isHasCartoon());
		update.set("price", work.getLabels());
		update.set("currency", work.getCurrency());
		update.set("labels", work.getLabels());
		update.set("verifyState", work.getVerifyState());
		update.set("topicID", work.getTopicID());
		update.set("picturePath", work.getPicturePath());
		update.set("filePath", work.getFilePath());
		update.set("downloadNum", work.getDownloadNum());
		mongoTemplate.updateFirst(query, update, Work.class);
	}
	
	@Override
	public Work queryWork(String condition, String value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Work.class);
	}
	
	@Override
	public Long getAmount(String condition, String value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		return mongoTemplate.count(query, Work.class);
	}

	@Override
	public List<Work> workList(String condition, String value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, Work.class);
	}

}
