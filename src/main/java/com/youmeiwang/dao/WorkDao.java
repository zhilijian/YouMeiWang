package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Work;

@Repository
public interface WorkDao {

	public Work addWork(Work work);
	
	public void removeWork(String condition, Object value);

	public void updateWork(Work work);
	
	public Work queryWork(String condition, Object value);
	
	public Long getAmount(String condition, String value);
	
	public List<Work> workList(String condition, String value, Integer page, Integer size);
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size);
}
