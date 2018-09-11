package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.User;

@Repository
public interface UserDao {

	public void addUser(User user);
	
	public void removeUser(String condition, String value);
	
	public void updateUser(User user);

	public User queryUser(String condition, String value);
	
	public void setUser(String condition, Object value1, String target, Object value2);
	
	public Long getAmount(String condition, String value);
	
	public Long getAmount(String condition, Object value);
	
	public Long getAmount(String condition1, String value1, String condition2, 
			Integer value2, String condition3, Integer value3);
	
	public List<User> userList(String condition, Object value, Integer page, Integer size);
	
	public List<User> userList(String condition, Integer VIPKind, Integer memberKind, Integer applyForOriginal);
	
	public List<User> userlist(String condition1, Object value1, String condition2, String value2);
	
}
