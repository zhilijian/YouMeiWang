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
import com.youmeiwang.entity.Purchase;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.OrderService;
import com.youmeiwang.service.PurchaseService;
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
	private PurchaseService purchaseService;
	
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
			Set<Order> orderset = new HashSet<Order>();
			orderset.addAll(orderService.orderList("outTradeNo", condition, payType, payStatus, startTime, endTime, page, size));
			orderset.addAll(orderService.orderList("userID", condition, payType, payStatus, startTime, endTime, page, size));
			
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
	
	
}
