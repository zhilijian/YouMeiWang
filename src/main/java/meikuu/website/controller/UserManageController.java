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
import meikuu.domain.entity.user.User;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;
/**
 * 后台管理项目·用户管理
 * @author zhilijian
 */
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
	
	@Autowired
	private SessionService sessionService;
	
	/**
	 * 普通用户/原创作者 查询/搜索
	 */
	@PostMapping("/usersearch")
	public CommonVO userSearch(@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="VIPKind", required=false) Integer VIPKind,
			@RequestParam(name="memberKind", required=false) Integer memberKind,
			@RequestParam(name="isVerify", required=true) Boolean isVerify,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = sessionService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "请求失败，请重新登录。", "{}");
		}
		
		int authority = 0;
		if (isVerify) {
			authority = 1;
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getUserManage(), authority);
		if (!flag) {
			return new CommonVO(false, "该管理员无查询/搜索权限。","{}");
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "请先核对是否正确输入参数。","{}");
		}
		
		try {
			List<User> userlist = userService.userlist(condition, VIPKind, memberKind, isVerify, page, size);
			List<Map<String, Object>> users = new LinkedList<Map<String, Object>>();
			for (User user : userlist) {
				Map<String, Object> usermap = new HashMap <String, Object>();
				usermap.put("userID", user.getUserID());
				usermap.put("username", user.getUsername());
				usermap.put("nickname", user.getNickname());
				usermap.put("VIPKind", user.getVipKind());
				usermap.put("memberKind", user.getMemberKind());
				usermap.put("youbiAmount", user.getYoubiAmount());
				usermap.put("balance", user.getBalance());
				usermap.put("isBanLogin", user.isBanLogin());
				users.add(usermap);
			}
			
			Long userAmount = userService.getAmount(condition, VIPKind, memberKind, isVerify);
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
			return new CommonVO(true, "查询/搜索用户列表成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"查询/搜索用户列表失败。", "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 查询用户详情
	 */
	@GetMapping("/userdetail")
	public CommonVO userDetail(@RequestParam(name="userID", required=true) String userID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = sessionService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "请求失败，请重新登录。", "{}");
		}
		
		try {
			User user = userService.queryUser("userID", userID);
			if (user == null) {
				return new CommonVO(false, "请先核对该用户是否存在。", "{}");
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
			return new CommonVO(true, "查询用户详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询用户详情失败。", "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 单独/批量 禁止/解禁用户
	 */
	@PostMapping("/banuser")
	public SimpleVO banUser(@RequestParam(name="userIDs", required=true) String[] userIDs, 
			@RequestParam(name="banOrRelease", required=true) Boolean banOrRelease,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = sessionService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "请求失败，请重新登录。");
		}
		
		if (!"8888".equals(adminID)) {
			return new SimpleVO(false, "该管理员无禁止/解禁用户权限。");
		}
		
		try {
			if (banOrRelease) {
				for (String userID : userIDs) {
					userService.setUser("userID", userID, "isBanLogin", true);
				}
				return new SimpleVO(true, "禁止用户登录成功！");
				
			} else {
				for (String userID : userIDs) {
					userService.setUser("userID", userID, "isBanLogin", false);
				}
				return new SimpleVO(true, "解禁用户登录成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 原创作者审核（列表）
	 */
	@PostMapping("/verifyapply")
	public CommonVO verifyApply(@RequestParam(name = "userID", required = true) String userID,
			@RequestParam(name = "isPass", required = true) boolean isPass,
			@RequestParam(name = "dismissalMsg", required = false) String dismissalMsg, 
			@RequestParam(name="adminToken", required=true) String sessionId) {

		String adminID = sessionService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "请求失败，请重新登录。", "{}");
		}

		boolean flag = ContainUtil.hasNumber(admin.getUserManage(), 1);
		if (!flag) {
			return new CommonVO(false, "该管理员无原创作者审核权限。", "{}");
		}

		try {
			User user = userService.queryUser("userID", userID);
			if (user == null) {
				return new CommonVO(false, "该用户不存在或已销户", "{}");
			}
			if (isPass) {
				userService.setUser("userID", userID, "applyForOriginal", 2);
				userService.setUser("userID", userID, "memberKind", 1);
				
				String title = "申请原创作者审核通过！";
				String content = "恭喜你，您提交的原创作者申请经游模网审核通过啦！";
				newsService.addNews(userID, title, content, 1);
			} else {
				userService.setUser("userID", userID, "applyForOriginal", 3);
				userService.setUser("userID", userID, "dismissalMsg", dismissalMsg);
				
				String title = "申请原创作者审核未通过。";
				String content = "很抱歉，您提交的原创作者申请经游模网审核并未通过。您可查看驳回信息，或直接咨询我们客服人员。";
				newsService.addNews(userID, title, content, 1);
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("nickname", user.getNickname());
			data.put("applyForOriginal", user.getApplyForOriginal());
			return new CommonVO(true, "审核原创作者成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "审核原创作者失败。", "出错信息：" +  e.toString());
		}
	}
}