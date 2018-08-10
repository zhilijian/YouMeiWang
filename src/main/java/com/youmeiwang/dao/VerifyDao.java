package com.youmeiwang.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface VerifyDao {

	public void addVerifyingWork(String userID, String workID);
	
	public void verifyAndPassWork(String userID, String workID);
	
	public void verifyNotPassWork(String userID, String workID);
	
	public void verifiedToNotPassWork(String userID, String workID);
}
