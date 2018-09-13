package com.youmeiwang.mongodb;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.Work;

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
	public void removeWork(String condition1, Object value1, String condition2, Object value2) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.and(condition1).is(value1);
		criteria.and(condition2).is(value2);
		query.addCriteria(criteria);
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
		update.set("downloadNum", work.getDownloadNum());
		update.set("collectNum", work.getCollectNum());
		update.set("browseNum", work.getBrowseNum());
		update.set("remarks", work.getRemarks());
		update.set("isDelete", work.getIsDelete());
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
	public Long getAmount(String condition, String value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).regex(value));
		}
		return mongoTemplate.count(query, Work.class);
	}
	
	@Override
	public Long getAmount(String condition, Object value) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		return mongoTemplate.count(query, Work.class);
	}
	
	@Override
	public Long getAmount(String condition1, String value1, String condition2, Object value2) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).regex(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		return mongoTemplate.count(query, Work.class);
	}
	
	@Override
	public Long getAmount(String condition1, Object value1, 
			String condition2, Object value2, String condition3,Object value3) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		if (value3 != null) {
			query.addCriteria(Criteria.where(condition3).is(value3));
		}
		return mongoTemplate.count(query, Work.class);
	}
	
	@Override
	public Long getAmount(List<Map<String, Object>> conditions) {
		Query query = new Query();
		if (conditions != null) {
			for (Map<String, Object> map : conditions) {
				if (map == null) {
					continue;
				}
				
				switch ((int)map.get("searchType")) {
				case 1:
					query.addCriteria(Criteria.where((String)map.get("condition")).is(map.get("value")));
					break;
				case 2:
					query.addCriteria(Criteria.where((String)map.get("condition")).regex((String)map.get("value")));
					break;
				case 3:
					query.addCriteria(Criteria.where((String)map.get("condition")).in(map.get("value")));
					break;
				case 4:
					query.addCriteria(Criteria.where((String)map.get("condition")).is(map.get("value")).not());
					break;
				default:
					break;
				}
			}
		}
		return mongoTemplate.count(query, Work.class);
	}

	@Override
	public List<Work> workSortDESC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		query.with(new Sort(new Order(Sort.Direction.DESC, condition3)));
		query.limit(limit);
		return mongoTemplate.find(query, Work.class);
	}
	
	
	
	@Override
	public List<Work> workSortDESC(String condition, Map<String, Object> conditions, Integer limit) {
		Query query = new Query();
		if (conditions != null) {
			for (String key : conditions.keySet()) {
				query.addCriteria(Criteria.where(key).is(conditions.get(key)));
			}
		}
		query.with(new Sort(new Order(Sort.Direction.DESC, condition)));
		query.limit(limit);
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> workSortASC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		Query query = new Query();
		if (value1 != null) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		query.with(new Sort(new Order(Sort.Direction.ASC, condition3)));
		return mongoTemplate.find(query, Work.class);
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

	@Override
	public List<Work> workList(String condition, Integer value, Integer page, Integer size) {
		Query query = new Query();
		if (value != null) {
			query.addCriteria(Criteria.where(condition).is(value));
		}
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, Work.class);
	}
	
	@Override
	public List<Work> workList(Boolean flag, String condition1, String value1, String condition2, Object value2, String condition3, Object value3, Integer page, Integer size) {
		Query query = new Query();
		if (value1 != null) {
			if (flag) {
				query.addCriteria(Criteria.where(condition1).regex(value1));
			} else {
				query.addCriteria(Criteria.where(condition1).is(value1));
			}
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2)); 
		}
		if (value3 != null) {
			query.addCriteria(Criteria.where(condition3).is(value3));
		}
		return mongoTemplate.find(query, Work.class);
	}
	
	@Override
	public List<Work> workList(String condition1, Object value1, String condition2, Object value2, String condition3, Object value3, Integer page, Integer size) {
		Query query = new Query();
		if (value1 != null && !value1.equals("")) {
			query.addCriteria(Criteria.where(condition1).is(value1));
		}
		if (value2 != null) {
			query.addCriteria(Criteria.where(condition2).is(value2));
		}
		if (value3 != null) {
			query.addCriteria(Criteria.where(condition3).is(value3));
		}
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> workList(Integer searchType, String condition, String value, Map<String, Object> conditions, Integer page, Integer size) {
		Query query = new Query();
		switch (searchType) {
		case 1:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).is(value));
			}
			break;
		case 2:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).regex(value));
			}
			break;
		case 3:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).in(value));
			}
			break;
		case 4:
			if (value != null) {
				query.addCriteria(Criteria.where(condition).is(value).not());
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
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> workList1(List<Map<String, Object>> conditions, Integer page, Integer size) {
		Query query = new Query();
		if (conditions != null) {
			for (Map<String, Object> map : conditions) {
				if (map == null) {
					continue;
				}
				
				switch ((int)map.get("searchType")) {
				case 1:
					query.addCriteria(Criteria.where((String)map.get("condition")).is(map.get("value")));
					break;
				case 2:
					query.addCriteria(Criteria.where((String)map.get("condition")).regex((String)map.get("value")));
					break;
				case 3:
					query.addCriteria(Criteria.where((String)map.get("condition")).in(map.get("value")));
					break;
				case 4:
					query.addCriteria(Criteria.where((String)map.get("condition")).ne(map.get("value")));
					break;
				case 5:
					query.addCriteria(Criteria.where((String)map.get("condition")).gte(map.get("value")));
					break;
				case 6:
					query.addCriteria(Criteria.where((String)map.get("condition")).gt(map.get("value")));
					break;
				case 7:
					query.addCriteria(Criteria.where((String)map.get("condition")).lte(map.get("value")));
					break;
				case 8:
					query.addCriteria(Criteria.where((String)map.get("condition")).lt(map.get("value")));
					break;
				default:
					break;
				}
			}
		}
		if (page != null && size != null) {
			query.skip((page - 1) * size);
			query.limit(size);
		}
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType) {
		Query query = new Query();
		if (condition != null && !"".equals(condition)) {
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
			default:
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
			default:
				break;
			}
		}
		query.addCriteria(Criteria.where("verifyState").is(1));
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState) {
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
			query.addCriteria(Criteria.where("verifyState").is(1));
		}
		return mongoTemplate.find(query, Work.class);
	}

	@Override
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification) {
		Query query = new Query();
		query.addCriteria(Criteria.where("primaryClassification").is(primaryClassification));
		query.addCriteria(Criteria.where("secondaryClassification").is(secondaryClassification));
		query.addCriteria(Criteria.where("isDelete").is(false));
		query.limit(8);
		return mongoTemplate.find(query, Work.class);
	}
}
