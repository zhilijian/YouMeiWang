package com.youmeiwang;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.util.ZipUtil;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void Test() throws IOException {
		ZipUtil zu = new ZipUtil("E:\\YouMoResource\\88888888\\aaa.zip");
		zu.compress("E:\\YouMoResource\\88888888\\20180905131325243956.zip");
	
	}
	
}
 