package com.youmeiwang.wechat;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

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
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.UserService;

@CrossOrigin
@Controller
@RequestMapping
public class WeChatController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AliPayService alipayService;
	
	@Autowired
	private WeChatAuthService weChatAuthService;

	@GetMapping("/wechatlogin")
	public String wxLoginPage() throws Exception {
		String url = weChatAuthService.getAuthorizationUrl();
		return "redirect:" + url;
	}
	
	@GetMapping("/")
	public String wechatlogin(@RequestParam(name="code", required=true) String code,
			@RequestParam(name="state", required=false) String state) throws InterruptedException, ExecutionException, TimeoutException {
		
		JSONObject result = weChatAuthService.getUserInfo(code);
		String nickname= (String) result.get("nickname");
		Integer sex= (Integer) result.get("sex");
		String portrait = (String) result.get("headimgurl");
		User user = userService.addUser(null);
		String userID = user.getUserID();
		userService.setUser("userID", userID, "nickname", nickname);
		userService.setUser("userID", userID, "sex", sex);
		userService.setUser("userID", userID, "portrait", portrait);
		
		return "redirect:" + "http://www.linshaocong.cn/#/Wxlogin" + nickname;
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
