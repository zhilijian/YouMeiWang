package com.youmeiwang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void Test() {
		
		String str1 = "";
		String[] str2 = str1.split(",");
		System.out.println(str2[0]);
	}
	
}
 