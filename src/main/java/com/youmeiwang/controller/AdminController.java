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
import com.youmeiwang.vo.CommonVO;
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
		
		Admin admin = adminService.queryAdmin("adminname", adminname, "password", password);
		if (admin == null) {
			return new CommonVO(false, "该管理员不存在。", "{}");
		}
		
		try {
			String adminID = admin.getAdminID();
			session.setAttribute("adminID", adminID);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("adminID", admin.getAdminID());
			data.put("adminname", adminname);
			data.put("position", admin.getPosition());
			data.put("userManage", admin.getUserManage());
			data.put("workManage", admin.getWorkManage());
			data.put("homepageModule", admin.getHomepageModule());
			data.put("rechargeManage", admin.getRechargeManage());
			data.put("roleAuthority", admin.getRoleAuthority());
			return new CommonVO(true, "用户登录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "用户登录失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/logout")
	public SimpleVO logout(HttpSession session) {
		
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		try {
			session.removeAttribute(adminID);
			if (session.getAttribute(adminID) == null) {
				return new SimpleVO(true, "管理员退出成功！");
			} else {
				return new SimpleVO(false, "管理员退出失败。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/addadmin")
	public SimpleVO addAdmin(@RequestParam(name="adminname", required=true) String adminname,
			@RequestParam(name="password", required=true) String password,
			@RequestParam(name="position", required=true) String position,
			@RequestParam(name="userManage", required=false) Integer[] userManage,
			@RequestParam(name="workManage", required=false) Integer[] workManage,
			@RequestParam(name="homepageModule", required=false) Integer[] homepageModule,
			@RequestParam(name="rechargeManage", required=false) Integer[] rechargeManage,
			@RequestParam(name="roleAuthority", required=false) Integer[] roleAuthority,
			HttpSession session) {
		
		String adminID = (String) session.getAttribute("adminID");
		Admin admin1 = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin1 == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin1.getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限");
		}
		
		try {
			adminService.addAdmin(adminname, password, position, userManage, 
					workManage, homepageModule, rechargeManage, roleAuthority);
			return new SimpleVO(true, "添加管理员成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/editpassword")
	public SimpleVO editPassword(@RequestParam(name="password1", required=true) String password1,
			@RequestParam(name="password2", required=true) String password2,
			HttpSession session) {
		
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		try {
			String password = admin.getPassword();
			if (!password.equals(password1)) {
				return new SimpleVO(false, "旧密码输入错误。");
			}
			adminService.setAdmin("adminID", adminID, "password", password2);
			return new SimpleVO(true, "密码修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/editadmin")
	public SimpleVO editadmin(@RequestParam(name="adminID", required=true) String adminID2,
			@RequestParam(name="password", required=false) String password,
			@RequestParam(name="position", required=true) String position,
			@RequestParam(name="userManage", required=true) Integer[] userManage,
			@RequestParam(name="workManage", required=true) Integer[] workManage,
			@RequestParam(name="homepageModule", required=true) Integer[] homepageModule,
			@RequestParam(name="rechargeManage", required=true) Integer[] rechargeManage,
			@RequestParam(name="roleAuthority", required=true) Integer[] roleAuthority,
			HttpSession session) {
		
		String adminID1 = (String) session.getAttribute("adminID");
		Admin admin1 = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin1 == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin1.getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (adminID2.equals("8888")) {
			return new SimpleVO(false, "超级管理员权限无法修改。");
		}
		
		Admin admin2 = adminService.queryAdmin("adminID", adminID2);
		try {
			if (password != null) {
				admin2.setPassword(password);
			}
			admin2.setPosition(position);
			if (userManage == null) {
				userManage = new Integer[] {};
			}
			admin2.setUserManage(userManage);
			if (workManage == null) {
				workManage = new Integer[] {};
			}
			admin2.setWorkManage(workManage);
			if (homepageModule == null) {
				homepageModule = new Integer[] {};
			}
			admin2.setHomepageModule(homepageModule);
			if (rechargeManage == null) {
				rechargeManage = new Integer[] {};
			}
			admin2.setRechargeManage(rechargeManage);
			if (roleAuthority == null) {
				roleAuthority = new Integer[] {};
			}
			admin2.setRoleAuthority(roleAuthority);
			adminService.updateAdmin(admin2);
			return new SimpleVO(true, "权限修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/removeadmin")
	public SimpleVO removeAdmin(@RequestParam(name="adminID", required=true) String adminID2, HttpSession session) {
		
		String adminID1 = (String) session.getAttribute("adminID");
		Admin admin1 = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin1 == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin1.getRoleAuthority(), 0);
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
	public CommonVO adminlist(@RequestParam(name="position", required=false) String position,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
		String adminID = (String) session.getAttribute("adminID");
		Admin admin1 = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin1 == null) {
			return new CommonVO(false, "用户尚未登录或不存在。","{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin1.getRoleAuthority(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。","请先核对是否正确输入参数。");
		}
		
		try {
			List<Admin> adminlist1 = adminService.adminlist("position", position);
			List<Admin> adminlist2 = new LinkedList<Admin>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < adminlist1.size()-currIdx; i++) {
				Admin admin2 = adminlist1.get(currIdx + i);
				adminlist2.add(admin2);
			}
			
			Long adminAmount = (long) adminlist1.size();
			Long pageAmount = 0l;
			if (adminAmount % size == 0) {
				pageAmount = adminAmount / size;
			} else {
				pageAmount = adminAmount / size + 1;
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Admin admin3 : adminlist2) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("adminID", admin3.getAdminID());
				map.put("position", admin3.getPosition());
				map.put("userManage", admin3.getUserManage());
				map.put("workManage", admin3.getWorkManage());
				map.put("homepageModule", admin3.getHomepageModule());
				map.put("rechargeManage", admin3.getRechargeManage());
				map.put("roleAuthority", admin3.getRoleAuthority());
				maplist.add(map);
			}
			
			Map<String, Object> data = new HashMap <String, Object>();
			data.put("admins", maplist);
			data.put("adminAmount", adminAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "返回所有管理员成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"返回所有管理员失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/batchremove")
	public SimpleVO BatchRemoveAdmin(@RequestParam(name="adminIDs", required=true) String[] adminIDs, HttpSession session) {
		
		String adminID1 = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			for (String adminID2 : adminIDs) {
				if (adminID2.equals("8888")) {
					continue;
				}
				adminService.removeAdmin("adminID", adminID2);
			}
			return new SimpleVO(true, "批量删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
}
