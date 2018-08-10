package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Topic;

@Repository
public interface TopicDao {

	public void addTopic(Topic topic);
	
	public void removeTopic(String condition, String value);
	
	public void batchRemoveTopic(String condition, String[] values);

	public void updateTopic(Topic topic);
	
	public void changeIsRecommend(String topicID, Integer isRecommend);
	
	public Topic queryTopic(String condition, String value);
	
	public Long getTopicAmount(String condition1, String value1, String condition2, Integer value2);
	
	public List<Topic> topicList();
	
	public List<Topic> topicList(String condition1, String value1, String condition2, Integer value2, Integer page, Integer size);
	
}
