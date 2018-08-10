package com.youmeiwang.wxpay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.User;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/wxpay")
public class WXpayController {

	@Autowired
	private WXOrderService wxOrderService;
	
	@Autowired
	private WXpayService wxpayService;
	
	@RequestMapping("/createwxorder")
	public CommonVO createWXOrder(String workID, Integer money, 
			HttpServletRequest request, HttpSession session) {
		CommonVO cvo = new CommonVO();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return new CommonVO(false, "用户非法登录。", null); 
		}
//		String reqIP = request.getRemoteAddr();
		String reqIP = "192.168.0.128";
		WXOrder wxOrder = wxOrderService.addWXOrder(user.getUserID(), 
				workID, money, reqIP);
		try {
			Map<String, String> resultMap = wxpayService.createOrder(wxOrder, reqIP);
			cvo.setSuccess(true);
			cvo.setMsg("微信支付订单发送成功！");
			cvo.setData(resultMap);
		} catch (Exception e) {
			cvo.setSuccess(false);
			cvo.setMsg("微信支付订单发送失败。");
			cvo.setData(e.getMessage());
		}
		return cvo;
	}
	
}
