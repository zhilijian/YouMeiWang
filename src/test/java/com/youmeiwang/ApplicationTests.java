package com.youmeiwang;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.entity.Config;
import com.youmeiwang.service.ConfigService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private ConfigService configService;
	
	@Test
	public void Test() throws IOException {
		Config config = configService.queryConfig("configName", "uploadPicturePath");
		String str = (String) config.getValue();
		System.out.println(str);
	}
	
}
 