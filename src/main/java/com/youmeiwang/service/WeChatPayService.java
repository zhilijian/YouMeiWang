package com.youmeiwang.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.youmeiwang.config.WeChatConfig;
import com.youmeiwang.entity.Order;
import com.youmeiwang.util.MD5Util;
import com.youmeiwang.util.XMLObjectConvertUtil;

@Service
public class WeChatPayService {

	/**
	 * 调用微信统一下单接口
	 */
	public SortedMap<String, String> createOrder(Order order, String reqIP) throws MalformedURLException, IOException {
		// 微信统一下单接口
		final String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String orderInfo = createOrderInfo(order, reqIP);
		return orderRequest(orderInfo, url);
	}
	
	public SortedMap<String, String> receiveOrder(HttpServletRequest request) {
		// 获取输入流
		BufferedReader reader;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			// 接受数据
			String line = null;
			// 将输入流中的信息放在sb中
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return XMLObjectConvertUtil.praseXMLToMap(sb.toString());
	}
	
	public SortedMap<String, String> queryOrder(String out_trade_no) throws MalformedURLException, IOException {
		// 微信查询订单接口
		final String url = "https://api.mch.weixin.qq.com/pay/orderquery";
		String orderInfo = createQueryOrCloseInfo(out_trade_no);
		return orderRequest(orderInfo, url);
	}
	
	public SortedMap<String, String> closeOrder(String out_trade_no) throws MalformedURLException, IOException {
		// 微信查询订单接口
		final String url = "https://api.mch.weixin.qq.com/pay/closeorder";
		String orderInfo = createQueryOrCloseInfo(out_trade_no);
		return orderRequest(orderInfo, url);
	}
	
	public SortedMap<String, String> refundOrder(Order order, Integer refund_fee, String refund_desc) throws MalformedURLException, IOException {
		final String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
		String orderInfo = createRefundInfo(order, refund_fee, refund_desc);
		return orderRequest(orderInfo, url);
	}
	
	/**
	 * 生成统一下单信息
	 */
	public String createOrderInfo(Order order, String reqIP) {
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", WeChatConfig.APPID);
		parameters.put("mch_id", WeChatConfig.MCH_ID);
		parameters.put("nonce_str", UUID.randomUUID().toString().substring(0, 32));
		parameters.put("body", order.getBody());
		parameters.put("out_trade_no", order.getOutTradeNo());
		parameters.put("total_fee", order.getTotalFee().toString());
		parameters.put("spbill_create_ip", reqIP);
		parameters.put("notify_url", WeChatConfig.NOTIFY_URL);
		parameters.put("trade_type", WeChatConfig.TRADE_TYPE);
		parameters.put("attach", order.getAttach());
		parameters.put("sign", createSign(parameters));
		return XMLObjectConvertUtil.praseMapToXML(parameters);
	}
	
	/**
	 * 生成查询/关闭订单信息
	 */
	private String createQueryOrCloseInfo(String out_trade_no) {
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", WeChatConfig.APPID);
		parameters.put("mch_id", WeChatConfig.MCH_ID);
		parameters.put("out_trade_no", out_trade_no);
		parameters.put("nonce_str", UUID.randomUUID().toString().substring(0, 32));
		parameters.put("sign", createSign(parameters));
		return XMLObjectConvertUtil.praseMapToXML(parameters);
	}
	
	/*
	 * 生成申请退款信息
	 */
	public String createRefundInfo(Order order, Integer refund_fee, String refund_desc) {
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", WeChatConfig.APPID);
		parameters.put("mch_id", WeChatConfig.MCH_ID);
		parameters.put("nonce_str", UUID.randomUUID().toString().substring(0, 32));
		parameters.put("sign", createSign(parameters));
		parameters.put("out_trade_no", order.getOutTradeNo());
		parameters.put("out_refund_no", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + UUID.randomUUID().toString().substring(14, 32));
		parameters.put("total_fee", order.getTotalFee().toString());
		parameters.put("refund_fee", refund_fee.toString());
		parameters.put("refund_desc", refund_desc);
		parameters.put("refund_fee", refund_fee.toString());
		parameters.put("notify_url", WeChatConfig.NOTIFY_URL);
		return XMLObjectConvertUtil.praseMapToXML(parameters);
	}
	
	private SortedMap<String, String> orderRequest(String orderInfo, String url) throws MalformedURLException, IOException {
		// 连接 微信查询订单接口
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		// 打开传输输出流
		conn.setDoOutput(true);
		// 获取输出流
		BufferedOutputStream buffer = new BufferedOutputStream(conn.getOutputStream());
		buffer.write(orderInfo.getBytes());
		buffer.flush();
		buffer.close();
		// 获取输入流
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		// 接受数据
		String line = null;
		StringBuffer sb = new StringBuffer();
		// 将输入流中的信息放在sb中
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return XMLObjectConvertUtil.praseXMLToMap(sb.toString());
	}
	
	/**
	 * 生成签名
	 */
	public String createSign(SortedMap<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = parameters.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		/* 拼接 key,设置路径:微信商户平台(pay.weixin.com)->账户设置->API安全-->秘钥设置 */
		sb.append("key=" + WeChatConfig.KEY);
		String sign = MD5Util.getMD5(sb.toString(), WeChatConfig.CHARSET).toUpperCase();
		return sign;
	}
}
