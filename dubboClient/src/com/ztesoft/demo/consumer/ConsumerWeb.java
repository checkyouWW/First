package com.ztesoft.demo.consumer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ztesoft.demo.inf.DemoService;

@Controller
@RequestMapping("/lifeApp")
public class ConsumerWeb {
	@Resource(name = "demoService")
	private DemoService demoService;
	
	public ConsumerWeb(){
        System.out.println("******* From the ConsumerWeb constructor ***** " );
    }
	
	@RequestMapping(params = "method=login")
    public String login(){ 
		if (demoService == null) {
			System.out.println("demoService == null");
		} else {
			String hello = demoService.sayHello("world"); // 执行远程方法
	        System.out.println( hello ); // 显示调用结果
	        
	        Map<String, Object> params = new HashMap<String, Object>();
	        Map<String, Object> sayHelloFromDB = demoService.sayHelloFromDB(params); // 执行远程方法
	        System.out.println( sayHelloFromDB ); // 显示调用结果
		}
		
        return "index.jsp"; 
    } 

}