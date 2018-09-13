package com.youmeiwang.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Topic;

@Repository
public interface TopicDao {

	public void addTopic(Topic topic);
	
	public void removeTopic(String condition, String value);

	public void updateTopic(Topic topic);
	
	public void setTopic(String condition, String value1, String target, Object value2);
	
	public void changeIsRecommend(String topicID, Integer isRecommend);
	
	public Topic queryTopic(String condition, String value);
	
	public Long getTopicAmount();
	
	public Long getTopicAmount(String condition1, String value1, String condition2, Integer value2);
	
	public List<Topic> topiclist(boolean isRecommend);
	
	public List<Topic> topicList(Map<String, Object> conditions, Integer page, Integer size);
	
	public List<Topic> topicList(String condition1, String value1, String condition2, Integer value2, Integer page, Integer size);
	
	public List<Topic> topiclist();
	
	public List<Topic> topiclist(String topicName, Integer isRecommend);
}
