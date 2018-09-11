package com.youmeiwang.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.BalanceRecord;
import com.youmeiwang.entity.CashApplication;
import com.youmeiwang.entity.Order;
import com.youmeiwang.entity.Purchase;
import com.youmeiwang.entity.Transaction;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.BalanceRecordService;
import com.youmeiwang.service.CashApplicationService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.PurchaseService;
import com.youmeiwang.service.TransactionService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/paymanage")
public class PayManageController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private BalanceRecordService balanceRecordService;
	
	@Autowired
	private CashApplicationService cashApplicationService;
	
	@PostMapping("/orderrecord")
	public CommonVO orderRecord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="payType", required=false) Integer payType,
			@RequestParam(name="payStatus", required=false) String payStatus,
			@RequestParam(name="startTime", required=false) Long startTime,
			@RequestParam(name="endTime", required=false) Long endTime,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getRechargeManage(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			Set<Order> orderset = new HashSet<Order>();
			orderset.addAll(orderService.orderList("outTradeNo", condition, payType, payStatus, startTime, endTime, null, null));
			orderset.addAll(orderService.orderList("userID", condition, payType, payStatus, startTime, endTime, null, null));
			
			List<Order> orderlist1 = new ArrayList<Order>(orderset);
			List<Order> orderlist2 = new LinkedList<Order>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < orderlist1.size()-currIdx; i++) {
				Order order = orderlist1.get(currIdx + i);
				orderlist2.add(order);
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Order order : orderlist2) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("outTradeNo", order.getOutTradeNo());
				map.put("payType", order.getPayType());
				map.put("transactionID", order.getTransactionID());
				map.put("userID", order.getUserID());
				map.put("totalFee", order.getTotalFee());
				map.put("cashFee", order.getCashFee());
				map.put("attach", order.getAttach());
				map.put("payStatus", order.getPayStatus());
				map.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getEndTime())));
				maplist.add(map);
			}
			
			Long orderAmount = (long) orderlist1.size();
			Long pageAmount = 0l;
			if (orderAmount % size == 0) {
				pageAmount = orderAmount / size;
			} else {
				pageAmount = orderAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("orders", maplist);
			data.put("orderAmount", orderAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询交易记录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询交易记录失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/purchaserecord")
	public CommonVO purchaseRecord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="userID", required=false) String userID,
			@RequestParam(name="purchaseType", required=false) Integer purchaseType,
			@RequestParam(name="startTime", required=false) Long startTime,
			@RequestParam(name="endTime", required=false) Long endTime,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		 
		try {
			List<Purchase> purchaselist = purchaseService.purchaselist(userID, purchaseType, startTime, endTime, page, size);
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Purchase purchase : purchaselist) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("purchaseID", purchase.getPurchaseID());
				map.put("userID", purchase.getUserID());
				map.put("purchaseType", purchase.getPurchaseType());
				map.put("goodsName", purchase.getGoodsName());
				map.put("payableAmount", purchase.getPayableAmount());
				map.put("actualPayment", purchase.getActualPayment());
				map.put("payableYoubi", purchase.getPayableYoubi());
				map.put("actualPayYoubi", purchase.getActualPayYoubi());
				map.put("payTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(purchase.getPayTime())));
				maplist.add(map);
			}
			
			Long purchaseAmount = purchaseService.getAmount(userID, purchaseType, startTime, endTime);
			Long pageAmount = 0l;
			if (purchaseAmount % size == 0) {
				pageAmount = purchaseAmount / size;
			} else {
				pageAmount = purchaseAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("purchaseRecord", maplist);
			data.put("purchaseAmount", purchaseAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询购买记录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询购买记录失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/balancerecord")
	public CommonVO balanceRecord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="startTime", required=false) Long startTime,
			@RequestParam(name="endTime", required=false) Long endTime,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		try {
			Set<BalanceRecord> balanceRecordset = new HashSet<BalanceRecord>();
			balanceRecordset.addAll(balanceRecordService.balanceRecordlist("userID", condition, startTime, endTime));
			balanceRecordset.addAll(balanceRecordService.balanceRecordlist("username", condition, startTime, endTime));
			
			List<BalanceRecord> balanceRecordlist1 = new ArrayList<BalanceRecord>(balanceRecordset);
			List<BalanceRecord> balanceRecordlist2 = new LinkedList<BalanceRecord>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < balanceRecordlist1.size()-currIdx; i++) {
				BalanceRecord balanceRecord = balanceRecordlist1.get(currIdx + i);
				balanceRecordlist2.add(balanceRecord);
			}
			
			List<Map<String, Object>> balanceRecords = new LinkedList<Map<String, Object>>();
			for (BalanceRecord balanceRecord : balanceRecordlist2) {
				Map<String, Object> balanceRecordmap = new HashMap<String, Object>();
				balanceRecordmap.put("balanceRecordID", balanceRecord.getBalanceRecordID());
				balanceRecordmap.put("userID", balanceRecord.getUserID());
				balanceRecordmap.put("username", balanceRecord.getUsername());
				balanceRecordmap.put("money", balanceRecord.getMoney());
				balanceRecordmap.put("use", balanceRecord.getUse());
				balanceRecordmap.put("balance", balanceRecord.getBalance());
				balanceRecordmap.put("youbiAmount", balanceRecord.getYoubiAmount());
				balanceRecordmap.put("operateTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(balanceRecord.getOperateTime())));
				balanceRecords.add(balanceRecordmap);
			}
			
			Long balanceRecordAmount = (long) balanceRecordlist1.size();
			Long pageAmount = 0l;
			if (balanceRecordAmount % size == 0) {
				pageAmount = balanceRecordAmount / size;
			} else {
				pageAmount = balanceRecordAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("balanceRecords", balanceRecords);
			data.put("balanceRecordAmount", balanceRecordAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询余额使用记录成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询余额使用记录失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/transactionrecord")
	public CommonVO transactionRecord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="currency", required=false) Integer currency,
			@RequestParam(name="reason", required=false) Integer reason,
			@RequestParam(name="startTime", required=false) Long startTime,
			@RequestParam(name="endTime", required=false) Long endTime,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		try {
			Set<Transaction> transactionset = new HashSet<Transaction>();
			transactionset.addAll(transactionService.transactionlist(condition, currency, reason, startTime, endTime));
			
			List<Transaction> transactionlist1 = new ArrayList<Transaction>(transactionset);
			List<Transaction> transactionlist2 = new LinkedList<Transaction>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < transactionlist1.size()-currIdx; i++) {
				Transaction transaction = transactionlist1.get(currIdx + i);
				transactionlist2.add(transaction);
			}
			
			List<Map<String, Object>> transactions = new LinkedList<Map<String, Object>>();
			for (Transaction transaction : transactionlist2) {
				Map<String, Object> transactionmap = new HashMap<String, Object>();
				transactionmap.put("transactionID", transaction.getTransactionID());
				transactionmap.put("userID", transaction.getUserID());
				transactionmap.put("username", transaction.getUsername());
				transactionmap.put("memberKind", transaction.getMemberKind());
				transactionmap.put("money", transaction.getMoney());
				transactionmap.put("reason", transaction.getReason());
				transactionmap.put("currency", transaction.getCurrency());
				transactionmap.put("operateTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(transaction.getOperateTime())));
				transactions.add(transactionmap);
			}
			
			Long transactionAmount = (long) transactionlist1.size();
			Long pageAmount = 0l;
			if (transactionAmount % size == 0) {
				pageAmount = transactionAmount / size;
			} else {
				pageAmount = transactionAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("transactions", transactions);
			data.put("transactionAmount", transactionAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "流水查询成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "流水查询失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/cashapplication")
	public CommonVO cashApplication(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="applyStatus", required=false) Integer applyStatus,
			@RequestParam(name="startTime", required=false) Long startTime,
			@RequestParam(name="endTime", required=false) Long endTime,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		try {
			Set<CashApplication> cashApplicationset = new HashSet<CashApplication>();
			cashApplicationset.addAll(cashApplicationService.cashApplicationlist(condition, applyStatus, startTime, endTime));
			
			List<CashApplication> cashApplicationlist1 = new ArrayList<CashApplication>(cashApplicationset);
			List<CashApplication> cashApplicationlist2 = new LinkedList<CashApplication>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < cashApplicationlist1.size()-currIdx; i++) {
				CashApplication transaction = cashApplicationlist1.get(currIdx + i);
				cashApplicationlist2.add(transaction);
			}
			
			List<Map<String, Object>> cashApplications = new LinkedList<Map<String, Object>>();
			for (CashApplication cashApplication : cashApplicationlist2) {
				Map<String, Object> cashApplicationmap = new HashMap<String, Object>();
				cashApplicationmap.put("applicationID", cashApplication.getApplicationID());
				cashApplicationmap.put("userID", cashApplication.getUserID());
				cashApplicationmap.put("username", cashApplication.getUsername());
				cashApplicationmap.put("memberKind", cashApplication.getMemberKind());
				cashApplicationmap.put("balance", cashApplication.getBalance());
				cashApplicationmap.put("cashApply", cashApplication.getCashApply());
				cashApplicationmap.put("applyTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(cashApplication.getApplyTime())));
				cashApplicationmap.put("applyStatus", cashApplication.getApplyStatus());
				cashApplications.add(cashApplicationmap);
			}
			
			Long cashApplicationAmount = (long) cashApplicationlist1.size();
			Long pageAmount = 0l;
			if (cashApplicationAmount % size == 0) {
				pageAmount = cashApplicationAmount / size;
			} else {
				pageAmount = cashApplicationAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("cashApplications", cashApplications);
			data.put("cashApplicationAmount", cashApplicationAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "流水查询成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "流水查询失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/supplement")
	public CommonVO supplement(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="outTradeNo", required=true) String outTradeNo,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getRechargeManage(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			
			Order order = orderService.queryOrder("outTradeNo", outTradeNo);
			User user = userService.queryUser("userID", order.getUserID());
			
			if (user == null || order == null) {
				return new CommonVO(false, "补单失败。", "用户信息或订单信息错误。");
			}
			
			if (!"FAIL".equals(order.getPayStatus())) {
				return new CommonVO(false, "补单失败。", "该订单并未失败，无需补单。");
			}
			if ("RECHARGE".equals(order.getAttach())) {
				Double balance = user.getBalance()==null ? 0 : user.getBalance();
				balance += order.getTotalFee();
				userService.setUser("userID", order.getUserID(), "balance", balance);
				orderService.setOrder("outTradeNo", outTradeNo, "cashFee", order.getTotalFee());
				orderService.setOrder("outTradeNo", outTradeNo, "endTime", System.currentTimeMillis());
				purchaseService.addPurchase(order.getUserID(), 2, order.getProductID(), order.getBody(), order.getTotalFee(), order.getCashFee(), null, null);
			} else {
				List<String> worklist = ListUtil.addElement(user.getPurchaseWork(), order.getProductID());
				userService.setUser("userID", order.getUserID(), "purchaseWork", worklist);
			}
			order.setPayStatus("SUPPLIED");
			orderService.setOrder("outTradeNo", outTradeNo, "payStatus", "SUPPLIED");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("outTradeNo", order.getOutTradeNo());
			data.put("payStatus", order.getPayStatus());
			return new CommonVO(true, "补单成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			orderService.setOrder("outTradeNo", outTradeNo, "payStatus", "FAIL");
			return new CommonVO(false, "补单失败。", "出错信息：" + e.toString());
		}
	}
	
}
