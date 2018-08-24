package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.BBS;

@Repository
public interface BBSDao {
	
	public void addBBS(BBS bbs);
	
	public void removeBBS(String condition, Object value);
	
	public void batchRemoveBBS(String condition, Object[] values);
	
	public void updateBBS(BBS bbs);
	
	public Long getBBSAmount(String condition, Object value);
	
	public Long getBBSAmount(String condition1, Object value1, String condition2, Object value2);
	
	public BBS queryBBS(String condition, Object value);
	
	public BBS queryBBS(String condition1, Object value1, String condition2, Object value2);
	
	public List<BBS> bbsList(String condition, Object value, Integer page, Integer size);
	
	public List<BBS> bbsList(String condition1, Object value1, String condition2, Object value2, Integer page, Integer size);
}
