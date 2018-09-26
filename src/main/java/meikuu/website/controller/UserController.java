package meikuu.website.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import meikuu.domain.entity.user.User;
import meikuu.domain.util.VerifyUtil;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/loginorregister")
	public CommonVO loginOrRegister(@RequestParam(name="username", required=true) String username,
			@RequestParam(name="loginOrRegister", required=true) boolean loginOrRegister,
			HttpSession session) {
		
//		if (session.getAttribute("code") == null) {
//			return new CommonVO(false, "验证码输入错误。", "{}");
//		}
//		session.removeAttribute("code");
		
		try {
			User user = new User();
			if (loginOrRegister) {
				user = userService.queryUser("username", username);
				if (user == null) {
					return new CommonVO(false, "不存在此账号的用户。", "{}");
				}
				if (user.isBanLogin()) {
					return new CommonVO(false, "违规操作，禁止登录，详情请联系客服。", "{}");
				}
				
			} else {
				if (userService.queryUser("username", username) != null) {
					return new CommonVO(false, "该手机号已被注册。","{}");
				}
				user = userService.addUser(username);
				
				String userID = user.getUserID();
				String title = "新用户注册成功！";
				String content = "亲爱的" + username+ "先生/女士，欢迎来到奇妙的游模网。";
				newsService.addNews(userID, title, content, 1);
			}
			
			String userID = user.getUserID();
			String userToken = cmdService.getUserSessionID(userID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("userToken", userToken);
			data.put("username", user.getUsername());
			data.put("nickname", user.getNickname());
			data.put("portrait", user.getPortrait());
			data.put("vipKind", user.getVipKind());
			data.put("memberKind", user.getMemberKind());
			data.put("applyForOriginal", user.getApplyForOriginal());
			data.put("youbiAmount", user.getYoubiAmount());
			data.put("balance", user.getBalance());
			if (user.getShareVIPTime() == null) {
				data.put("shareVIPTime", null);
			} else {
				data.put("shareVIPTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getShareVIPTime())));
			}
			if (user.getOriginalVIPTime() == null) {
				data.put("originalVIPTime", null);
			} else {
				data.put("originalVIPTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getOriginalVIPTime())));
			}
			if (user.getCompanyVIPTime() == null) {
				data.put("companyVIPTime", null);
			} else {
				data.put("companyVIPTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getCompanyVIPTime())));
			}
			data.put("newsAmount", newsService.getAmount(userID));
			return new CommonVO(true, "用户登录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "用户登录失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/logout")
	public SimpleVO logout(@RequestParam(name="userToken", required=true) String sessionId) {
		
		try {
			String userID = cmdService.getIDBySessionId(sessionId);
			if (userID == null) {
				return new SimpleVO(false, "用户尚未登录。");
			}
			cmdService.removeSession(userID);
			return new SimpleVO(true, "用户退出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/edituser")
	public CommonVO editUser(@RequestParam(name="username", required=false) String username,
			@RequestParam(name="nickname", required=false) String nickname,
			@RequestParam(name="portrait", required=false) String portrait,
			@RequestParam(name="isApplyOriginalAuthor", required=true) Boolean isApplyOriginalAuthor,
			@RequestParam(name="fullname", required=false) String fullname,
			@RequestParam(name="phone", required=false) String phone,
			@RequestParam(name="qq", required=false) String qq,
			@RequestParam(name="email", required=false) String email,
			@RequestParam(name="alipay", required=false) String alipay,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user1 = userService.queryUser("userID", userID);
		if (userID == null || user1 == null) {
			return new CommonVO(false, "用户尚未登录。", "{}");
		}
		
		try {
			if (username != null && !"".equals(username.trim())) {
				User user2 = userService.queryUser("username", username);
				if (!VerifyUtil.isValidPhone(username) || user2 != null) {
					return new CommonVO(false, "该手机号不可用。", "{}"); 
				}
				user1.setUsername(username);
			}
			if (nickname != null) {
				user1.setNickname(nickname);
			}
			if (portrait != null && !"".equals(portrait.trim())) {
				user1.setPortrait(portrait);
			}
			if (isApplyOriginalAuthor) {
				if (fullname == null || phone == null || alipay == null || qq == null || email ==null) {
					return new CommonVO(false, "信息填写不完整。", "{}"); 
				}
				user1.setFullname(fullname);
				user1.setPhone(phone);
				user1.setQq(qq);
				user1.setEmail(email);
				user1.setAlipay(alipay);
				user1.setApplyForOriginal(1);
			}
			userService.updateUser(user1);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user1.getUsername());
			data.put("portrait", user1.getPortrait());
			data.put("nickname", user1.getNickname());
			data.put("fullname", user1.getFullname());
			data.put("phone", user1.getPhone());
			data.put("qq", user1.getQq());
			data.put("email", user1.getEmail());
			data.put("alipay", user1.getAlipay());
			data.put("applyForOriginal", user1.getApplyForOriginal());
			return new CommonVO(true, "信息保存成功！", data); 
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "信息保存失败。", "出错信息：" + e.toString());
		}
	}
}
