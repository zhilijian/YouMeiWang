package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.Admin;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.util.RandomUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.ExtraVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@PostMapping("/login")
	public CommonVO login(@RequestParam(name="adminname", required=true) String adminname, 
						@RequestParam(name="password", required=true) String password, 
						HttpSession session) {
		Map<String, Object> data = new HashMap<String, Object>();
		Admin admin = adminService.queryAdmin("adminname", adminname, "password", password);
		if (admin != null) {
			session.setAttribute(admin.getAdminID(), admin.getAdminID());
			data.put("adminID", admin.getAdminID());
			data.put("adminname", adminname);
			data.put("position", admin.getPosition());
			data.put("userManage", admin.getUserManage());
			data.put("workManage", admin.getWorkManage());
			data.put("homepageModule", admin.getHomepageModule());
			data.put("rechargeManage", admin.getRechargeManage());
			data.put("roleAuthority", admin.getRoleAuthority());
			return new CommonVO(true, "用户登录成功！", data);
		} else {
			return new CommonVO(false, "用户登录失败。", "请先注册后登录。");
		}
	}
	
	@GetMapping("/logout")
	public SimpleVO logout(@RequestParam(name="adminID", required=true) String adminID, 
						HttpSession session) {
		try {
			session.removeAttribute(adminID);
			if (session.getAttribute(adminID) == null) {
				return new SimpleVO(true, "管理员退出成功！");
			} else {
				return new SimpleVO(false, "管理员退出失败。");
			}
		} catch (Exception e) {
			return new SimpleVO(false, e.getMessage());
		}
	}
	
	@PostMapping("/addadmin")
	public CommonVO addAdmin(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="adminname", required=true) String adminname,
							@RequestParam(name="password", required=true) String password,
							@RequestParam(name="position", required=true) String position,
							@RequestParam(name="userManage", required=false) Integer[] userManage,
							@RequestParam(name="workManage", required=false) Integer[] workManage,
							@RequestParam(name="homepageModule", required=false) Integer[] homepageModule,
							@RequestParam(name="rechargeManage", required=false) Integer[] rechargeManage,
							@RequestParam(name="roleAuthority", required=false) Integer[] roleAuthority,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getRoleAuthority(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限", "请先核对该用户是否有此权限。");
		}
		
		Admin admin = new Admin();
		String newAdminID = null;
		do {
			newAdminID = RandomUtil.getRandomNumber(4);
		} while (adminService.queryAdmin("adminID", newAdminID) != null);
		admin.setAdminID(newAdminID);
		admin.setAdminname(adminname);
		admin.setPassword(password);
		admin.setPosition(position);
		admin.setUserManage(userManage);
		admin.setWorkManage(workManage);
		admin.setHomepageModule(homepageModule);
		admin.setRechargeManage(rechargeManage);
		admin.setRoleAuthority(roleAuthority);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("adminID", admin.getAdminID());
		data.put("adminname", admin.getAdminname());
		data.put("position", admin.getPosition());
		data.put("userManage", admin.getUserManage());
		data.put("workManage", admin.getWorkManage());
		data.put("homepageModule", admin.getHomepageModule());
		data.put("rechargeManage", admin.getRechargeManage());
		data.put("roleAuthority", admin.getRoleAuthority());
		
		try {
			adminService.addAdmin(admin);
			return new CommonVO(true, "添加管理员成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "添加管理员失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@RequestMapping("/editpassword")
	public SimpleVO editPassword(@RequestParam(name="adminID", required=true) String adminID,
								@RequestParam(name="password1", required=true) String password1,
								@RequestParam(name="password2", required=true) String password2,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (!admin.getPassword().equals(password1)) {
			return new SimpleVO(false, "旧密码输入错误。");
		}
		try {
			admin.setPassword(password2);
			adminService.updateAdmin(admin);
			return new SimpleVO(true, "密码修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@PostMapping("/editadmin")
	public SimpleVO editadmin(@RequestParam(name="adminID1", required=true) String adminID1,
							@RequestParam(name="adminID2", required=true) String adminID2,
							@RequestParam(name="password", required=false) String password,
							@RequestParam(name="position", required=true) String position,
							@RequestParam(name="userManage", required=true) Integer[] userManage,
							@RequestParam(name="workManage", required=true) Integer[] workManage,
							@RequestParam(name="homepageModule", required=true) Integer[] homepageModule,
							@RequestParam(name="rechargeManage", required=true) Integer[] rechargeManage,
							@RequestParam(name="roleAuthority", required=true) Integer[] roleAuthority,
							HttpSession session) {
		
//		if (session.getAttribute(adminID1) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID1).getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (adminID2.equals("8888")) {
			return new SimpleVO(false, "超级管理员权限无法修改。");
		}
		
		Admin admin = adminService.queryAdmin("adminID", adminID2);
		try {
			admin.setPassword(password);
			admin.setPosition(position);
			admin.setUserManage(userManage);
			admin.setWorkManage(workManage);
			admin.setHomepageModule(homepageModule);
			admin.setRechargeManage(rechargeManage);
			admin.setRoleAuthority(roleAuthority);
			adminService.updateAdmin(admin);
			return new SimpleVO(true, "权限修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/removeadmin")
	public SimpleVO removeAdmin(@RequestParam(name="adminID1", required=true) String adminID1,
								@RequestParam(name="adminID2", required=true) String adminID2,
								HttpSession session) {
		
//		if (session.getAttribute(adminID1) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID1).getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (adminID2.equals("8888")) {
			return new SimpleVO(false, "超级管理员无法移除。");
		}
		
		try {
			adminService.removeAdmin("adminID", adminID2);
			return new SimpleVO(true, "移除管理员成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/adminlist")
	public ExtraVO adminList(@RequestParam(name="adminID", required=true) String adminID, 
								@RequestParam(name="position", required=false) String position,
								@RequestParam(name="page", required=true) Integer page,
								@RequestParam(name="size", required=true) Integer size,
								HttpSession session) {
//		if (session.getAttribute(adminID) == null) {
//			return new ExtraVO(false, "该用户尚未登录。", "请先确认是否登录成功。", null);
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getRoleAuthority(), 1);
		if (!flag) {
			return new ExtraVO(false, "该用户无此权限。","请先申请查看管理员的权限。", null);
		}
		
		if (page <= 0 || size <= 0) {
			return new ExtraVO(false, "参数输入不合理。","请先核对是否正确输入参数。", null);
		}
		
		try {
			List<Admin> adminlist = adminService.adminList("position", position, page, size);
			Long adminAmount = adminService.getAmount("position", position);
			Long pageAmount = 0l;
			if (adminAmount % size == 0) {
				pageAmount = adminAmount / size;
			} else {
				pageAmount = adminAmount / size + 1;
			}
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			for (Admin admin : adminlist) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("adminID", admin.getAdminID());
				map.put("position", admin.getPosition());
				map.put("userManage", admin.getUserManage());
				map.put("workManage", admin.getWorkManage());
				map.put("homepageModule", admin.getHomepageModule());
				map.put("rechargeManage", admin.getRechargeManage());
				map.put("roleAuthority", admin.getRoleAuthority());
				data.add(map);
			}
			Map<String, Object> extra = new HashMap <String, Object>();
			extra.put("adminAmount", adminAmount);
			extra.put("pageAmount", pageAmount);
			return new ExtraVO(true, "返回所有管理员成功！", data, extra);
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtraVO(false,"返回所有管理员失败。", "出错信息：" + e.getMessage(), null);
		}
	}
	
	@GetMapping("/batchremove")
	public SimpleVO BatchRemoveAdmin(@RequestParam(name="adminID", required=true) String adminID, 
								@RequestParam(name="adminIDs", required=true) String[] adminIDs,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		for (String adminId : adminIDs) {
			if (adminId.equals(adminID)) {
				return new SimpleVO(false, "管理员无法移除自身。");
			}
			if (adminId.equals("8888")) {
				return new SimpleVO(false, "超级管理员无法移除。");
			}
		}
		
		try {
			adminService.batchRemoveAdmin("adminID", adminIDs);
			return new SimpleVO(true, "批量删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
}
