package com.youmeiwang.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.config.WeChatConfig;
import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WeChatPayService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/wechatpay")
public class WechatPayController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private WeChatPayService wechatPayService;
	
	@PostMapping("/createorder")
	public CommonVO createOrder(String userID, String workID, Double money, 
			HttpServletRequest request, HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户尚未登录。"); 
//		}
		
		if (workID == null && money == null) {
			return new CommonVO(false, "微信支付订单发送失败。", "请选择购买或者充值。");
		}
		
		if (workID != null && money != null) {
			return new CommonVO(false, "微信支付订单发送失败。", "只可以进行购买商品或充值得操作。");
		}
		
//		String reqIP = request.getRemoteAddr();
		String reqIP = "192.168.0.128";
		
		Order order = orderService.createOrder(userID, workID, money, "WeChatPay");
		try {
			Map<String, Object> data = new HashMap<String, Object>();
 			SortedMap<String, String> resultMap = wechatPayService.createOrder(order, reqIP);
			String newSign = wechatPayService.createSign(resultMap);
			if ("SUCCESS".equals(resultMap.get("return_code")) && newSign.equals(resultMap.get("sign"))) {
				data.put("outTradeNo", order.getOutTradeNo());
				data.put("appid", WeChatConfig.APPID);
				data.put("mch_id", WeChatConfig.MCH_ID);
				data.put("prepay_id", resultMap.get("prepay_id"));
				data.put("package", "Sign=WXPay");
				data.put("timestamp", String.valueOf(System.currentTimeMillis()));
				data.put("nonce_str", UUID.randomUUID().toString().substring(0, 30));
				data.put("code_url", resultMap.get("code_url"));
				data.put("sign", wechatPayService.createSign(resultMap));
				return new CommonVO(true, "微信支付订单发送成功！", data);
			} else {
				data.put("returnMsg", resultMap.get("return_msg"));
				return new CommonVO(false, "微信支付订单发送失败。", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "微信支付订单发送失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/wechatnotify")
	public String wechatNotify(HttpServletRequest request) {
		try {
			SortedMap<String, String> resultMap = wechatPayService.receiveOrder(request);
			if (resultMap == null || !"SUCCESS".equals(resultMap.get("return_code"))) {
				return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[请求失败。]]></return_msg></xml>";
			}
			
			String newSign = wechatPayService.createSign(resultMap);
			if (!newSign.equals(resultMap.get("sign"))) {
				return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名错误。]]></return_msg></xml>";
			}
			
			SortedMap<String, String> responseMap = wechatPayService.queryOrder(resultMap.get("out_trade_no"));
			if (responseMap == null || !"SUCCESS".equals(responseMap.get("result_code"))) {
				return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[响应失败。]]></return_msg></xml>";
			}
			
			String trade_state = responseMap.get("trade_state");
			orderService.setOrder("outTradeNo", responseMap.get("out_trade_no"), "payStatus", trade_state);
			orderService.setOrder("outTradeNo", responseMap.get("out_trade_no"), "transactionId", responseMap.get("transaction_id"));
			
			Order order = orderService.queryOrder("outTradeNo", responseMap.get("out_trade_no"));
			User user = userService.queryUser("userID", order.getUserID());
			if ("SUCCESS".equals(trade_state)) {
				if ("RECHARGE".equals(responseMap.get("attach"))) {
					Double balance = user.getBalance()==null ? 0 : user.getBalance();
					balance += Double.valueOf(responseMap.get("total_fee"))/100;
					userService.setUser("userID", order.getUserID(), "balance", balance);
				} else {
					List<String> worklist = ListUtil.addElement(user.getPurchaseWork(), responseMap.get("attach"));
					userService.setUser("userID", order.getUserID(), "purchaseWork", worklist);
				}
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "cashFee", Double.valueOf(responseMap.get("cash_fee"))/100);
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
		} catch (Exception e) {
			e.printStackTrace();
			return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + e.toString() +"]]></return_msg></xml>";
		}
	}
	
	@PostMapping("/orderquery")
	public CommonVO orderQuery(String userID, String outTradeNo) {
		try {
			SortedMap<String, String> responseMap = wechatPayService.queryOrder(outTradeNo);
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			Map<String, Object> data = new HashMap<String, Object>();
			if (responseMap == null || "FAIL".equals(responseMap.get("return_code"))) {
				return new CommonVO(false, "微信支付订单查询失败。", "出错信息：" + responseMap.get("return_msg"));
			}
			if ("FAIL".equals(responseMap.get("result_code"))) {
				return new CommonVO(false, "微信支付订单查询失败。", "出错信息：" + responseMap.get("err_code_des"));
			}
			if (!"SUCCESS".equals(order.getPayStatus())) {
				data.put("outTradeNo", outTradeNo);
				data.put("attach", responseMap.get("attach"));
				data.put("payStatus", order.getPayStatus());
				return new CommonVO(true, "订单查询成功！", data);
			}
			data.put("outTradeNo", outTradeNo);
			data.put("payStatus", responseMap.get("trade_state"));
			data.put("bankType", responseMap.get("bank_type"));
			data.put("totalFee", responseMap.get("total_fee"));
			data.put("attach", responseMap.get("attach"));
			data.put("timeEnd", responseMap.get("time_end"));
			data.put("tradeStateDesc", responseMap.get("trade_state_desc"));
			return new CommonVO(true, "订单查询成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "微信支付订单查询失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/closeorder")
	public CommonVO closeOrder(String userID, String outTradeNo, Integer refund_fee, String refund_desc) {
		try {
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			SortedMap<String, String> responseMap = wechatPayService.closeOrder(outTradeNo);
			Map<String, Object> data = new HashMap<String, Object>();
			if (responseMap == null || "FAIL".equals(responseMap.get("return_code"))) {
				return new CommonVO(false, "微信支付订单查询失败。", "出错信息：" + responseMap.get("return_msg"));
			}
			if ("FAIL".equals(responseMap.get("result_code"))) {
				return new CommonVO(false, "微信支付订单查询失败。", "出错信息：" + responseMap.get("err_code_des"));
			}
			order.setPayStatus("CLOSED");
			orderService.updateOrder(order);
			
			data.put("outTradeNo", outTradeNo); 
			data.put("sign", responseMap.get("sign"));
			data.put("payStatus", order.getPayStatus());
			return new CommonVO(true, "微信支付订单关闭成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "微信支付订单关闭失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/refund")//需要双向证书，暂不执行
	public CommonVO refund(String userID, String outTradeNo, Integer refund_fee, String refund_desc) {
		try {
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			SortedMap<String, String> responseMap = wechatPayService.refundOrder(order, refund_fee, refund_desc);
			Map<String, Object> data = new HashMap<String, Object>();
			if (responseMap == null || "FAIL".equals(responseMap.get("return_code"))) {
				return new CommonVO(false, "微信支付订单退款失败1。", "出错信息：" + responseMap.get("return_msg"));
			}
			if ("FAIL".equals(responseMap.get("result_code"))) {
				return new CommonVO(false, "微信支付订单退款失败2。", "出错信息：" + responseMap.get("err_code_des"));
			}
			order.setPayStatus("REFUND");
			orderService.updateOrder(order);
			
			data.put("sign", responseMap.get("sign"));
			data.put("outTradeNo", responseMap.get("out_trade_no"));
			data.put("refund_id", responseMap.get("refund_id"));
			data.put("refund_fee", responseMap.get("refund_fee"));
			return new CommonVO(true, "微信支付订单退款成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "微信支付订单退款失败。", "出错信息：" + e.toString());
		}
	}
}
