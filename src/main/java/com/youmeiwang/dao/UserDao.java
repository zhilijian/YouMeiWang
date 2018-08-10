package com.youmeiwang.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.User;

@Repository
public interface UserDao {

	public void addUser(User user);
	
	public void removeUser(String condition, String value);
	
	public void batchRemoveUser(String condition, String[] values);
	
	public void updateUser(User user);

	public User queryUser(String condition, String value);
	
	public Long getAmount(String condition, String value);
	
	public Long getAmount(String condition1, String value1, String condition2, 
			Integer value2, String condition3, Integer value3);
	
	public List<User> userList(String condition, String value, Integer page, Integer size);

	public List<User> userList(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3, Integer page, Integer size);
}
