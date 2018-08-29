package com.youmeiwang.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.entity.Transaction;
import com.youmeiwang.entity.Work;

@Service
public class PayService {

	@Autowired
	private WorkService workService;
	
	@Autowired
	private TransactionService transactionService;
	
	public Transaction createTransaction(String userID, String workID, Double money, Integer currency, Integer reason) {
		Transaction transaction = new Transaction();
		transaction.setTransactionID(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + userID);
		transaction.setUserID(userID);
		if (workID == null) {
			transaction.setMoney(money);
		} else {
			Work work = workService.queryWork("workID", workID);
			transaction.setMoney((double)work.getPrice());
		}
		transaction.setReason(reason);
		transaction.setOperateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		transaction.setCurrency(currency);
		
		transactionService.addTransaction(transaction);
		return transaction;
	}
	
	public void purchasevip() {
		
		
		
		
	}
	
}
