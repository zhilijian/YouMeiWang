package com.youmeiwang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.entity.CashApplication;
import com.youmeiwang.service.CashApplicationService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private CashApplicationService cashApplicationService;
	
	@Test
	public void Test() {
		
		String userID = "12345678";
		Double cashApply = 10.0;
		CashApplication cashApplication = cashApplicationService.addCashApplication(userID, cashApply);
		System.out.println(cashApplication.toString());
	}
	
}
 