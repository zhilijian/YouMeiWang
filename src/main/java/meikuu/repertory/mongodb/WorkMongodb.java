package meikuu.repertory.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import meikuu.domain.entity.work.Work;
import meikuu.repertory.dao.WorkDao;

/**
 * 作品对象数据层
 * @author Administrator
 */
@Component
public class WorkMongodb implements WorkDao{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void addWork(Work work) {
		mongoTemplate.insert(work);
	}
	
	@Override
	public void removeWork(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Work.class);
	}

	@Override
	public void updateWork(Work work) {
		Query query = new Query(Criteria.where("workID").is(work.getWorkID()));
		Update update = new Update();
		update.set("workName", work.getWorkName());
		update.set("primaryClassification", work.getPrimaryClassification());
		update.set("yijifenlei", work.getYijifenlei());
		update.set("secondaryClassification", work.getSecondaryClassification());
		update.set("erjifenlei", work.getErjifenlei());
		update.set("reclassify", work.getReclassify());
		update.set("sanjifenlei", work.getSanjifenlei());
		update.set("pattern", work.getPattern());
		update.set("geshi", work.getGeshi());
		update.set("hasTextureMapping", work.isHasTextureMapping());
		update.set("isBinding", work.isBinding());
		update.set("hasCartoon", work.isHasCartoon());
		update.set("price", work.getPrice());
		update.set("labels", work.getLabels());
		update.set("pictures", work.getPictures());
		update.set("files", work.getFiles());
		update.set("remarks", work.getRemarks());
		update.set("verifyMessage", work.getVerifyMessage());
		mongoTemplate.updateFirst(query, update, Work.class);
	}
	
	@Override
	public Work queryWork(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Work.class);
	}
	
	@Override
	public void setWork(String condition, Object value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, Work.class);
	}
	
	@Override
	public Long getAmount(String condition, Object value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		return mongoTemplate.count(query, Work.class);
	}
	
	@Override
	public Long getAmount(String condition, Integer primaryClassification, Boolean verifyState) {
		Query query = new Query();
		if (condition != null && !"".equals(condition)) {
			Criteria criteria1 = Criteria.where("workID").regex(condition);
			Criteria criteria2 = Criteria.where("workName").regex(condition);
			Criteria criteria3 = Criteria.where("author").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (verifyState) {
			query.addCriteria(Criteria.where("verifyState").is(0));
		} else {
			query.addCriteria(Criteria.where("verifyState").is(1));
		}
		return mongoTemplate.count(query, Work.class);
	}

	@Override
	public Long getAmount(Integer modelType, String condition, Integer primaryClassification,
			Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType) {
		Query query = new Query();
		if (condition != null && !"".equals(condition.trim())) {
			Criteria criteria1 = Criteria.where("workID").regex(condition);
			Criteria criteria2 = Criteria.where("workName").regex(condition);
			Criteria criteria3 = Criteria.where("labels").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (modelType != null) {
			switch (modelType) {
			case 1:
				query.addCriteria(Criteria.where("primaryClassification").ne(2));
				break;
			case 2:
				query.addCriteria(Criteria.where("primaryClassification").is(2));
				break;
			}
		}
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (secondaryClassification != null) {
			query.addCriteria(Criteria.where("secondaryClassification").is(secondaryClassification));
		}
		if (reclassify != null) {
			query.addCriteria(Criteria.where("reclassify").is(reclassify));
		}
		if (pattern != null) {
			query.addCriteria(Criteria.where("pattern").is(pattern));
		}
		if (sortType != null) {
			switch (sortType) {
			case 1:
				query.with(new Sort(new Order(Sort.Direction.DESC, "uploadTime")));
				break;
			case 2:
				query.with(new Sort(new Order(Sort.Direction.DESC, "downloadNum")));
				break;
			case 3:
				query.with(new Sort(new Order(Sort.Direction.DESC, "collectNum")));
				break;
			}
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		return mongoTemplate.count(query, Work.class);
	}

	@Override
	public List<Work> workSort(Integer primaryClassification, Integer secondaryClassification, Integer limit) {
		Query query = new Query();
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (secondaryClassification != null) {
			query.addCriteria(Criteria.where("secondaryClassification").is(secondaryClassification));
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		query.with(new Sort(new Order(Sort.Direction.DESC, "downloadNum")));
		query.limit(limit);
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType, Integer page, Integer size) {
		Query query = new Query();
		if (condition != null && !"".equals(condition.trim())) {
			Criteria criteria1 = Criteria.where("workID").regex(condition);
			Criteria criteria2 = Criteria.where("workName").regex(condition);
			Criteria criteria3 = Criteria.where("labels").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (modelType != null) {
			switch (modelType) {
			case 1:
				query.addCriteria(Criteria.where("primaryClassification").ne(2));
				break;
			case 2:
				query.addCriteria(Criteria.where("primaryClassification").is(2));
				break;
			}
		}
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (secondaryClassification != null) {
			query.addCriteria(Criteria.where("secondaryClassification").is(secondaryClassification));
		}
		if (reclassify != null) {
			query.addCriteria(Criteria.where("reclassify").is(reclassify));
		}
		if (pattern != null) {
			query.addCriteria(Criteria.where("pattern").is(pattern));
		}
		if (sortType != null) {
			switch (sortType) {
			case 1:
				query.with(new Sort(new Order(Sort.Direction.DESC, "uploadTime")));
				break;
			case 2:
				query.with(new Sort(new Order(Sort.Direction.DESC, "downloadNum")));
				break;
			case 3:
				query.with(new Sort(new Order(Sort.Direction.DESC, "collectNum")));
				break;
			}
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState, Integer page, Integer size) {
		Query query = new Query();
		if (condition != null && !"".equals(condition)) {
			Criteria criteria1 = Criteria.where("workID").regex(condition);
			Criteria criteria2 = Criteria.where("workName").regex(condition);
			Criteria criteria3 = Criteria.where("author").regex(condition);
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2, criteria3));
		}
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (verifyState) {
			query.addCriteria(Criteria.where("verifyState").is(0));
		} else {
			query.addCriteria(Criteria.where("verifyState").is(1));
		}
		query.with(new Sort(new Order(Sort.Direction.DESC, "uploadTime")));
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification, String workID) {
		Query query = new Query();
		query.addCriteria(Criteria.where("workID").ne(workID));
		query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		query.addCriteria(Criteria.where("secondaryClassification").is(secondaryClassification));
		query.addCriteria(Criteria.where("verifyState").is(1));
		query.with(new Sort(new Order(Sort.Direction.DESC, "downloadNum")));
		query.limit(8);
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(Integer primaryClassification, Boolean downloadOrBrowse, Integer page, Integer size) {
		Query query = new Query();
		if (primaryClassification != null) {
			query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		}
		if (downloadOrBrowse) {
			query.with(new Sort(new Order(Sort.Direction.DESC, "downloadNum")));
		} else {
			query.with(new Sort(new Order(Sort.Direction.DESC, "browseNum")));
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		return mongoTemplate.find(query, Work.class);
	}
}
