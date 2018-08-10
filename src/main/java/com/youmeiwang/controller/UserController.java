package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.User;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.RandomUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/adduser")
	public CommonVO register(@RequestParam(name="phone", required=true) String phone,
							@RequestParam(name="code", required=true) String code,
							HttpSession session) {
		
//		if (session.getAttribute(code) == null) {
//			return new CommonVO(false, "验证码输入错误。", "请正确输入验证码。");
//		}
//		session.removeAttribute(code);
		
		if (userService.queryUser("phone", phone) != null) {
			return new CommonVO(false, "该手机号已被注册。","您可直接通过该手机号登录！");
		}
			
		User user = new User();
		String userID;
		do {
			userID = RandomUtil.getRandomNumber(8);
		} while (userService.queryUser("userID", userID) != null);
		user.setUserID(userID);
		user.setPhone(phone);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("userID", userID);
		data.put("phone", phone);
		
		try {
			userService.addUser(user);
			return new CommonVO(true, "新用户注册成功！", data);
		} catch (Exception e) {
			return new CommonVO(false, "新用户注册失败。", "错误信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/login")
	public CommonVO login(@RequestParam(name="phone", required=true) String phone, 
						@RequestParam(name="code", required=true) String code, 
						HttpSession session) {
		
//		if (session.getAttribute(code) == null) {
//			return new CommonVO(false, "验证码输入错误。", "请正确输入验证码。");
//		}
//		session.removeAttribute(code);
		
		User user = userService.queryUser("phone", phone);
		Map<String, Object> data = new HashMap<String, Object>();
		if (user != null) {
			String userID = user.getUserID();
			session.setAttribute(userID, userID);
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("phone", user.getPhone());
			return new CommonVO(true, "用户登录成功！", user);
		} else {
			return new CommonVO(false, "用户登录失败。", "请先注册后登录。");
		}
	}
	
	@GetMapping("/logout")
	public SimpleVO logout(@RequestParam(name="userID", required=true) String userID, 
						HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		try {
			session.removeAttribute(userID);
			if (session.getAttribute(userID) == null) {
				return new SimpleVO(true, "用户退出成功！");
			} else {
				return new SimpleVO(false, "用户退出失败。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/canceluser")
	public SimpleVO removeUser(@RequestParam(name="userID", required=true) String userID, 
						HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		
		try {
			session.removeAttribute(userID);
			if (session.getAttribute(userID) == null) {
				return new SimpleVO(true, "用户注销成功！");
			} else {
				return new SimpleVO(false, "用户注销失败。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/isvalidphone")
	public SimpleVO isValidPhone(@RequestParam(name="phone", required=true) String phone) {
		User user = userService.queryUser("phone", phone);
		if (user == null) {
			return new SimpleVO(true, "该手机未注册，可用！");
		} else {
			return new SimpleVO(false, "该手机已注册，不可用。");
		}
	}
}
