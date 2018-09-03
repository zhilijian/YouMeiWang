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
		System.out.println(hideString("abc123456789"));;
	}
	
	public static String hideString(String str) {
		return str.substring(0, 1) + "********" + str.substring(str.length()-4);
	}
}
 