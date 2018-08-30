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

import com.youmeiwang.entity.Order;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.TransactionService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/paymanage")
public class PayManageController {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private TransactionService transactionService;
	
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
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			List<Map<String, Object>> conditions1 = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> conditions2 = new ArrayList<Map<String, Object>>();
			if (condition != null) {
				Map<String, Object> searchCondition1 = new HashMap<String, Object>();
				Map<String, Object> searchCondition2 = new HashMap<String, Object>();
				searchCondition1.put("searchType", 2);
				searchCondition1.put("condition", "outTradeNo");
				searchCondition1.put("value", condition);
				conditions1.add(searchCondition1);
				searchCondition2.put("searchType", 2);
				searchCondition2.put("condition", "userID");
				searchCondition2.put("value", condition);
				conditions2.add(searchCondition2);
			}
			if (payType != null) {
				switch (payType) {
				case 1:
					Map<String, Object> searchCondition1 = new HashMap<String, Object>();
					searchCondition1.put("searchType", 1);
					searchCondition1.put("condition", "payType");
					searchCondition1.put("value", "WeChatPay");
					conditions1.add(searchCondition1);
					conditions2.add(searchCondition1);
					break;
				case 2:
					Map<String, Object> searchCondition2 = new HashMap<String, Object>();
					searchCondition2.put("searchType", 1);
					searchCondition2.put("condition", "payType");
					searchCondition2.put("value", "AliPay");
					conditions1.add(searchCondition2);
					conditions2.add(searchCondition2);
					break;
				default:
					break;
				}
			}
			if (payStatus != null && !"".equals(payStatus)) {
				Map<String, Object> searchCondition = new HashMap<String, Object>();
				searchCondition.put("searchType", 1);
				searchCondition.put("condition", "payStatus");
				searchCondition.put("value", payStatus);
				conditions1.add(searchCondition);
				conditions2.add(searchCondition);
			}
			if (startTime != null) {
				Map<String, Object> searchCondition = new HashMap<String, Object>();
				searchCondition.put("searchType", 5);
				searchCondition.put("condition", "startTime");
				searchCondition.put("value", startTime);
				conditions1.add(searchCondition);
				conditions2.add(searchCondition);
			}
			if (endTime != null) {
				Map<String, Object> searchCondition = new HashMap<String, Object>();
				searchCondition.put("searchType", 7);
				searchCondition.put("condition", "endTime");
				searchCondition.put("value", endTime);
				conditions1.add(searchCondition);
				conditions2.add(searchCondition);
			}
			Map<String, Object> searchCondition2 = new HashMap<String, Object>();
			searchCondition2.put("searchType", 4);
			searchCondition2.put("condition", "endTime");
			searchCondition2.put("value", null);
			conditions1.add(searchCondition2);
			conditions2.add(searchCondition2);
			
			Set<Order> orderset = new HashSet<Order>();
			orderset.addAll(orderService.orderList(conditions1, null, null));
			orderset.addAll(orderService.orderList(conditions2, null, null));
			
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
				map.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getStartTime())));
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
			@RequestParam(name="payType", required=false) String payType,
			@RequestParam(name="payStatus", required=false) String payStatus,
			@RequestParam(name="startTime", required=false) String startTime,
			@RequestParam(name="endTime", required=false) String endTime,
			@RequestParam(name="authority", required=true) Integer authority,				
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		try {
			List<Map<String, Object>> conditions = new ArrayList<Map<String, Object>>();
			
			
			
			
//			List<Order> orderlist = orderService.
			
			
//			Map<String, Object> purchaseRecord = new HashMap<String, Object>();
			
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		return null;
	}
	
	
}
