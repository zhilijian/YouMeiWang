package com.youmeiwang.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.TransactionDao;
import com.youmeiwang.entity.Transaction;

@Service
public class TransactionService {

	@Autowired
	private TransactionDao transactionDao;
	
	public void addTransaction(Transaction transaction) {
		transactionDao.addTransaction(transaction);
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
}
