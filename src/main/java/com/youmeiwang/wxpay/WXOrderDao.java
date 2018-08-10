package com.youmeiwang.wxpay;

public interface WXOrderDao {

	public WXOrder findWXOrderByOut_trade_no(String out_trade_no);

	public void addWXOrder(WXOrder order);

	public Boolean updateWXOrderStatus(String out_trade_no, String status);

	public Boolean updateTransaction_id(String out_trade_no, String transaction_id);
	
	public Boolean updateDeliveTime(String out_trade_no, Long deliveTime);
}
