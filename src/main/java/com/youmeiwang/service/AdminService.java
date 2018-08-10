package com.youmeiwang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.AdminDao;
import com.youmeiwang.entity.Admin;

@Service
public class AdminService {

	@Autowired
	private AdminDao adminDao;
	
	public void addAdmin(Admin admin) {
		adminDao.addAdmin(admin);
	}
	
	public void removeAdmin(String condition, String value) {
		adminDao.removeAdmin(condition, value);
	}
	
	public void batchRemoveAdmin(String condition, String[] values) {
		adminDao.batchRemoveAdmin(condition, values);
	}
	
	public void updateAdmin(Admin admin) {
		adminDao.updateAdmin(admin);
	}
	
	public Admin queryAdminByCondition(String condition, String value) {
		return adminDao.queryAdmin(condition, value);
	}

	public Admin queryAdminByConditions(String condition1, String value1, String condition2, String value2) {
		return adminDao.queryAdmin(condition1, value1, condition2, value2);
	}
	
	public Long getAmount(String condition, String value) {
		return adminDao.getAmount(condition, value);
	}
	
	public List<Admin> adminList(String condition, String value, Integer page, Integer size) {
		return adminDao.adminList(condition, value, page, size);
	}
	
}
