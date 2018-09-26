package meikuu.domain.entity.pay;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_order")
public class OrderInfo {

	/**
	 * 商户订单号
	 */
	@Indexed
	private String outTradeNo;
	/**
	 * 交易流水号
	 */
	private String transactionID;
	/**
	 * 发起人（用户编号）
	 */
	private String userID;
	/**
	 * 商品编号（作品编号）
	 */
	private String productID;
	/**
	 * 商品描述
	 */
	private String body;
	/**
	 * 标价金额
	 */
	private Double totalFee;
	/**
	 * 支付金额
	 */
	private Double cashFee;
	/**
	 * 交易起始时间
	 */
	private Long startTime;
	/**
	 * 交易终止时间
	 */
	private Long endTime;
	/**
	 * 支付方式
	 */
	private String payType;
	/**
	 * 支付状态
	 * NOTPAY：未支付 SUCCESS：支付成功 FAIL：支付失败 SUPPLIED：已补单
	 */
	private String payStatus;
	/**
	 * 附加信息
	 */
	private String attach;
	
	public OrderInfo() {}
	
	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionId(String transactionID) {
		this.transactionID = transactionID;
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

	public Double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Double totalFee) {
		this.totalFee = totalFee;
	}

	public Double getCashFee() {
		return cashFee;
	}

	public void setCashFee(Double cashFee) {
		this.cashFee = cashFee;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
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

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
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
		OrderInfo other = (OrderInfo) obj;
		if (outTradeNo == null) {
			if (other.outTradeNo != null)
				return false;
		} else if (!outTradeNo.equals(other.outTradeNo))
			return false;
		return true;
	}
}
