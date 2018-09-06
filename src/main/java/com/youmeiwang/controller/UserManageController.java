package com.youmeiwang.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/usermanage")
public class UserManageController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private NewsService newsService;
	
	@PostMapping("/usersearch")
	public CommonVO userSearch(@RequestParam(name="adminID", required=true) String adminID,
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="VIPKind", required=false) Integer VIPKind,
			@RequestParam(name="memberKind", required=false) Integer memberKind,
			@RequestParam(name="applyForOriginal", required=false) Integer applyForOriginal,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "管理员尚未登录。", "请先确认是否登录成功。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。","请先核对是否正确输入参数。");
		}
		
		try {
			Set<User> userset = new HashSet<User>();
			userset.addAll(userService.userlist(condition, VIPKind, memberKind, applyForOriginal));
			
			List<User> userlist1 = new ArrayList<User>(userset);
			List<User> userlist2 = new LinkedList<User>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < userset.size()-currIdx; i++) {
				User user = userlist1.get(currIdx + i);
				userlist2.add(user);
			}
			
			List<Map<String, Object>> users = new LinkedList<Map<String, Object>>();
			for (User user : userlist2) {
				Map<String, Object> usermap = new HashMap <String, Object>();
				usermap.put("userID", user.getUserID());
				usermap.put("username", user.getUsername());
				usermap.put("nickname", user.getNickname());
				usermap.put("VIPKind", user.getVipKind());
				usermap.put("memberKind", user.getMemberKind());
				usermap.put("youbiAmount", user.getYoubiAmount());
				usermap.put("balance", user.getBalance());
				users.add(usermap);
			}
			Long userAmount = (long) userset.size();
			Long pageAmount = 0l;
			if (userAmount % size == 0) {
				pageAmount = userAmount / size;
			} else {
				pageAmount = userAmount / size + 1;
			}
			Map<String, Object> data = new HashMap <String, Object>();
			data.put("users", users);
			data.put("userAmount", userAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "用户查询成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"用户查询失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/userdetail")
	public CommonVO userDetail(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="userID", required=true) String userID,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "管理员尚未登录。", "请先确认是否登录成功。");
		}
		
		try {
			User user = userService.queryUser("userID", userID);
			if (user == null) {
				return new CommonVO(false, "该用户不存在。", "请先核对该用户是否存在。");
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("nickname", user.getNickname());
			data.put("fullname", user.getFullname());
			data.put("phone", user.getPhone());
			data.put("Alipay", user.getAlipay());
			data.put("QQ", user.getQq());
			data.put("email", user.getEmail());
			data.put("VIPKind", user.getVipKind());
			data.put("memberKind", user.getMemberKind());
			data.put("youbiAmount", user.getYoubiAmount());
			data.put("balance", user.getBalance());
			return new CommonVO(true, "返回用户详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "返回用户详情失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/batchremoveuser")
	public SimpleVO BatchRemoveUser(@RequestParam(name="adminID", required=true) String adminID, 
								@RequestParam(name="userIDs", required=true) String[] userIDs,
								HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new SimpleVO(false, "该用户尚未登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			userService.batchRemoveUser("userID", userIDs);
			return new SimpleVO(true, "批量删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@PostMapping("/verifyapply")
	public CommonVO verifyApply(@RequestParam(name = "adminID", required = true) String adminID,
			@RequestParam(name = "userID", required = true) String userID,
			@RequestParam(name = "isPass", required = true) boolean isPass,
			@RequestParam(name = "dismissalMsg", required = false) String dismissalMsg, 
			HttpSession session) {

		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "管理员尚未登录。", "请先确认是否登录成功。");
		}

		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先核对该管理员是否有此权限。");
		}

		try {
			User user = userService.queryUser("userID", userID);
			if (user == null) {
				return new CommonVO(false, "该用户不存在或已销户", "请审核其他用户。");
			}
			String username = user.getUsername();
			if (isPass) {
				userService.setUser("userID", userID, "applyForOriginal", 2);
				userService.setUser("userID", userID, "memberKind", 1);
				String title = "申请原创作者审核通过！";
				String content = "恭喜你，您提交的原创作者申请经游模网审核通过啦！";
				newsService.addNews(username, title, content, 1);
			} else {
				userService.setUser("userID", userID, "applyForOriginal", 3);
				userService.setUser("userID", userID, "dismissalMsg", dismissalMsg);
				String title = "申请原创作者审核未通过。";
				String content = "很抱歉，您提交的原创作者申请经游模网审核并未通过。您可查看驳回信息，或直接咨询我们客服人员。";
				newsService.addNews(username, title, content, 1);
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("nickname", user.getNickname());
			data.put("phone", user.getPhone());
			data.put("applyForOriginal", user.getApplyForOriginal());
			
			return new CommonVO(true, "审核原创作者成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "审核原创作者失败。", "出错信息：" +  e.getMessage());
		}
	}
}