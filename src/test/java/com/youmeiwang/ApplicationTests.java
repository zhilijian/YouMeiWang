package com.youmeiwang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.entity.Admin;
import com.youmeiwang.service.AdminService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private AdminService adminService;
	
	@Test
	public void Test1() {
		
		String adminID = "8888";
		String adminname = "admin";
		String password = "123456";
		String position = "超级管理员";
		
		Integer[] adminManager = {0, 1, 2, 3};
		Integer[] workManager = {0, 1, 2, 3};
		Integer[] homepageModule = {0, 1, 2, 3, 4, 5};
		Integer[] rechargeManager = {0, 1, 2, 3, 4, 5};
		Integer[] roleAuthority = {0, 1};
		
		Admin admin = new Admin();
		admin.setAdminID(adminID);
		admin.setAdminname(adminname);
		admin.setPassword(password);
		admin.setPosition(position);
		admin.setAdminManager(adminManager);
		admin.setWorkManager(workManager);
		admin.setHomepageModule(homepageModule);
		admin.setRechargeManager(rechargeManager);
		admin.setRoleAuthority(roleAuthority);
		
		adminService.addAdmin(admin);
	}
	
	@Test
	public void Test2() {
		
	}
}
 