package com.youmeiwang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.BBSDao;
import com.youmeiwang.dao.UserDao;
import com.youmeiwang.entity.BBS;
import com.youmeiwang.entity.User;
import com.youmeiwang.util.RandomUtil;

@Service
public class BBSService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private BBSDao bbsDao;
	
	public BBS addBBS(String userID, String workID, Integer correctionType, String comment) {
		User user = userDao.queryUser("userID", userID);
		String username = user.getUsername();
		
		BBS bbs = new BBS();
		String bbsID = null;
		do {
			bbsID = RandomUtil.getRandomNumber(8);
		} while (bbsDao.queryBBS("bbsID", bbsID) != null);
		bbs.setBbsID(bbsID);
		bbs.setUserID(userID);
		bbs.setUsername(username);
		bbs.setWorkID(workID);
		bbs.setCorrectionType(correctionType);
		bbs.setComment(comment);
		if (workID == null) {
			bbs.setType(0);
		} else {
			bbs.setType(1);
		}
		bbs.setPublishTime(System.currentTimeMillis());
		bbsDao.addBBS(bbs);
		return bbs;
	}
	
	public void removeBBS(String condition, String value) {
		bbsDao.removeBBS(condition, value);
	}
	
	public void batchRemoveBBS(String condition, String[] values) {
		bbsDao.batchRemoveBBS(condition, values);
	}
	
	public void updateBBS(BBS bbs) {
		bbsDao.updateBBS(bbs);
	}
	
	public Long getBBSAmount(String condition, Object value) {
		return bbsDao.getBBSAmount(condition, value);
	}
	
	public Long getBBSAmount(String condition1, Object value1, String condition2, Object value2) {
		return bbsDao.getBBSAmount(condition1, value1, condition2, value2);
	}
	
	public BBS queryBBS(String condition, Object value) {
		return bbsDao.queryBBS(condition, value);
	}
	
	public BBS queryBBS(String condition1, Object value1, String condition2, Object value2) {
		return bbsDao.queryBBS(condition1, value1, condition2, value2);
	}
	
	public List<BBS> bbsList(String condition, Object value, Integer page, Integer size) {
		return bbsDao.bbsList(condition, value, page, size);
	}
	
	public List<BBS> bbsList(String condition1, Object value1, String condition2, 
			Object value2, Integer page, Integer size) {
		return bbsDao.bbsList(condition1, value1, condition2, value2, page, size);
	}
}
