package meikuu.repertory.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.pay.Transaction;
import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.Work;
import meikuu.repertory.dao.TransactionDao;
import meikuu.repertory.dao.UserDao;
import meikuu.repertory.dao.WorkDao;

@Service
public class TransactionService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private TransactionDao transactionDao;
	
	public Transaction addTransaction(String userID, String workID, Double money, Integer currency, Integer reason) {
		User user = userDao.queryUser("userID", userID);
		Transaction transaction = new Transaction();
		transaction.setTransactionID(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + userID);
		transaction.setUserID(userID);
		transaction.setUsername(user.getUsername());
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
	
	public void removeTransaction(String condition, String value) {
		transactionDao.removeTransaction(condition, value);
	}
	
	public void removeTransaction(Map<String, Object> conditions) {
		transactionDao.removeTransaction(conditions);
	}
	
	public void updateTransaction(Transaction transaction) {
		transactionDao.updateTransaction(transaction);
	}
	
	public void setTransaction(String condition, Object value1, String target, Object value2) {
		transactionDao.setTransaction(condition, value1, target, value2);
	}
	
	public Transaction queryTransaction(String condition, Object value) {
		return transactionDao.queryTransaction(condition, value);
	}
	
	public Transaction queryTransaction(Map<String, Object> conditions) {
		return transactionDao.queryTransaction(conditions);
	}
	
	public Long getAmount(Integer searchType, String condition, Object value) {
		return transactionDao.getAmount(searchType, condition, value);
	}
	
	public Long getAmount(Integer searchType, Map<String, Object> conditions) {
		return transactionDao.getAmount(searchType, conditions);
	}
	
	public List<Transaction> transactionList(Integer searchType, String condition, Object value, Integer page, Integer size){
		return transactionDao.transactionList(searchType, condition, value, page, size);
	}
	
	public List<Transaction> transactionList(Integer searchType, String condition, Object value, Map<String, Object> conditions, Integer page, Integer size){
		return transactionDao.transactionList(searchType, condition, value, conditions, page, size);
	}
	
	public List<Transaction> transactionlist(String condition, Integer currency, Integer reason, Long startTime, Long endTime) {
		return transactionDao.transactionlist(condition, currency, reason, startTime, endTime);
	}
}
