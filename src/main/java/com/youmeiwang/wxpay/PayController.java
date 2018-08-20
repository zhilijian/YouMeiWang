package com.youmeiwang.wxpay;

import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.Order;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.WeChatPayService;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/wxpay")
public class PayController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private WeChatPayService wechatPayService;
	
	@PostMapping("/createorder")
	public CommonVO createOrder(String userID, String workID, Integer money, 
			HttpServletRequest request, HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户尚未登录。"); 
//		}
		
//		String reqIP = request.getRemoteAddr();
		String reqIP = "192.168.0.128";
		
		Order Order = orderService.createOrder(userID, workID, money, "WeChatPay");
		try {
			Map<String, String> resultMap = wechatPayService.createOrder(Order, reqIP);
			return new CommonVO(true, "微信支付订单发送成功！", resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "微信支付订单发送失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/wxnotify")
	public String wxNotify(HttpServletRequest request) {
		SortedMap<String, String> resultMap = wechatPayService.receiveNotify(request);
		if (resultMap != null && "SUCCESS".equals(resultMap.get("return_code"))) {
			String newSign = wechatPayService.createSign(resultMap);
			if (newSign.equals(resultMap.get("sign"))) {
				SortedMap<String, String> responseMap = null;
				try {
					responseMap = wechatPayService.queryOrderResult(resultMap.get("outTradeNo"));
				} catch (Exception e) {
					e.printStackTrace();
					return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[参数格式校验错误]]></return_msg></xml>";
				}
				if (responseMap != null && "SUCCESS".equals(responseMap.get("result_code"))) {
					String trade_state = responseMap.get("trade_state");
					orderService.setOrder("outTradeNo", responseMap.get("out_trade_no"), "payStatus", trade_state);
					Order order = orderService.queryOrder("outTradeNo", responseMap.get("out_trade_no"));
					if ("SUCCESS".equals(order.getPayStatus()) && order.getSuccessTime() == null) {
						orderService.setOrder("outTradeNo", order.getOutTradeNo(), "successTime", System.currentTimeMillis());
					}
					return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
				}
			}
		}
		return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[验签失败]]></return_msg></xml>";
	}
}
