package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Admin;

@Repository
public interface AdminDao {

	public void addAdmin(Admin admin);
	
	public void removeAdmin(String condition, String value);
	
	public void updateAdmin(Admin admin);
	
	public void setAdmin(String condition, Object value1, String target, Object value2);
	
	public Admin queryAdmin(String condition, String value);

	public Admin queryAdmin(String condition1, String value1, String condition2, String value2);
	
	public List<Admin> adminlist(String condition, String value);
	
}
