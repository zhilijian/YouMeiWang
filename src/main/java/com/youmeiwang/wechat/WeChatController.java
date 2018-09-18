package com.youmeiwang.wechat;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.AliPayService;
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.UserService;

@CrossOrigin
@Controller
@RequestMapping("/redirect")
public class WeChatController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AliPayService alipayService;
	
	@Autowired
	private WeChatAuthService weChatAuthService;
	
	@Autowired
	private NewsService newsService;

	@GetMapping("/wechatlogin")
	public String wechatlogin() throws Exception {
		String url = weChatAuthService.getAuthorizationUrl();
		return "redirect:" + url;
	}
	
	@GetMapping("/wechatuser")
	public String wechatuser(@RequestParam(name="code", required=true) String code,
			@RequestParam(name="state", required=false) String state,
			HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
		
		JSONObject result = weChatAuthService.getUserInfo(code);
		String unionid = (String) result.get("unionid");
		String nickname= (String) result.get("nickname");
		Integer sex= (Integer) result.get("sex");
		String portrait = (String) result.get("headimgurl");
		
		User user = userService.queryUser("unionid", unionid);
		if (user == null) {
			user = userService.addUser(unionid);
			userService.setUser("username", unionid, "unionid", unionid);
			userService.setUser("username", unionid, "nickname", nickname);
			userService.setUser("username", unionid, "portrait", portrait);
			userService.setUser("username", unionid, "sex", sex);
			String userID = user.getUserID();
			String title = "新用户注册成功！";
			String content = "亲爱的" + nickname + "先生/女士，欢迎来到奇妙的游模网。";
			newsService.addNews(userID, title, content, 1);
		} else {
			unionid = user.getUsername();
		}
		
		session.setAttribute("code", "123456");
		return "redirect:" + "http://www.linshaocong.cn:8019/#/Wxlogin?username=" + unionid;
	}
	
	@GetMapping("/alipayreturn")
	public String alipayReturn(HttpServletRequest request) {
		Map<String, String> responseMap = alipayService.receiveOrder(request);
		if (responseMap == null) { 
			return null;
		}
		try {
			String outTradeNo = responseMap.get("out_trade_no");
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			String workID = order.getProductID();
			if (workID != null) {
				return "redirect:http://www.linshaocong.cn:8019/#/Details?workID=" + workID;
			} else {
				return "redirect:http://www.linshaocong.cn:8019/#/PersonalInformation";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
