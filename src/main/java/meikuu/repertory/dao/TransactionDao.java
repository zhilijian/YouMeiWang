package meikuu.repertory.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.pay.Transaction;

@Repository
public interface TransactionDao {

	public void addTransaction(Transaction transaction);
	
	public void removeTransaction(String condition, String value);
	
	public void setTransaction(String condition, Object value1, String target, Object value2);
	
	public Transaction queryTransaction(String condition, Object value);
	
	public List<Transaction> transactionlist(String condition, Integer currency, Integer reason, Long startTime, Long endTime);
}
