package com.youmeiwang.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "youmei_transaction")
public class Transaction {

	@Indexed
	private String transactionID;
	private String userID;
	private Double money;
	/*
	 * 原因 reason
	 * 0 模型出售  1 模型购买  2 余额充值  3 余额兑换  4 余额提现 5 VIP购买
	 */
	private Integer reason;
	private String operateTime;
	/*
	 * 币种 currency
	 * 0：余额（人民币） 1：游币
	 */
	private Integer currency;
	
	public Transaction() {
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getCurrency() {
		return currency;
	}

	public void setCurrency(Integer currency) {
		this.currency = currency;
	}
}
