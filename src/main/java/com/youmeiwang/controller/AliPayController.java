package com.youmeiwang.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.youmeiwang.config.AliPayConfig;
import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.AliPayService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.PurchaseService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@Controller
@RequestMapping("/alipay")
public class AliPayController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AlipayClient alipayClient;
	
	@Autowired
	private AliPayService alipayService;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@PostMapping("/createorder")
	@ResponseBody
	public SimpleVO createOrder(String userID, String workID, Double money, 
			HttpServletRequest request, HttpServletResponse httpResponse, HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户尚未登录。"); 
//		}
		
		if (workID == null && money == null) {
			return new SimpleVO(false, "商品和金额不能同时为空。");
		}
		
		if (workID != null && money != null) {
			return new SimpleVO(false, "只可以进行购买商品或充值得操作。");
		}
		try {
			Order order = orderService.createOrder(userID, workID, money, "AliPay");
			AlipayTradePagePayModel model = alipayService.createModel(order);
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
			alipayRequest.setReturnUrl(AliPayConfig.RETURNURL);
			alipayRequest.setNotifyUrl(AliPayConfig.NOTIFYURL);
			alipayRequest.setBizModel(model);
			
			String form="";
	        form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
	        httpResponse.setContentType("text/html;charset=" + AliPayConfig.CHARSET);
	        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
	        httpResponse.getWriter().flush();
	        httpResponse.getWriter().close();
	        return null;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new SimpleVO(false, "出错信息：" + e.toString());
	    }
	}
	
	@PostMapping("/alipaynotify")
	@ResponseBody
	public String alipayNotify(HttpServletRequest request) {
		Map<String, String> responseMap = alipayService.receiveOrder(request);
		if (responseMap == null) { 
			return "fail";
		}

		String out_trade_no = responseMap.get("out_trade_no");
		
		try {
			String trade_state = responseMap.get("trade_status");
			orderService.setOrder("outTradeNo", out_trade_no, "transactionID", responseMap.get("trade_no"));
			
			Order order = orderService.queryOrder("outTradeNo", out_trade_no);
			User user = userService.queryUser("userID", order.getUserID());
			
			if ("TRADE_SUCCESS".equals(trade_state)) {
				if ("RECHARGE".equals(responseMap.get("body"))) {
					Double balance = user.getBalance()==null ? 0 : user.getBalance();
					balance += Double.valueOf(responseMap.get("receipt_amount"));
					userService.setUser("userID", order.getUserID(), "balance", balance);
				} else {
					List<String> worklist = ListUtil.addElement(user.getPurchaseWork(), responseMap.get("body"));
					userService.setUser("userID", order.getUserID(), "purchaseWork", worklist);
				}
				orderService.setOrder("outTradeNo", out_trade_no, "payStatus", "SUCCESS");
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "cashFee", Double.valueOf(responseMap.get("receipt_amount")));
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "endTime", System.currentTimeMillis());
				purchaseService.addPurchase(order.getUserID(), 2, order.getProductID(), Double.valueOf(responseMap.get("receipt_amount")), Double.valueOf(responseMap.get("receipt_amount")), null, null);
			}
			return "success";
		} catch (Exception e) {
			orderService.setOrder("outTradeNo", out_trade_no, "payStatus", "FAIL");
			e.printStackTrace();
			return "fail"; 
		}
	}
	
	@GetMapping("/alipayreturn")
	public String alipayReturn(HttpServletRequest request) {
		Map<String, String> responseMap = alipayService.receiveOrder(request);
		if (responseMap == null) { 
			return "fail";
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
			return "fail"; 
		}
	}
	
	@PostMapping("/closeorder")
	@ResponseBody
	public CommonVO closeOrder(String userID,  String out_trade_no,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户尚未登录。"); 
//		}
		
		try {
			AlipayTradePagePayModel model = alipayService.queryOrCloseModel(out_trade_no);
			AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
			alipayRequest.setBizModel(model);
			AlipayTradeCloseResponse response = alipayClient.execute(alipayRequest);
			if (response.isSuccess()) {
				return new CommonVO(true, "支付宝支付交易关闭成功！", response.getSubMsg()); 
			} else {
				return new CommonVO(false, "支付宝支付交易关闭失败。", response.getSubMsg()); 
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new CommonVO(false, "支付宝支付交易关闭失败。", "出错信息：" + e.toString());
	    }
	}
	
	@PostMapping("/queryorder")
	@ResponseBody
	public CommonVO queryOrder(String userID,  String out_trade_no,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户尚未登录。"); 
//		}
		
		try {
			AlipayTradePagePayModel model = alipayService.queryOrCloseModel(out_trade_no);
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
			alipayRequest.setBizModel(model);
			AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
			if (response.isSuccess()) {
				return new CommonVO(true, "支付宝支付交易查询成功！", response.getSubMsg()); 
			} else {
				return new CommonVO(false, "支付宝支付交易查询失败。", response.getSubMsg()); 
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new CommonVO(false, "支付宝支付交易查询失败。", "出错信息：" + e.toString());
	    }
	}
	
}
