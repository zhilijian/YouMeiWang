package meikuu.website.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import meikuu.domain.config.AliPayConfig;
import meikuu.domain.entity.pay.Order;
import meikuu.domain.entity.user.User;
import meikuu.domain.util.ListUtil;
import meikuu.repertory.service.AliPayService;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.OrderService;
import meikuu.repertory.service.PurchaseService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

@CrossOrigin
@RestController
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
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/createorder")
	public SimpleVO createOrder(@RequestParam(name="workID", required=false) String workID, 
			@RequestParam(name="money", required=false) Double money,
			@RequestParam(name="userToken", required=true) String sessionId,
			HttpServletResponse httpResponse) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "用户尚未登录或用户不存在");
		}
		
		if (workID == null && money == null) {
			return new SimpleVO(false, "商品和金额不能同时为空。");
		}
		
		if (workID != null && money != null) {
			return new SimpleVO(false, "只可以进行购买商品或充值得操作。");
		}
		
		List<String> worklist = user.getPurchaseWork();
		if(worklist != null && worklist.contains(workID)) {
			return new SimpleVO(true, "已购作品24小时内不必再次购买。"); 
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
			String title = "";
			String content = "";
			
			if ("TRADE_SUCCESS".equals(trade_state)) {
				if ("RECHARGE".equals(responseMap.get("body"))) {
					Double balance1 = user.getBalance()==null ? 0 : user.getBalance();
					Double balance2 = Double.valueOf(responseMap.get("receipt_amount"));
					balance1 += balance2;
					userService.setUser("userID", order.getUserID(), "balance", balance1);
					
					title = "充值成功！";
					content = "恭喜您，您已成功充值" + balance2 +"元。快去购买您喜欢的作品吧。";
					newsService.addNews(user.getUserID(), title, content, 1);
				} else {
					List<String> worklist = ListUtil.addElement(user.getPurchaseWork(), responseMap.get("body"));
					userService.setUser("userID", order.getUserID(), "purchaseWork", worklist);
					
					title = "购买作品成功！";
					content = "恭喜您，您已成功" + order.getBody() +"作品，请前往下载界面下载。下载有效时间为24小时。";
					newsService.addNews(user.getUserID(), title, content, 1);
				}
				orderService.setOrder("outTradeNo", out_trade_no, "payStatus", "SUCCESS");
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "cashFee", Double.valueOf(responseMap.get("receipt_amount")));
				orderService.setOrder("outTradeNo", order.getOutTradeNo(), "endTime", System.currentTimeMillis());
				purchaseService.addPurchase(order.getUserID(), 2, order.getProductID(), order.getBody(), Double.valueOf(responseMap.get("receipt_amount")), Double.valueOf(responseMap.get("receipt_amount")), null, null);
			}
			return "success";
		} catch (Exception e) {
			orderService.setOrder("outTradeNo", out_trade_no, "payStatus", "FAIL");
			e.printStackTrace();
			return "fail"; 
		}
	}
	
	@PostMapping("/closeorder")
	public CommonVO closeOrder(@RequestParam(name="out_trade_no", required=true) String out_trade_no, 
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录或用户不存在", "{}");
		}
		
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
	public CommonVO queryOrder(String out_trade_no, HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录或用户不存在", "{}");
		}
		
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
