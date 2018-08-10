package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Admin;

@Repository
public interface AdminDao {

	public void addAdmin(Admin admin);
	
	public void removeAdmin(String condition, String value);
	
	public void batchRemoveAdmin(String condition, String[] values);

	public void updateAdmin(Admin admin);
	
	public Admin queryAdmin(String condition, String value);

	public Admin queryAdmin(String condition1, String value1, String condition2, String value2);
	
	public Long getAmount(String condition, String value);
	
	public List<Admin> adminList(String condition, String value, Integer page, Integer size);
	
}
