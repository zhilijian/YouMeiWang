package com.youmeiwang.wxpay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.RandomUtil;

@Service
public class WXOrderService {

	@Autowired
	private WXOrderDao wxOrderDao;
	
	@Autowired
	private WorkService workService;
	
	public WXOrder addWXOrder(String userID, String workID, Integer money, String reqIP) {
		WXOrder wxOrder = new WXOrder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		
		wxOrder.setUserID(userID);
		wxOrder.setWorkID(workID);
		wxOrder.setNonce_str(UUID.randomUUID().toString());
//		wxOrder.setSign(sign);
		wxOrder.setBody(workService.queryWork("workID", workID).getWorkName());
		wxOrder.setOut_trade_no(format.format(date) + userID + UUID.randomUUID().toString().substring(22, 32));
		wxOrder.setTotal_fee(money);
		wxOrder.setSpbill_create_ip(reqIP);
		wxOrder.setTime_start(format.format(System.currentTimeMillis()));
		wxOrder.setProduct_id(RandomUtil.getRandomString(8));
		wxOrderDao.addWXOrder(wxOrder);
		return wxOrder;
	}
	
	public WXOrder findOrderByOut_trade_no(String out_trade_no) {
		return wxOrderDao.findWXOrderByOut_trade_no(out_trade_no);
	}
}
