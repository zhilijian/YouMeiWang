package com.youmeiwang.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.UserDao;
import com.youmeiwang.dao.VerifyDao;
import com.youmeiwang.dao.WorkDao;
import com.youmeiwang.entity.User;
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
		userDao.setUser("userID", userID, "verifyingWork", worklist);
	}

	@Override
	public void verifyAndPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> verifyingWork = ListUtil.removeElement(user.getVerifyingWork(), workID);
		List<String> verifiedWork = ListUtil.addElement(user.getVerifiedWork(), workID);
		
		userDao.setUser("userID", userID, "verifyingWork", verifyingWork);
		userDao.setUser("userID", userID, "verifiedWork", verifiedWork);
		workDao.setWork("workID", workID, "verifyState", 1);
	}

	@Override
	public void verifyNotPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> verifyingWork = ListUtil.removeElement(user.getVerifyingWork(), workID);
		List<String> notPassWork = ListUtil.addElement(user.getNotPassWork(), workID);
		
		userDao.setUser("userID", userID, "verifyingWork", verifyingWork);
		userDao.setUser("userID", userID, "notPassWork", notPassWork);
		workDao.setWork("workID", workID, "verifyState", 2);
	}
	
	@Override
	public void verifiedToNotPassWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> verifiedWork = ListUtil.removeElement(user.getVerifiedWork(), workID);
		List<String> notPassWork = ListUtil.addElement(user.getNotPassWork(), workID);
		
		userDao.setUser("userID", userID, "verifiedWork", verifiedWork);
		userDao.setUser("userID", userID, "notPassWork", notPassWork);
		workDao.setWork("workID", workID, "verifyState", 2);
	}

}
