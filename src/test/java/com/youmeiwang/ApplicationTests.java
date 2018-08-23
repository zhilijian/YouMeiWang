package com.youmeiwang;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmeiwang.entity.Work;
import com.youmeiwang.service.WorkService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private WorkService workService;
	
	@Test
	public void Test() {
		List<Work> worklist = workService.workList("primaryClassification", 1, 1, 15);
		
		System.out.println(worklist);
	}
	
}
 