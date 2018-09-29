package meikuu.website.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import meikuu.domain.entity.pay.OrderInfo;
import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.DoubleUtil;
import meikuu.repertory.service.BalanceRecordService;
import meikuu.repertory.service.ConfigService;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.OrderService;
import meikuu.repertory.service.PurchaseService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.TransactionService;
import meikuu.repertory.service.UserService;
import meikuu.repertory.service.WorkService;
import meikuu.website.vo.SimpleVO;

/**
 * 前台界面项目·支付相关
 * 支付对象表现层
 * @author zhilijian
 *
 */
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
	
	/**
	 * 余额购买作品
	 */
	@GetMapping("/purchasework")
	public SimpleVO purchaseWork(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="userToken", required=true) String sessionId ) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user1 = userService.queryUser("userID", userID);
		if (userID == null || user1 == null) {
			return new SimpleVO(false, "用户尚未登录。"); 
		}

		Work work = workService.queryWork("workID", workID);
		if (work == null) {
			return new SimpleVO(false, "该作品不存在或已被删除。"); 
		}
		
		try {
			User user2 = userService.queryUser("userID", work.getAuthor());
			if (user2 == null) {
				return new SimpleVO(false, "该模型作者不存在。"); 
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
				double fee1 = 0.0;
				double fee2 = 0.0;
				if (user1.getVipKind().contains(2)) {
					fee1 = work.getPrice() * discount;
				} else {
					fee1 = work.getPrice();
				}
				
				balance1 = DoubleUtil.Subtraction(user1.getBalance(), fee1);
				if (balance1 < 0) {
					return new SimpleVO(false, "余额不足，请先充值。");
				}
				fee2 = fee1 * commissionRate;
				balance2 = DoubleUtil.addition(user2.getBalance(), fee2);
				userService.setUser("userID", userID, "balance", balance1);
				userService.setUser("username", work.getAuthor(), "balance", balance2);
				
				purchaseService.addPurchase(userID, 2, workID, work.getWorkName(), (double)work.getPrice(), (double)work.getPrice(), null, null);
				transactionService.addTransaction(userID, workID, null, 0, 1);
				transactionService.addTransaction(user2.getUserID(), null, fee2, 0, 0);
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
			return new SimpleVO(true, "余额购买作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 余额购买VIP
	 */
	@PostMapping("/purchasevip")
	public SimpleVO purchaseVIP(@RequestParam(name="vipKind", required=true) Integer vipKind,
			@RequestParam(name="taocanType", required=true) Integer taocanType,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "请求失败，请重新登录。");
		}
		
		try {
			double fee = 0;
			int monthNum = 0;
			Calendar calendar = Calendar.getInstance();
			Set<Integer> vips = user.getVipKind();
			if (vips == null) {
				vips = new HashSet<Integer>();
			}
			Long initialTime = 0l;
			Long vipTime = 0l;
			Integer freedownload = user.getFreedownload();
			
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
				
				vipTime = user.getShareVIPTime();
				if (vipTime == null || vipTime < System.currentTimeMillis()) {
					initialTime = System.currentTimeMillis();
				} else {
					initialTime = vipTime;
				}
				
				calendar.setTime(new Date(initialTime));
				calendar.add(Calendar.MONTH, monthNum);
				userService.setUser("userID", userID, "shareVIPTime", calendar.getTimeInMillis());
				
				if (freedownload == null) {
					userService.setUser("userID", userID, "freedownload", 10);
				}
				if (!vips.contains(1)) {
					userService.setUser("userID", userID, "freedownload", freedownload + 10);
				}
				
				vips.add(1);
				vips.remove(0);
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
				
				vipTime = user.getOriginalVIPTime();
				if (vipTime == null || vipTime < System.currentTimeMillis()) {
					initialTime = System.currentTimeMillis();
				} else {
					initialTime = vipTime;
				}
				
				calendar.setTime(new Date(initialTime));
				calendar.add(Calendar.MONTH, monthNum);
				userService.setUser("userID", userID, "originalVIPTime", calendar.getTimeInMillis());
				
				vips.add(2);
				vips.remove(0);
				userService.setUser("userID", userID, "vipKind", vips);
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
				
				vipTime = user.getOriginalVIPTime();
				if (vipTime == null || vipTime < System.currentTimeMillis()) {
					initialTime = System.currentTimeMillis();
				} else {
					initialTime = vipTime;
				}
				calendar.setTime(new Date(initialTime));
				calendar.add(Calendar.MONTH, monthNum);
				userService.setUser("userID", userID, "companyVIPTime", calendar.getTimeInMillis());
				
				if (freedownload == null) {
					userService.setUser("userID", userID, "freedownload", 50);
				}
				if (!vips.contains(3)) {
					userService.setUser("userID", userID, "freedownload", freedownload + 50);
				}
				
				vips.add(3);
				vips.remove(0);
				userService.setUser("userID", userID, "vipKind", vips);
				break;
			}
			
			Double balance = DoubleUtil.Subtraction(user.getBalance(), fee);
			if (balance < 0) {
				return new SimpleVO(false, "余额不足，请先充值。");
			}
			userService.setUser("userID", userID, "balance", balance);
			
			transactionService.addTransaction(userID, null, (double)fee, 0, 5);
			balanceRecordService.addBalanceRecord(userID, (double)fee, 2);
			
			String title = "购买VIP成功！";
			String content = "恭喜您，您已成功VIP权益，赶紧去体验我们的会员服务吧。";
			newsService.addNews(userID, title, content, 1);
			return new SimpleVO(true, "余额购买VIP成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 余额兑换游币
	 */
	@GetMapping("/exchange")
	public SimpleVO exchange(@RequestParam(name="money", required=true) Integer money,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "请求失败，请重新登录。");
		}
		
		if (money < 10) {
			return new SimpleVO(false, "余额兑换游币，10元起充。");
		}
		
		try {
			transactionService.addTransaction(userID, null, (double)money, 0, 3);
			Double balance = DoubleUtil.Subtraction(user.getBalance(), money);
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
	
	/**
	 * 支付是否成功
	 */
	@GetMapping("/issucceed")
	public SimpleVO isSucceed(@RequestParam(name="outTradeNo", required=true) String outTradeNo) {
		
		try {
			OrderInfo order = orderService.queryOrder("outTradeNo", outTradeNo);
			if (order == null) {
				return new SimpleVO(false, "该查询订单并不存在。");
			}
			if ("SUCCESS".equals(order.getPayStatus()) || "FAIL".equals(order.getPayStatus())) {
				return new SimpleVO(true, "该订单已支付成功！");
			} else {
				return new SimpleVO(false, "该订单尚未支付。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
}
