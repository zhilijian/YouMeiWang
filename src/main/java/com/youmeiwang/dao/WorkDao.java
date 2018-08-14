package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Work;

@Repository
public interface WorkDao {

	public Work addWork(Work work);
	
	public void removeWork(String condition, Object value);
	
	public void removeWork(String condition1, Object value1, String condition2, Object value2);

	public void updateWork(Work work);
	
	public Work queryWork(String condition, Object value);
	
	public Long getAmount(String condition, String value);
	
	public Long getAmount(String condition, Object value);
	
	public Long getAmount(String condition1, String value1, String condition2, Object value2);
	
	public List<Work> workList(String condition, String value, Integer page, Integer size);
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size);
	
	public List<Work> workList(String condition1, String value1, String condition2, Object value2, String condition3, Object value3, Integer page, Integer size);
}
