package com.youmeiwang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.config.CommonConfig;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void Test() {
		String str = CommonConfig.FilePathUrl;
		System.out.println(str);
	}
	
}
 