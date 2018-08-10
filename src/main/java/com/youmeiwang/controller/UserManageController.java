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

import com.youmeiwang.entity.User;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.ExtraVO;

@CrossOrigin
@RestController
@RequestMapping("/usermanage")
public class UserManageController {

	@Autowired
	private UserService userService;
	
//	@Autowired
//	private WorkService workService;
	
	@Autowired
	private AdminService adminService;
	
	@PostMapping("/usersearch")
	public ExtraVO userSearch(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="condition", required=false) String condition,
							@RequestParam(name="VIPKind", required=false) Integer VIPKind,
							@RequestParam(name="memberKind", required=false) Integer memberKind,
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new ExtraVO(false, "管理员尚未登录。", "请先确认是否登录成功。", null);
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdminByCondition("adminID", adminID).getAdminManager(), 0);
		if (!flag) {
			return new ExtraVO(false, "该用户无此权限。","请先申请查看管理员的权限。", null);
		}
		
		if (page <= 0 || size <= 0) {
			return new ExtraVO(false, "参数输入不合理。","请先核对是否正确输入参数。", null);
		}
		
		try {
			List<User> userlist = new LinkedList<User>();
			Long userAmount = 0l;
			if (condition != null) {
				if (userService.queryUserByCondition("userID", condition) != null) {
					userlist = userService.userList("userID", condition, "VIPKind", VIPKind, "memberKind", memberKind, page, size);
					userAmount = userService.getAmount("userID", condition, "VIPKind", VIPKind, "memberKind", memberKind);
				} else if (userService.queryUserByCondition("phone", condition) != null) {
					userlist = userService.userList("phone", condition, "VIPKind", VIPKind, "memberKind", memberKind, page, size);
					userAmount = userService.getAmount("phone", condition, "VIPKind", VIPKind, "memberKind", memberKind);
				} else if (userService.queryUserByCondition("nickname", condition) != null) {
					userlist = userService.userList("nickname", condition, "VIPKind", VIPKind, "memberKind", memberKind, page, size);
					userAmount = userService.getAmount("nickname", condition, "VIPKind", VIPKind, "memberKind", memberKind);
				} else {
					return new ExtraVO(false, "用户查询失败。", "该搜索条件无法搜索到用户。", null);
				}
			} else {
				userlist = userService.userList("userID", condition, "VIPKind", VIPKind, "memberKind", memberKind, page, size);
				userAmount = userService.getAmount("userID", condition, "VIPKind", VIPKind, "memberKind", memberKind);
			}
			Long pageAmount = 0l;
			if (userAmount % size == 0) {
				pageAmount = userAmount / size;
			} else {
				pageAmount = userAmount / size + 1;
			}
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			for (User user : userlist) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("userID", user.getUserID());
				map.put("phone", user.getPhone());
				map.put("nickname", user.getNickname());
				map.put("VIPKind", user.getVIPKind());
				map.put("memberKind", user.getMemberKind());
				map.put("youbiAmount", user.getYoubiAmount());
				map.put("balance", user.getBalance());
				data.add(map);
			}
			Map<String, Object> extra = new HashMap <String, Object>();
			extra.put("userAmount", userAmount);
			extra.put("pageAmount", pageAmount);
			return new ExtraVO(true, "用户查询成功", data, extra);
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtraVO(false,"用户查询失败。", "出错信息：" + e.getMessage(), null);
		}
	}
	
	@GetMapping("/userdetail")
	public CommonVO userDetail(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="userID", required=true) String userID,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "管理员尚未登录。", "请先确认是否登录成功。");
//		}
		try {
			User user = userService.queryUserByCondition("userID", userID);
			if (user == null) {
				return new CommonVO(false, "该用户不存在。", "请先核对该用户是否存在。");
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("nickname", user.getNickname());
			data.put("phone", user.getPhone());
			data.put("Alipay", user.getAlipay());
			data.put("QQ", user.getQQ());
			data.put("email", user.getEmail());
			data.put("VIPKind", user.getVIPKind());
			data.put("memberKind", user.getMemberKind());
			data.put("youbiAmount", user.getYoubiAmount());
			data.put("balance", user.getBalance());
			return new CommonVO(true, "返回用户详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "返回用户详情失败。", "出错信息：" + e.getMessage());
		}
	}
	
}
