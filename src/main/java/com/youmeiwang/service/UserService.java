package com.youmeiwang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.entity.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	public void addUser(User user) {
		userDao.addUser(user);
	}
	
	public void removeUserByCondition(String condition, String value) {
		userDao.removeUser(condition, value);
	}

	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	public User queryUserByCondition(String condition, String value) {
		return userDao.queryUser(condition, value);
	}
	
	public Long getAmount(String condition, String value) {
		return userDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3) {
		return userDao.getAmount(condition1, value1, condition2, value2, condition3, value3);
	}
	
	public List<User> userList(String condition, String value, Integer page, Integer size) {
		return userDao.userList(condition, value, page, size);
	}
	
	public List<User> userList(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3, Integer page, Integer size) {
		return userDao.userList(condition1, value1, condition2, value2, condition3, value3, page, size);
	}
}
