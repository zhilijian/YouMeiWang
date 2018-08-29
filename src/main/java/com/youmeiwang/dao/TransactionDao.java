package com.youmeiwang.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Transaction;

@Repository
public interface TransactionDao {

	public void addTransaction(Transaction transaction);
	
	public void removeTransaction(String condition, String value);
	
	public void removeTransaction(Map<String, Object> conditions);
	
	public void updateTransaction(Transaction transaction);
	
	public void setTransaction(String condition, Object value1, String target, Object value2);
	
	public Transaction queryTransaction(String condition, Object value);
	
	public Transaction queryTransaction(Map<String, Object> conditions);
	
	public Long getAmount(Integer searchType, String condition, Object value);
	
	public Long getAmount(Integer searchType, Map<String, Object> conditions);
	
	public List<Transaction> transactionList(Integer searchType, String condition, Object value, Integer page, Integer size);
	
	public List<Transaction> transactionList(Integer searchType, String condition, Object value, Map<String, Object> conditions, Integer page, Integer size);
}
