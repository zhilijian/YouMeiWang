package com.youmeiwang.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import com.youmeiwang.util.RandomUtil;

@Service
public class TopicService {

	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private TopicDao topicDao;
	
	public Topic addTopic(String topicName, String picturePath, String describe, String[] workIDs) {
		String topicID = null;
		do {
			topicID = RandomUtil.getRandomNumber(5);
		} while (topicDao.queryTopic("topicID", topicID) != null);
		List<String> works = Arrays.asList(workIDs);
		
		Topic topic = new Topic();
		topic.setTopicID(topicID);
		topic.setTopicName(topicName);
		topic.setPicturePath(picturePath);
		topic.setDescribe(describe);
		topic.setIsRecommend(0);
		topic.setCreateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())));
		topic.setBrowsed(0l);
		topic.setCollected(0l);
		topic.setWorks(works);
		topicDao.addTopic(topic);
		return topic;
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
			if(works.contains(workID)) {
				continue;
			} 
			works = ListUtil.addElement(works, workID);
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
	
	public void setTopic(String condition, String value1, 
			String target, Object value2) {
		topicDao.setTopic(condition, value1, target, value2);
	}
	
	public void changeIsRecommend(String topicID, Integer isRecommend) {
		topicDao.changeIsRecommend(topicID, isRecommend);
	}
	
	public Topic queryTopic(String condition, String value) {
		return topicDao.queryTopic(condition, value);
	}
	
	public Long getTopicAmount() {
		return topicDao.getTopicAmount();
	}
	
	public Long getTopicAmount(String condition1, String value1, String condition2, Integer value2) {
		return topicDao.getTopicAmount(condition1, value1, condition2, value2);
	}
	
	public List<Topic> topiclist(boolean isRecommend) {
		return topicDao.topiclist(isRecommend);
	}
	
	public List<Topic> topicList(Map<String, Object> conditions, Integer page, Integer size) {
		return topicDao.topicList(conditions, page, size);
	}

	public List<Topic> topiclist() {
		return topicDao.topiclist();
	}
	
	public List<Topic> topiclist(String topicName, Integer isRecommend) {
		return topicDao.topiclist(topicName, isRecommend);
	}
}
