package com.youmeiwang;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.util.ListUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void Test1() {
		List<Integer> works = new ArrayList<Integer>();
		works.add(1);
		works.add(2);
		works.add(2);
		works.add(1);
		works.add(3);
		System.out.println(ListUtil.removeDuplicate(works));
	}
	
}
 