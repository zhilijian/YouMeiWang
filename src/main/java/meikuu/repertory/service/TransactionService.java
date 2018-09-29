package meikuu.repertory.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.pay.Transaction;
import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.Work;
import meikuu.repertory.dao.TransactionDao;
import meikuu.repertory.dao.UserDao;
import meikuu.repertory.dao.WorkDao;

/**
 * 交易流水业务层
 * @author zhilijian
 */
@Service
public class TransactionService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private TransactionDao transactionDao;
	
	/**
	 * 添加交易流水对象
	 */
	public Transaction addTransaction(String userID, String workID, Double money, Integer currency, Integer reason) {
		User user = userDao.queryUser("userID", userID);
		Transaction transaction = new Transaction();
		transaction.setTransactionID(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + userID);
		transaction.setUserID(userID);
		transaction.setMemberKind(user.getMemberKind());
		if (workID == null) {
			transaction.setMoney(money);
		} else {
			Work work = workDao.queryWork("workID", workID);
			transaction.setMoney((double)work.getPrice());
		}
		transaction.setReason(reason);
		transaction.setOperateTime(System.currentTimeMillis());
		transaction.setCurrency(currency);
		
		transactionDao.addTransaction(transaction);
		return transaction;
	}
	
	/**
	 * 精确条件移除交易流水对象
	 */
	public void removeTransaction(String condition, String value) {
		transactionDao.removeTransaction(condition, value);
	}
	
	/**
	 * 设置目标交易流水对象某个属性
	 */
	public void setTransaction(String condition, Object value1, String target, Object value2) {
		transactionDao.setTransaction(condition, value1, target, value2);
	}
	
	/**
	 * 单条件精确查询交易流水对象确
	 */
	public Transaction queryTransaction(String condition, Object value) {
		return transactionDao.queryTransaction(condition, value);
	}

	/**
	 * 
	 */
	public List<Transaction> transactionlist(String condition, Integer currency, Integer reason, Long startTime, Long endTime) {
		return transactionDao.transactionlist(condition, currency, reason, startTime, endTime);
	}
}
