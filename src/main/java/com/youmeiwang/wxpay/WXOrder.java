package com.youmeiwang.wxpay;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "youmei_wxorder")
public class WXOrder {

	//发起人ID
	private String userID;
	//购买作品ID
	private String workID;
	//公众账号ID
	private static final String appid = WXConfig.APPID;
	//商户号
	private static final String mch_id = WXConfig.MCH_ID;
	//随机字符串
	private String nonce_str;
	//签名
	private String sign;
	//商品描述
	private String body;
	//商户订单号
	private String out_trade_no;
	//标价币种
	private static final String fee_type = WXConfig.FEE_TYPE;
	//标价金额
	private Integer total_fee;
	//终端IP
	private String spbill_create_ip;
	//交易起始时间
	private String time_start;
	//通知地址
	private static final String notify_url = WXConfig.NOTIFY_URL;
	//交易类型
	private static final String trade_type = WXConfig.TRADE_TYPE;
	//商品ID
	private String product_id;
	//用户标识
//	private String openid;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getWorkID() {
		return workID;
	}
	public void setWorkID(String workID) {
		this.workID = workID;
	}
	public String getAppid() {
		return appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getFee_type() {
		return fee_type;
	}
	public Integer getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(Integer total_fee) {
		this.total_fee = total_fee;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getTime_start() {
		return time_start;
	}
	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
}
