package com.youmeiwang.controller;

import java.util.ArrayList;
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
	
	@PostMapping("/orderrecord")
	public CommonVO orderRecord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="condition", required=false) String condition,
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
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			Set<Order> orderset = new HashSet<Order>();
			Map<String, Object> conditions = new HashMap<String, Object>();
			if (payType != null) {
				conditions.put("payType", payType);
			}
			if (payStatus != null) {
				conditions.put("payStatus", payStatus);
			}
			orderset.addAll(orderService.orderList(2, "outTradeNo", condition, conditions, null, null));
			orderset.addAll(orderService.orderList(2, "userID", condition, conditions, null, null));
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
				map.put("userID", order.getUserID());
				map.put("totalFee", order.getTotalFee());
				map.put("attach", order.getAttach());
				map.put("startTime", order.getStartTime());
				map.put("payStatus", order.getPayStatus());
				map.put("attach", order.getAttach());
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
}
