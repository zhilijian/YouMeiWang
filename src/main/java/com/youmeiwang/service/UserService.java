package com.youmeiwang.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.entity.User;
import com.youmeiwang.util.RandomUtil;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private FileService fileService;

	public User addUser(String username) {
		User user = new User();
		String userID;
		do {
			userID = RandomUtil.getRandomNumber(8);
		} while (userDao.queryUser("userID", userID) != null);
		user.setUserID(userID);
		user.setUsername(username);
		user.setNickname("游模网_游客");
		user.setSex(0);
		user.setYoubiAmount(0l);
		user.setBalance(0.0);
		List<Integer> vipKind = new ArrayList<Integer>();
		vipKind.add(0);
		user.setVipKind(vipKind);
		user.setPortrait(fileService.getFilePath("000000000001"));
		user.setMemberKind(0);
		user.setVerifyingWork(new ArrayList<String>());
		user.setVerifiedWork(new ArrayList<String>());
		user.setNotPassWork(new ArrayList<String>());
		user.setCollectWork(new ArrayList<String>());
		user.setCollectTopic(new ArrayList<String>());
		user.setDownWork(new ArrayList<String>());
		user.setPurchaseWork(new ArrayList<String>());
		user.setApplyForOriginal(0);
		userDao.addUser(user);
		return user;
	}
	
	public void removeUser(String condition, String value) {
		userDao.removeUser(condition, value);
	}
	
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	public User queryUser(String condition, String value) {
		return userDao.queryUser(condition, value);
	}
	
	public void setUser(String condition, Object value1, String target, Object value2) {
		userDao.setUser(condition, value1, target, value2);
	}
	
	public String queryNickname(String condition, String value) {
		User user = userDao.queryUser(condition, value);
		return user.getNickname();
	}
	
	public Long getAmount(String condition, String value) {
		return userDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition, Object value) {
		return userDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition1, String value1, String condition2, Integer value2, 
			String condition3, Integer value3) {
		return userDao.getAmount(condition1, value1, condition2, value2, condition3, value3);
	}
	
	public List<User> userlist() {
		return userDao.userlist();
	}
	
	public List<User> userList(String condition, Object value, Integer page, Integer size) {
		return userDao.userList(condition, value, page, size);
	}
	
	public List<User> userlist(String condition, Integer VIPKind, Integer memberKind, Integer applyForOriginal) {
		return userDao.userList(condition, VIPKind, memberKind, applyForOriginal);
	}
	
	public List<User> userlist(String condition1, Object value1, String condition2, String value2) {
		return userDao.userlist(condition1, value1, condition2, value2);
	}
}
