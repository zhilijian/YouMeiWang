package com.youmeiwang.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	public void addWork(Work work) {
		workDao.addWork(work);
	}
	
	public void addCollectWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> collectWork = ListUtil.addElement(user.getCollectWork(), workID);
		user.setVerifyingWork(collectWork);
		userDao.updateUser(user);
	}
	
	public void removeWork(String condition, Object value) {
		workDao.removeWork(condition, value);
	}
	
	public void removeWork(String condition1, Object value1, String condition2, Object value2) {
		workDao.removeWork(condition1, value1, condition2, value2);
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
	
	public Long getAmount(String condition, Object value) {
		return workDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition1, String value1, String condition2, Object value2) {
		return workDao.getAmount(condition1, value1, condition2, value2);
	}
	
	public Long getAmount(String condition1, Object value1, String condition2, Object value2, String condition3, Object value3) {
		return workDao.getAmount(condition1, value1, condition2, value2, condition3, value3);
	}
	
	public List<Work> workSortDESC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		return workDao.workSortDESC(condition1, value1, condition2, value2, condition3, limit);
	}
	
	public List<Work> workSortASC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		return workDao.workSortASC(condition1, value1, condition2, value2, condition3, limit);
	}
	
	public List<Work> workSortDESC(String condition, Map<String, Object> conditions, Integer limit) {
		return workDao.workSortDESC(condition, conditions, limit);
	}
	
	public List<Work> workList(String condition, List<String> values) {
		List<Work> worklist = new LinkedList<Work>();
		for (String value : values) {
			worklist.add(workDao.queryWork(condition, value));
		}
		return worklist;
	}
	
	public List<Work> workList(String condition, String value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
	
	public List<Work> workList(Boolean flag, String condition1, String value1, 
			String condition2, Integer value2, String condition3, Integer value3, Integer page, Integer size) {
		return workDao.workList(flag, condition1, value1, condition2, value2, condition3, value3, page, size);
	}
	
	public List<Work> workList(String condition1, Object value1, 
			String condition2, Object value2, String condition3, Object value3, Integer page, Integer size) {
		return workDao.workList(condition1, value1, condition2, value2, condition3, value3, page, size);
	}
	
	public List<Work> workList(Integer searchTpye, String condition, String value, Map<String, Object> conditions, Integer page, Integer size) {
		return workDao.workList(searchTpye, condition, value, conditions, page, size);
	}
}
