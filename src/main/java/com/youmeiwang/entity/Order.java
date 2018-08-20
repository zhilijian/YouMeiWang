package com.youmeiwang.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "youmei_order")
public class Order {

	//商户订单号
	@Indexed
	private String outTradeNo;
	//发起人ID
	private String userID;
	//商品ID(作品ID)
	private String productID;
	//随机字符串
//	private String nonce_str;
	//签名
//	private String sign;
	//商品描述
	private String body;
	//标价币种
//	private static final String fee_type = WXConfig.FEE_TYPE;
	//标价金额
	private Integer totalFee;
	//终端IP
//	private String spbill_create_ip;
	//交易起始时间
	private String startTime;
	//
	private String endTime;
	//用户标识
//	private String openid;
	//支付方式 payType
	private String payType;
	//支付状态 payStatus
	private String payStatus;
	
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Integer getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outTradeNo == null) ? 0 : outTradeNo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (outTradeNo == null) {
			if (other.outTradeNo != null)
				return false;
		} else if (!outTradeNo.equals(other.outTradeNo))
			return false;
		return true;
	}
	
}
