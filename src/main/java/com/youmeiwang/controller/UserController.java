package com.youmeiwang.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.VerifyUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;
import com.youmeiwang.wechat.WeChatAuthService;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private WeChatAuthService weChatAuthService;
	
	@GetMapping("/adduser")
	public CommonVO register(@RequestParam(name="username", required=true) String username,
							@RequestParam(name="code", required=true) String code,
							HttpSession session) {
		
//		if (session.getAttribute(code) == null) {
//			return new CommonVO(false, "验证码输入错误。", "请正确输入验证码。");
//		}
//		session.removeAttribute(code);
		
		if (userService.queryUser("username", username) != null) {
			return new CommonVO(false, "该手机号已被注册。","您可直接通过该手机号登录！");
		}
		
		try {
			User user = userService.addUser(username);
			String userID = user.getUserID();
			String title = "新用户注册成功！";
			String content = "亲爱的" + username+ "，欢迎来到奇妙的游模网。";
			newsService.addNews(userID, title, content, 1);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", user.getUserID());
			data.put("username", username);
			return new CommonVO(true, "新用户注册成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "新用户注册失败。", "错误信息：" + e.toString());
		}
	}
	
	@GetMapping("/removeuser")
	public SimpleVO removeUser(@RequestParam(name="userID", required=true) String userID, 
						HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户尚未登录。"); 
//		}
		
		try {
			session.removeAttribute(userID);
			userService.removeUser("userID", userID);
			return new SimpleVO(true, "用户注销成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/login")
	public CommonVO login(@RequestParam(name="username", required=true) String username, HttpSession session) {
		
//		if (session.getAttribute("code") == null) {
//			return new CommonVO(false, "验证码输入错误。", "请正确输入验证码。");
//		}
//		session.removeAttribute("code");
		
		try {
			User user = userService.queryUser("username", username);
			if (user == null) {
				return new CommonVO(false, "用户登录失败。", "请正确输入账号信息重新登录。");
			}
			
			String userID = user.getUserID();
			session.setAttribute("userID", userID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
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
				data.put("shareVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getShareVIPTime())));
			}
			if (user.getOriginalVIPTime() == null) {
				data.put("originalVIPTime", null);
			} else {
				data.put("originalVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getOriginalVIPTime())));
			}
			if (user.getCompanyVIPTime() == null) {
				data.put("companyVIPTime", null);
			} else {
				data.put("companyVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getCompanyVIPTime())));
			}
			data.put("newsAmount", newsService.getAmount(userID));
			return new CommonVO(true, "用户登录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "用户登录失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/sendcode")
	public CommonVO wechatlogin(String code, String state) throws InterruptedException, ExecutionException, TimeoutException {
		
		try {
			JSONObject result = weChatAuthService.getUserInfo(code);
			String nickname= (String) result.get("nickname");
			Integer sex= (Integer) result.get("sex");
			String portrait = (String) result.get("headimgurl");
			User user = userService.addUser(null);
			String userID = user.getUserID();
			userService.setUser("userID", userID, "nickname", nickname);
			userService.setUser("userID", userID, "sex", sex);
			userService.setUser("userID", userID, "portrait", portrait);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("nickname", nickname);
			data.put("portrait", portrait);
			data.put("vipKind", user.getVipKind());
			data.put("memberKind", user.getMemberKind());
			data.put("applyForOriginal", user.getApplyForOriginal());
			data.put("youbiAmount", user.getYoubiAmount());
			data.put("balance", user.getBalance());
			if (user.getShareVIPTime() == null) {
				data.put("shareVIPTime", null);
			} else {
				data.put("shareVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getShareVIPTime())));
			}
			if (user.getOriginalVIPTime() == null) {
				data.put("originalVIPTime", null);
			} else {
				data.put("originalVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getOriginalVIPTime())));
			}
			if (user.getCompanyVIPTime() == null) {
				data.put("companyVIPTime", null);
			} else {
				data.put("companyVIPTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(user.getCompanyVIPTime())));
			}
			data.put("newsAmount", newsService.getAmount(userID));
			return new CommonVO(true, "用户登录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "用户登录失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/logout")
	public SimpleVO logout(HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		
		try {
			session.removeAttribute("userID");
			return new SimpleVO(true, "用户退出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/edituser")
	public CommonVO editUser(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="username", required=false) String username,
			@RequestParam(name="nickname", required=false) String nickname,
			@RequestParam(name="portrait", required=false) String portrait,
			@RequestParam(name="isApplyOriginalAuthor", required=true) Boolean isApplyOriginalAuthor,
			@RequestParam(name="fullname", required=false) String fullname,
			@RequestParam(name="phone", required=false) String phone,
			@RequestParam(name="qq", required=false) String qq,
			@RequestParam(name="email", required=false) String email,
			@RequestParam(name="alipay", required=false) String alipay,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户尚未登录。"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			if (username != null) {
				user.setUsername(username);
			}
			if (nickname != null) {
				user.setNickname(nickname);
			}
			if (portrait != null) {
				user.setPortrait(portrait);
			}
			if (isApplyOriginalAuthor) {
				if (fullname == null || phone == null || alipay == null || qq == null || email ==null) {
					return new CommonVO(false, "信息填写不完整。", "请确认信息填写完成再申请。"); 
				}
				user.setFullname(fullname);
				user.setPhone(phone);
				user.setQq(qq);
				user.setEmail(email);
				user.setAlipay(alipay);
				user.setApplyForOriginal(1);
			}
			userService.updateUser(user);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			if (nickname != null) {
				data.put("nickname", nickname);
			}
			if (portrait != null) {
				data.put("portrait", portrait);
			}
			if (nickname != null) {
				data.put("nickname", nickname);
			}
			if (fullname != null) {
				data.put("fullname", fullname);
			}
			if (phone != null) {
				data.put("phone", phone);
			}
			if (qq != null) {
				data.put("qq", qq);
			}
			if (email != null) {
				data.put("email", email);
			}
			if (alipay != null) {
				data.put("alipay", alipay);
			}
			if (isApplyOriginalAuthor) {
				data.put("applyForOriginal", user.getApplyForOriginal());
			}
			return new CommonVO(true, "信息保存成功！", data); 
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "信息保存失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/wechatlogin")
	public CommonVO wechatlogin(@RequestParam(name="unionid", required=true) String unionid, 
			@RequestParam(name="nickname", required=false) String nickname, 
			@RequestParam(name="headimgurl", required=false) String portrait, 
			@RequestParam(name="sex", required=false, defaultValue="0") Integer sex,
			@RequestParam(name="username", required=false) String username) {
		
		User user1 = userService.queryUser("unionid", unionid);
		
		if (user1 == null) {
			user1 = userService.addUser(unionid);
			userService.setUser("username", unionid, "unionid", unionid);
			userService.setUser("username", unionid, "nickname", nickname);
			userService.setUser("username", unionid, "portrait", portrait);
			userService.setUser("username", unionid, "sex", sex);
		}
		
		if (username != null && VerifyUtil.isValidPhone(username)) {
			User user2 = userService.queryUser("username", username);
			if (user2 != null) {
				return new CommonVO(false, "该手机号已注册过。", "{}");
			}
			user1.setUsername(username);
			userService.setUser("unionid", unionid, "username", username);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("userID", user1.getUserID());
		data.put("username", user1.getUsername());
		data.put("nickname", user1.getNickname());
		data.put("portrait", user1.getPortrait());
		data.put("vipKind", user1.getVipKind());
		data.put("memberKind", user1.getMemberKind());
		data.put("applyForOriginal", user1.getApplyForOriginal());
		data.put("youbiAmount", user1.getYoubiAmount());
		data.put("balance", user1.getBalance());
		return new CommonVO(true, "用户登录成功！", data);
	}
}
