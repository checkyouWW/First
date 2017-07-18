package com.ztesoft.demo.consumer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ztesoft.demo.inf.DemoService;

 
public class ConsumerClient {
 
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"/dubbo/consumer.xml"});
        context.start();
 
        DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
        String hello = demoService.sayHello("world"); // 执行远程方法
        
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> sayHelloFromDB = demoService.sayHelloFromDB(params); // 执行远程方法
        
 
        System.out.println( hello ); // 显示调用结果
        System.out.println( sayHelloFromDB ); // 显示调用结果
        System.in.read(); // 按任意键退出
    }
 
}
