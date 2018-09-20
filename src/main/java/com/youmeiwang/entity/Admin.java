package com.youmeiwang.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_admin")
public class Admin {
	
	//管理员ID
	@Indexed
	private String adminID;
	//管理员名称
	private String adminname;
	//密码
	private String password;
	//职位
	private String position;
	//adminManager  用户管理	0:用户查询  1：原创作者审核  2：留言反馈  3：纠错提交
	private Integer[] userManage;
	//workManager  作品管理	0：原创模型管理  1：共享模型管理  2:2D美术管理  3：作品审核
	private Integer[] workManage;
	//homepageModule  首页模块  0：banner管理  1：专题管理  2：热门排行  3：原创推荐  4：共享推荐  5:2D美术推荐
	private Integer[] homepageModule;
	//rechargeManager  充值管理  0：充值记录  1：购买记录  2：余额使用记录  3：流水查询  4：提现申请  VIP设置
	private Integer[] rechargeManage;
	//roleAuthority  角色权限  0：创建后台角色  1：查看所有角色
	private Integer[] roleAuthority;
	
	public Admin() {
	}

	public String getAdminID() {
		return adminID;
	}

	public void setAdminID(String adminID) {
		this.adminID = adminID;
	}

	public String getAdminname() {
		return adminname;
	}

	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer[] getUserManage() {
		return userManage;
	}

	public void setUserManage(Integer[] userManage) {
		this.userManage = userManage;
	}

	public Integer[] getWorkManage() {
		return workManage;
	}

	public void setWorkManage(Integer[] workManage) {
		this.workManage = workManage;
	}

	public Integer[] getHomepageModule() {
		return homepageModule;
	}

	public void setHomepageModule(Integer[] homepageModule) {
		this.homepageModule = homepageModule;
	}

	public Integer[] getRechargeManage() {
		return rechargeManage;
	}

	public void setRechargeManage(Integer[] rechargeManage) {
		this.rechargeManage = rechargeManage;
	}

	public Integer[] getRoleAuthority() {
		return roleAuthority;
	}

	public void setRoleAuthority(Integer[] roleAuthority) {
		this.roleAuthority = roleAuthority;
	}
}
