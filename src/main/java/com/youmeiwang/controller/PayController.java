package com.youmeiwang.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.BalanceRecordService;
import com.youmeiwang.service.ConfigService;
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.PurchaseService;
import com.youmeiwang.service.TransactionService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.sessionmanage.SessionService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/pay")
public class PayController {

	@Autowired
	private UserService userService;
	
	@Autowired 
	private WorkService workService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private BalanceRecordService balanceRecordService;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private SessionService cmdService;
	
	@GetMapping("/purchasework")
	public SimpleVO purchaseWork(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="userToken", required=true) String sessionId ) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user1 = userService.queryUser("userID", userID);
		if (userID == null || user1 == null) {
			return new SimpleVO(false, "用户尚未登录。"); 
		}

		Work work = workService.queryWork("workID", workID);
		if (work == null || work.getIsDelete() == true) {
			return new SimpleVO(false, "该作品不存在或已被删除。"); 
		}
		
		try {
			User user2 = userService.queryUser("userID", work.getAuthor());
			if (user2 == null) {
				return new SimpleVO(false, "模型作者不存在或已销户。"); 
			}
			
			if (user1 == user2) {
				return new SimpleVO(true, "发行作者不必购买自己的作品。");
			}
			
			List<String> worklist = user1.getPurchaseWork();
			if(worklist.contains(workID)) {
				return new SimpleVO(true, "已购作品24小时内不必再次购买。"); 
			}
			
			double discount = (double) configService.getConfigValue("discount");
			double commissionRate = (double) configService.getConfigValue("commissionRate");
			
			if (work.getPrimaryClassification() == 1) {
				Double balance1 = 0.0;
				Double balance2 = 0.0;
				double fee = 0.0;
				if (user1.getVipKind().contains(2)) {
					fee = work.getPrice() * discount;
				} else {
					fee = work.getPrice();
				}
				
				balance1 = user1.getBalance() - fee;
				if (balance1 < 0) {
					return new SimpleVO(false, "余额不足，请先充值。");
				}
				
				balance2 = user2.getBalance() + fee * commissionRate;
				
				userService.setUser("userID", userID, "balance", balance1);
				userService.setUser("username", work.getAuthor(), "balance", balance2);
				purchaseService.addPurchase(userID, 2, workID, work.getWorkName(), (double)work.getPrice(), (double)work.getPrice(), null, null);
				transactionService.addTransaction(userID, workID, null, 0, 1);
				transactionService.addTransaction(user2.getUserID(), null, fee * commissionRate, 0, 0);
				balanceRecordService.addBalanceRecord(userID, (double)work.getPrice(), 2);
			} else {
				Integer freedownload = user1.getFreedownload();
				if (freedownload != null && freedownload > 0) {
					userService.setUser("userID", userID, "freedownload", freedownload - 1);
					purchaseService.addPurchase(userID, 3, workID, work.getWorkName(), null, null, work.getPrice(), 0);
					
				} else {
					Long youbiAmount1 = user1.getYoubiAmount() - work.getPrice();
					Long youbiAmount2 = user2.getYoubiAmount() + work.getPrice();
					if (youbiAmount1 < 0) {
						return new SimpleVO(false, "游币不足，请先兑换。");
					}
					userService.setUser("userID", userID, "youbiAmount", youbiAmount1);
					userService.setUser("username", work.getAuthor(), "youbiAmount", youbiAmount2);
					purchaseService.addPurchase(userID, 3, workID, work.getWorkName(), null, null, work.getPrice(), work.getPrice());
					transactionService.addTransaction(userID, workID, null, 1, 1);
					transactionService.addTransaction(user2.getUserID(), null, (double)work.getPrice(), 1, 0);
				}
			}
			worklist.add(workID);
			userService.setUser("userID", userID, "purchaseWork", worklist);
			
			String title = "购买作品成功！"; 
			String content = "恭喜您，您已成功购买《" + work.getWorkName() +"》作品，请前往下载界面下载。下载有效时间为24小时。";
			newsService.addNews(userID, title, content, 1);
			return new SimpleVO(true, "购买作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/purchasevip")
	public SimpleVO purchaseVIP(@RequestParam(name="vipKind", required=true) Integer vipKind,
			@RequestParam(name="taocanType", required=true) Integer taocanType,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "用户尚未登录或用户不存在");
		}
		
		try {
			double fee = 0;
			int monthNum = 0;
			Calendar calendar = Calendar.getInstance();
			switch (vipKind) {
			case 1:
				switch (taocanType) {
				case 1:
					fee = (double) configService.getConfigValue("shareVIPMonth");
					monthNum = 1;
					purchaseService.addPurchase(userID, 1, null, "共享VIP包月套餐", (double)fee, (double)fee, null, null);
					break;
				case 2:
					fee = (double) configService.getConfigValue("shareVIPHalf");
					monthNum = 6;
					purchaseService.addPurchase(userID, 1, null, "共享VIP半年套餐", (double)fee, (double)fee, null, null);
					break;
				case 3:
					fee = (double) configService.getConfigValue("shareVIPYear");
					monthNum = 12;
					purchaseService.addPurchase(userID, 1, null, "共享VIP包年套餐", (double)fee, (double)fee, null, null);
					break;
				}
				
				Long shareVIPTime = 0l;
				if (user.getShareVIPTime() == null || user.getShareVIPTime() < System.currentTimeMillis()) {
					shareVIPTime = System.currentTimeMillis();
				} else {
					shareVIPTime = user.getShareVIPTime();
				}
				
				calendar.setTime(new Date(shareVIPTime));
				calendar.add(Calendar.MONDAY, monthNum);
				userService.setUser("userID", userID, "shareVIPTime", calendar.getTimeInMillis());
				List<Integer> vips = ListUtil.addElement(user.getVipKind(), 1);
				vips = ListUtil.removeElement(vips, 0);
				Collections.sort(vips);
				userService.setUser("userID", userID, "vipKind", vips);
				break;
			case 2:
				switch (taocanType) {
				case 1:
					fee = (double) configService.getConfigValue("originalVIPMonth");
					monthNum = 1;
					purchaseService.addPurchase(userID, 1, null, "原创VIP包月套餐", (double)fee, (double)fee, null, null);
					break;
				case 2:
					fee = (double) configService.getConfigValue("originalVIPHalf");
					monthNum = 6;
					purchaseService.addPurchase(userID, 1, null, "原创VIP半年套餐", (double)fee, (double)fee, null, null);
					break;
				case 3:
					fee = (double) configService.getConfigValue("originalVIPYear");
					monthNum = 12;
					purchaseService.addPurchase(userID, 1, null, "原创VIP包年套餐", (double)fee, (double)fee, null, null);
					break;
				}
				break;
			case 3:
				switch (taocanType) {
				case 1:
					fee = (double) configService.getConfigValue("companyVIPMonth");
					monthNum = 1;
					purchaseService.addPurchase(userID, 1, null, "企业VIP包月套餐", (double)fee, (double)fee, null, null);
					break;
				case 2:
					fee = (double) configService.getConfigValue("companyVIPHalf");
					monthNum = 6;
					purchaseService.addPurchase(userID, 1, null, "企业VIP半年套餐", (double)fee, (double)fee, null, null);
					break;
				case 3:
					fee = (double) configService.getConfigValue("companyVIPYear");
					monthNum = 12;
					purchaseService.addPurchase(userID, 1, null, "企业VIP包年套餐", (double)fee, (double)fee, null, null);
					break;
				}
				break;
			}
			
			Double balance = user.getBalance() - fee;
			if (balance < 0) {
				return new SimpleVO(false, "余额不足，请先充值。");
			}
			userService.setUser("userID", userID, "balance", balance);
			
			transactionService.addTransaction(userID, null, (double)fee, 0, 5);
			balanceRecordService.addBalanceRecord(userID, (double)fee, 2);
			
			String title = "购买VIP成功！";
			String content = "恭喜您，您已成功VIP权益，赶紧去体验我们的会员服务吧。";
			newsService.addNews(userID, title, content, 1);
			return new SimpleVO(true, "购买VIP成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/exchange")
	public SimpleVO exchange(@RequestParam(name="money", required=true) Integer money,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "用户尚未登录或用户不存在。");
		}
		
		if (money < 10) {
			return new SimpleVO(false, "余额兑换游币，10元起充。");
		}
		
		try {
			transactionService.addTransaction(userID, null, (double)money, 0, 3);
			Double balance = user.getBalance() - money;
			Long youbiAmount = user.getYoubiAmount() + money * 10;
			if (balance < 0) {
				return new SimpleVO(false, "余额不足，请先充值。"); 
			}
			userService.setUser("userID", userID, "balance", balance);
			userService.setUser("userID", userID, "youbiAmount", youbiAmount);
			balanceRecordService.addBalanceRecord(userID, (double)money, 1);
			
			String title = "兑换游币成功！";
			String content = "恭喜您，您已成功兑换" + money * 10 + "游币";
			newsService.addNews(userID, title, content, 1);
			
			return new SimpleVO(true, "余额兑换游币成功。"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
	
	@GetMapping("/issucceed")
	public SimpleVO isSucceed(@RequestParam(name="outTradeNo", required=true) String outTradeNo) {
		
		try {
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			if (order == null) {
				return new SimpleVO(false, "该订单不存在。");
			}
			if ("SUCCESS".equals(order.getPayStatus()) || "FAIL".equals(order.getPayStatus())) {
				return new SimpleVO(true, "该订单已支付支付成功！");
			} else {
				return new SimpleVO(false, "该订单尚未支付。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
}
