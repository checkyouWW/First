package com.ztesoft.demo.inf;

import java.util.Map;

public interface DemoService {
	String sayHello(String name);
	Map<String, Object> sayHelloFromDB(Map<String,Object> params);
}
