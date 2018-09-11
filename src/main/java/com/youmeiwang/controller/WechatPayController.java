package com.youmeiwang.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.PurchaseService;
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
	private PurchaseService purchaseService;
	
	@Autowired
	private WeChatPayService wechatPayService;
	
	@Autowired
	private NewsService newsService;
	
	@PostMapping("/createorder")
	public CommonVO createOrder(@RequestParam(name="workID", required=false) String workID,
			@RequestParam(name="money", required=false) Double money,
			HttpServletRequest request, HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}"); 
		}
		
		if (workID == null && money == null) {
			return new CommonVO(false, "微信支付订单发送失败。", "请选择购买或者充值。");
		}
		
		if (workID != null && money != null) {
			return new CommonVO(false, "微信支付订单发送失败。", "只可以进行购买商品或充值得操作。");
		}
		
		String reqIP = request.getRemoteAddr();

		List<String> worklist = user.getPurchaseWork();
		if(worklist != null && worklist.contains(workID)) {
			return new CommonVO(true, "已购作品24小时内不必再次购买。", "{}"); 
		}
		
		Order order = orderService.createOrder(userID, workID, money, "WeChatPay");
		
		try {
			Map<String, Object> data = new HashMap<String, Object>();
 			SortedMap<String, String> resultMap = wechatPayService.createOrder(order, reqIP);
			String newSign = wechatPayService.createSign(resultMap);
			if ("SUCCESS".equals(resultMap.get("return_code")) && newSign.equals(resultMap.get("sign"))) {
				data.put("outTradeNo", order.getOutTradeNo());
				data.put("code_url", resultMap.get("code_url"));
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
	public String wechatNotify(HttpServletRequest request) throws MalformedURLException, IOException {
		
		SortedMap<String, String> resultMap = wechatPayService.receiveInfo(request);
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
			
		String out_trade_no = responseMap.get("out_trade_no");	
		String title = "";
		String content = "";
		
		try {
			String trade_state = responseMap.get("trade_state");
			orderService.setOrder("outTradeNo", out_trade_no, "transactionID", responseMap.get("transaction_id"));
			
			Order order = orderService.queryOrder("outTradeNo", responseMap.get("out_trade_no"));
			User user = userService.queryUser("userID", order.getUserID());
			if ("SUCCESS".equals(trade_state)) {
				if ("RECHARGE".equals(responseMap.get("attach"))) {
					Double balance1 = user.getBalance()==null ? 0 : user.getBalance();
					Double balance2 = Double.valueOf(responseMap.get("receipt_amount"));
					balance1 += balance2;
					userService.setUser("userID", order.getUserID(), "balance", balance1);
					
					title = "充值成功！";
					content = "恭喜您，您已成功充值" + balance2 +"元。快去购买您喜欢的作品吧。";
					newsService.addNews(user.getUserID(), title, content, 1);
				} else {
					List<String> worklist = ListUtil.addElement(user.getPurchaseWork(), responseMap.get("attach"));
					userService.setUser("userID", order.getUserID(), "purchaseWork", worklist);
					
					title = "购买作品成功！";
					content = "恭喜您，您已成功" + order.getBody() +"作品，请前往下载界面下载。下载有效时间为24小时。";
					newsService.addNews(user.getUserID(), title, content, 1);
				}
				orderService.setOrder("outTradeNo", out_trade_no, "payStatus", trade_state);
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "cashFee", Double.valueOf(responseMap.get("cash_fee"))/100);
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "endTime", System.currentTimeMillis());
				purchaseService.addPurchase(order.getUserID(), 2, order.getProductID(), order.getBody(), Double.valueOf(responseMap.get("total_fee"))/100, Double.valueOf(responseMap.get("cash_fee"))/100, null, null);
			}
			return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
		} catch (Exception e) {
			e.printStackTrace();
			orderService.setOrder("outTradeNo", out_trade_no, "payStatus", "FAIL");
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
