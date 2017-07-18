package com.ztesoft.ioc;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

/**
 * service调用业务逻辑层工厂类
 * Class Name : LogicInvokerFactory.java<br>
 * Description :service层调用bo层必须使用LogicInvokerFactory，在代理类中，框架可以统一处理共用逻辑
 * 	如：后续分布式部署时按业务分布部署需要远程调用处理<br>
 * @author liu.yuming
 * Date : 2014-8-21<br>
 *
 */
public class LogicInvokerFactory {
	private static LogicInvokerFactory factory = new LogicInvokerFactory();
	private Map boCache = new HashMap();
	private Map localCache = new HashMap();
	
	private LogicInvokerFactory(){
		
	}
	public static LogicInvokerFactory getInstance(){
		return factory;
	}
	
	public <T> T getBO(Class<T> clazz){
		T target = null;
		try {
			if (boCache.containsKey(clazz.getName())) {
				target = (T)boCache.get(clazz.getName());
			} else {
				ServiceBOProxy serviceBoProxy = new ServiceBOProxy();
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(clazz); 
				enhancer.setCallback(serviceBoProxy);//回调代理类
				enhancer.setClassLoader(clazz.getClassLoader());

				target = (T)enhancer.create();
				boCache.put(clazz.getName(), target);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}
	
	/**
	 * 调用此方法路由本地化代码<br>
	 * 本地化类命名规范：类路径必须跟核心版本一致，本地化类名在核心代码类名加后缀：_local
	 * @param service
	 * @param method
	 * @param params
	 * @param methodProxy
	 * @return
	 */
	public <T> T getBOForLocal(Class<T> clazz){
		T target = null;
		try {
			ServiceBOProxy serviceBoProxy = null;//new ServiceBOProxy();
			Class c = isLocal(clazz);
			String key = clazz.getName();
			if(c != null){
				serviceBoProxy = (ServiceBOProxy)c.newInstance();
				key = c.getName();
			}else{
				serviceBoProxy = new ServiceBOProxy();
			}
			if (boCache.containsKey(key)) {
				target = (T)boCache.get(key);
			} else {
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(clazz); 
				enhancer.setCallback(serviceBoProxy);//回调代理类
				enhancer.setClassLoader(clazz.getClassLoader());

				target = (T)enhancer.create();
				boCache.put(key, target);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}
	
	/**
	 * 判断本地化代码是否存在
	 * @param service
	 * @return
	 */
	private Class isLocal(Class clazz){
		String local = clazz.getName()+"Local";//组装本地化代码路径
		try{
			return Class.forName(local);
		}catch(ClassNotFoundException e){
			
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(LogicInvokerFactory.class.getSimpleName()+"_local");
	}

}