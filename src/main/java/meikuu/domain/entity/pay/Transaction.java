package meikuu.domain.entity.pay;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_transaction")
public class Transaction {

	/**
	 * 流水编号
	 */
	@Indexed
	private String transactionID;
	/**
	 * 用户编号
	 */
	private String userID;
	/**
	 * 会员种类
	 */
	private Integer memberKind;
	/**
	 * 金额
	 */
	private Double money;
	/**
	 * 交易理由，
	 * 0 模型出售  1 模型购买  2 余额充值  3 余额兑换  4 余额提现 5 VIP购买
	 */
	private Integer reason;
	/**
	 * 操作时间
	 */
	private Long operateTime;
	/**
	 * 币种，
	 * 0：余额（人民币） 1：游币
	 */
	private Integer currency;
	
	public Transaction() {}

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

	public Integer getMemberKind() {
		return memberKind;
	}

	public void setMemberKind(Integer memberKind) {
		this.memberKind = memberKind;
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

	public Long getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Long operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getCurrency() {
		return currency;
	}

	public void setCurrency(Integer currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transactionID == null) ? 0 : transactionID.hashCode());
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
		Transaction other = (Transaction) obj;
		if (transactionID == null) {
			if (other.transactionID != null)
				return false;
		} else if (!transactionID.equals(other.transactionID))
			return false;
		return true;
	}
}
