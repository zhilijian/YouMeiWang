package meikuu.repertory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.user.Admin;
import meikuu.domain.util.RandomUtil;
import meikuu.repertory.dao.AdminDao;

@Service
public class AdminService {

	@Autowired
	private AdminDao adminDao;
	
	public Admin addAdmin(String adminname, String password, String position, 
			Integer[] userManage, Integer[] workManage, Integer[] homepageModule, 
			Integer[] rechargeManage, Integer[] roleAuthority, Integer[] dataStatistics) {
		Admin admin = new Admin();
		String adminID = null;
		do {
			adminID = RandomUtil.getRandomNumber(4);
		} while (adminDao.queryAdmin("adminID", adminID) != null);
		admin.setAdminID(adminID);
		admin.setAdminname(adminname);
		admin.setPassword(password);
		admin.setPosition(position);
		if (userManage == null) {
			userManage = new Integer[] {};
		}
		admin.setUserManage(userManage);
		if (workManage == null) {
			workManage = new Integer[] {};
		}
		admin.setWorkManage(workManage);
		if (homepageModule == null) {
			homepageModule = new Integer[] {};
		}
		admin.setHomepageModule(homepageModule);
		if (rechargeManage == null) {
			rechargeManage = new Integer[] {};
		}
		admin.setRechargeManage(rechargeManage);
		if (roleAuthority == null) {
			roleAuthority = new Integer[] {};
		}
		admin.setRoleAuthority(roleAuthority);
		if (dataStatistics == null) {
			dataStatistics = new Integer[] {};
		}
		admin.setDataStatistics(dataStatistics);
		adminDao.addAdmin(admin);
		return admin;
	}
	
	public void removeAdmin(String condition, String value) {
		adminDao.removeAdmin(condition, value);
	}
	
	public void updateAdmin(Admin admin) {
		adminDao.updateAdmin(admin);
	}
	
	public void setAdmin(String condition, Object value1, String target, Object value2) {
		adminDao.setAdmin(condition, value1, target, value2);
	}
	
	public Admin queryAdmin(String condition, String value) {
		return adminDao.queryAdmin(condition, value);
	}

	public Admin queryAdmin(String condition1, String value1, String condition2, String value2) {
		return adminDao.queryAdmin(condition1, value1, condition2, value2);
	}
	
	public List<Admin> adminlist(String condition, String value) {
		return adminDao.adminlist(condition, value);
	}
}
