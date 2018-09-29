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
	
	/**
	 * 添加对象，并初始化部分属性
	 */
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

	/**
	 * 根据条件精确移除对象
	 */
	public void removeAdmin(String condition, Object value) {
		adminDao.removeAdmin(condition, value);
	}
	
	/**
	 * 更新对象（注意mongodb层）
	 */
	public void updateAdmin(Admin admin) {
		adminDao.updateAdmin(admin);
	}

	/**
	 * 更改目标对象的属性值
	 */
	public void setAdmin(String condition, Object value1, String target, Object value2) {
		adminDao.setAdmin(condition, value1, target, value2);
	}

	/**
	 * 单条件查询一个对象
	 */
	public Admin queryAdmin(String condition, String value) {
		return adminDao.queryAdmin(condition, value);
	}

	/**
	 * 双条件的查询一个对象，
	 * 如管理员登录需要账号和密码
	 */
	public Admin queryAdmin(String condition1, String value1, String condition2, String value2) {
		return adminDao.queryAdmin(condition1, value1, condition2, value2);
	}

	/**
	 * 单条件查询对象数量
	 */
	public Long getAmount(String condition, String value) {
		return adminDao.getAmount(condition, value);
	}

	/**
	 * 单条件查询对象集合
	 */
	public List<Admin> adminlist(String condition, String value, Integer page, Integer size) {
		return adminDao.adminlist(condition, value, page, size);
	}
}
