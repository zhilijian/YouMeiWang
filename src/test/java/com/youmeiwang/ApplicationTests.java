package com.youmeiwang;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void Test1() {
		List<String> person=new ArrayList<>();
        person.add("jackie");   //索引为0  //.add(e)
        person.add("peter");    //索引为1
        person.add("annie");    //索引为2
        person.add("martin");   //索引为3
        person.add("marry");    //索引为4
         
        person.remove(3);   //.remove(index)
        person.remove("marry");     //.remove(Object o)
         
        for (int i = 0; i < person.size(); i++) {
            System.out.println(person.get(i));  //.get(index)
        }
        
//        String per="";
//        per=person.get(1);
//        System.out.println(per);    ////.get(index)
//         
//        for (int i = 0; i < person.size(); i++) {
//            System.out.println(person.get(i));  //.get(index)
//        }
	}
	
}
 