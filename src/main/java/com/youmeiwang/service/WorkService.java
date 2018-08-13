package com.youmeiwang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.util.ListUtil;

@Service
public class WorkService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WorkDao workDao;
	
	public Work addWork(Work work) {
		return workDao.addWork(work);
	}
	
	public void addCollectWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> collectWork = ListUtil.addElement(user.getCollectWork(), workID);
		user.setVerifyingWork(collectWork);
		userDao.updateUser(user);
	}
	
	public void removeWorkByCondition(String condition, Object value) {
		workDao.removeWork(condition, value);
	}
	
	public void removeCollectWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> collectWork = ListUtil.removeElement(user.getCollectWork(), workID);
		user.setVerifyingWork(collectWork);
		userDao.updateUser(user);
	}

	public void updateWork(Work work) {
		workDao.updateWork(work);
	}
	
	public Work queryWork(String condition, Object value) {
		return workDao.queryWork(condition, value);
	}
	
	public String queryWorkName(String condition, Object value) {
		Work work = workDao.queryWork(condition, value);
		return work.getWorkName();
	}
	
	public Long getAmount(String condition, String value) {
		return workDao.getAmount(condition, value);
	}
	
	public List<Work> workList(String condition, String value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
}
