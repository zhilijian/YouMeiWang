package com.youmeiwang.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.dao.VerifyDao;
import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.util.ListUtil;

@Service
public class VerifyService implements VerifyDao{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WorkDao workDao;
	
	@Override
	public void addVerifyingWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> worklist = new LinkedList<String>();
		
		if (user.getVerifyingWork() != null) {
			worklist = user.getVerifyingWork();
		} 
		worklist.add(workID);
		user.setVerifyingWork(worklist);
		
		userDao.updateUser(user);
	}

	@Override
	public void verifyAndPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		Work work = workDao.queryWork("workID", workID);
		
		List<String> verifyingWork = ListUtil.removeElement(user.getVerifyingWork(), workID);
		work.setVerifyState(2);
		List<String> verifiedWork = ListUtil.addElement(user.getVerifiedWork(), workID);
		
		user.setVerifyingWork(verifyingWork);
		user.setVerifiedWork(verifiedWork);
		
		userDao.updateUser(user);
		workDao.updateWork(work);
	}

	@Override
	public void verifyNotPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		Work work = workDao.queryWork("workID", workID);
		
		List<String> verifyingWork = ListUtil.removeElement(user.getVerifyingWork(), workID);
		work.setVerifyState(3);
		List<String> notPassWork = ListUtil.addElement(user.getNotPassWork(), workID);
		
		user.setVerifyingWork(verifyingWork);
		user.setNotPassWork(notPassWork);
		
		userDao.updateUser(user);
		workDao.updateWork(work);
	}
	
	@Override
	public void verifiedToNotPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		Work work = workDao.queryWork("workID", workID);
		
		List<String> verifiedWork = ListUtil.removeElement(user.getVerifiedWork(), workID);
		work.setVerifyState(3);
		List<String> notPassWork = ListUtil.addElement(user.getNotPassWork(), workID);
		
		user.setVerifiedWork(verifiedWork);
		user.setNotPassWork(notPassWork);
		
		userDao.updateUser(user);
		workDao.updateWork(work);
	}

}
