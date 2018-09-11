package com.youmeiwang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.service.PurchaseService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private PurchaseService purchaseService;
	
	
	@Test
	public void Test(){
		
		purchaseService.removePurchase("username", null);
	}
	
}
 