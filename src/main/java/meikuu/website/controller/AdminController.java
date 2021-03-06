package meikuu.website.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import meikuu.domain.entity.user.Admin;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.SessionService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;
/**
 * 后台管理项目·角色权限
 * @author zhilijian
 */
@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private SessionService cmdService;
	 
	/**
	 * 管理员登录
	 */
	@PostMapping("/login")
	public CommonVO login(@RequestParam(name="adminname", required=true) String adminname, 
			@RequestParam(name="password", required=true) String password) {
		
		Admin admin = adminService.queryAdmin("adminname", adminname, "password", password);
		if (admin == null) {
			return new CommonVO(false, "管理员不存在或密码错误。", "{}");
		}
		
		try {
			String adminID = admin.getAdminID();
			String adminToken = cmdService.getAdminSessionID(adminID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("adminToken", adminToken);
			data.put("adminID", admin.getAdminID());
			data.put("adminname", adminname);
			data.put("position", admin.getPosition());
			data.put("userManage", admin.getUserManage());
			data.put("workManage", admin.getWorkManage());
			data.put("homepageModule", admin.getHomepageModule());
			data.put("rechargeManage", admin.getRechargeManage());
			data.put("roleAuthority", admin.getRoleAuthority());
			data.put("dataStatistics", admin.getDataStatistics());
			return new CommonVO(true, "管理员登录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "管理员登录失败。", "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 管理员退出
	 */
	@GetMapping("/logout")
	public SimpleVO logout(@RequestParam(name="adminToken", required=true) String sessionId) {
		
		try {
			String adminID = cmdService.getIDBySessionId(sessionId);
			cmdService.removeSession(adminID);
			return new SimpleVO(true, "管理员退出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 创建管理员
	 */
	@PostMapping("/addadmin")
	public SimpleVO addAdmin(@RequestParam(name="adminname", required=true) String adminname,
			@RequestParam(name="password", required=true) String password,
			@RequestParam(name="position", required=true) String position,
			@RequestParam(name="userManage", required=false) Integer[] userManage,
			@RequestParam(name="workManage", required=false) Integer[] workManage,
			@RequestParam(name="homepageModule", required=false) Integer[] homepageModule,
			@RequestParam(name="rechargeManage", required=false) Integer[] rechargeManage,
			@RequestParam(name="roleAuthority", required=false) Integer[] roleAuthority,
			@RequestParam(name="dataStatistics", required=false) Integer[] dataStatistics,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "登录超时，请重新登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getRoleAuthority(), 0);
		if (!flag) {
			return new SimpleVO(false, "该管理员无创建管理员权限");
		}
		
		try {
			adminService.addAdmin(adminname, password, position, userManage, workManage, 
					homepageModule, rechargeManage, roleAuthority, dataStatistics);
			return new SimpleVO(true, "添加管理员成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 修改管理员密码
	 */
	@PostMapping("/editpassword")
	public SimpleVO editPassword(@RequestParam(name="password1", required=true) String password1,
			@RequestParam(name="password2", required=true) String password2,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "登录超时，请重新登录。");
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
	
	/**
	 * 修改管理员信息
	 */
	@PostMapping("/editadmin")
	public SimpleVO editadmin(@RequestParam(name="adminID", required=true) String adminID2,
			@RequestParam(name="password", required=false) String password,
			@RequestParam(name="position", required=true) String position,
			@RequestParam(name="userManage", required=true) Integer[] userManage,
			@RequestParam(name="workManage", required=true) Integer[] workManage,
			@RequestParam(name="homepageModule", required=true) Integer[] homepageModule,
			@RequestParam(name="rechargeManage", required=true) Integer[] rechargeManage,
			@RequestParam(name="roleAuthority", required=true) Integer[] roleAuthority,
			@RequestParam(name="dataStatistics", required=true) Integer[] dataStatistics,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID1 = cmdService.getIDBySessionId(sessionId);
		Admin admin1 = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin1 == null) {
			return new SimpleVO(false, "登录超时，请重新登录。");
		}
		
		if (!adminID1.equals("8888")) {
			return new SimpleVO(false, "非超级管理员无修改权限。");
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
			if (dataStatistics == null) {
				dataStatistics = new Integer[] {};
			}
			admin2.setDataStatistics(dataStatistics);
			adminService.updateAdmin(admin2);
			return new SimpleVO(true, "管理员权限修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 移除管理员
	 */
	@GetMapping("/removeadmin")
	public SimpleVO removeAdmin(@RequestParam(name="adminID", required=true) String adminID2, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID1 = cmdService.getIDBySessionId(sessionId);
		Admin admin1 = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin1 == null) {
			return new SimpleVO(false, "登录超时，请重新登录。");
		}
		
		if (!adminID1.equals("8888")) {
			return new SimpleVO(false, "非超级管理员无移除权限。");
		}
		
		if (adminID2.equals("8888")) {
			return new SimpleVO(false, "超级管理员无法移除。");
		}
		
		try {
			adminService.removeAdmin("adminID", adminID2);
			return new SimpleVO(true, "移除管理员成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 批量移除管理员
	 */
	@GetMapping("/batchremove")
	public SimpleVO BatchRemoveAdmin(@RequestParam(name="adminIDs", required=true) String[] adminIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID1 = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID1);
		if (adminID1 == null || admin == null) {
			return new SimpleVO(false, "登录超时，请重新登录。");
		}
		
		if (!adminID1.equals("8888")) {
			return new SimpleVO(false, "非超级管理员无移除权限。");
		}
		
		try {
			for (String adminID2 : adminIDs) {
				if (adminID2.equals("8888")) {
					continue;
				}
				adminService.removeAdmin("adminID", adminID2);
			}
			return new SimpleVO(true, "批量移除管理员成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 管理员列表
	 */
	@GetMapping("/adminlist")
	public CommonVO adminlist(@RequestParam(name="position", required=false) String position,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin1 = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin1 == null) {
			return new CommonVO(false, "登录超时，请重新登录。","{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin1.getRoleAuthority(), 1);
		if (!flag) {
			return new CommonVO(false, "管理员无查看管理员列表权限。","{}");
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "请先核对是否正确输入参数。","{}");
		}
		
		try {
			List<Admin> adminlist = adminService.adminlist("position", position, page, size);
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Admin admin3 : adminlist) {
				Map<String, Object> adminmap = new HashMap <String, Object>();
				adminmap.put("adminID", admin3.getAdminID());
				adminmap.put("position", admin3.getPosition());
				adminmap.put("userManage", admin3.getUserManage());
				adminmap.put("workManage", admin3.getWorkManage());
				adminmap.put("homepageModule", admin3.getHomepageModule());
				adminmap.put("rechargeManage", admin3.getRechargeManage());
				adminmap.put("roleAuthority", admin3.getRoleAuthority());
				adminmap.put("dataStatistics", admin3.getDataStatistics());
				maplist.add(adminmap);
			}

			Long adminAmount = adminService.getAmount("position", position);
			Long pageAmount = 0l;
			if (adminAmount % size == 0) {
				pageAmount = adminAmount / size;
			} else {
				pageAmount = adminAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap <String, Object>();
			data.put("admins", maplist);
			data.put("adminAmount", adminAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询管理员列表成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"查询管理员列表失败。", "出错信息：" + e.toString());
		}
	}
}
