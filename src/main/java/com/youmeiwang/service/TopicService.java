package com.youmeiwang.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.TopicDao;
import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.Topic;
import com.youmeiwang.entity.Work;
import com.youmeiwang.util.ListUtil;

@Service
public class TopicService {

	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private TopicDao topicDao;
	
	public void addTopic(Topic topic) {
		topicDao.addTopic(topic);
	}
	
	public Map<String, String> addTopicWork(String workID) {
		Work work = workDao.queryWork("workID", workID);
		Map<String, String> map = new HashMap<String, String>();
		map.put("workID", workID);
		map.put("workName", work.getWorkName());
		return map;
	}
	
	public void addTopicWork(String topicID, String[] workIDs) {
		Topic topic = topicDao.queryTopic("topicID", topicID);
		List<String> works = new LinkedList<String>();
		if(topic.getWorks() != null) {
			works = topic.getWorks();
		}
		for (String workID : workIDs) {
			if(ListUtil.isHasString(works, workID)) {
				continue;
			} 
			works = ListUtil.addString(works, workID);
		}
		topic.setWorks(works);
		topicDao.updateTopic(topic);
	}
	
	public void removeTopic(String condition, String value) {
		topicDao.removeTopic(condition, value);
	}

	public void updateTopic(Topic topic) {
		topicDao.updateTopic(topic);
	}
	
	public void changeIsRecommend(String topicID, Integer isRecommend) {
		topicDao.changeIsRecommend(topicID, isRecommend);
	}
	
	public Topic queryTopic(String condition, String value) {
		return topicDao.queryTopic(condition, value);
	}
	
	public Long getTopicAmount(String condition1, String value1, String condition2, Integer value2) {
		return topicDao.getTopicAmount(condition1, value1, condition2, value2);
	}
	
	public List<Topic> topicList(String condition1, String value1, String condition2, Integer value2, Integer page, Integer size) {
		return topicDao.topicList(condition1, value1, condition2, value2, page, size);
	}
}
