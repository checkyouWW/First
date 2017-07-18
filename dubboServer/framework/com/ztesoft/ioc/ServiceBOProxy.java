package com.ztesoft.ioc;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ServiceBOProxy implements MethodInterceptor{

	@Override
	public Object intercept(Object service, Method method, Object[] params,
			MethodProxy methodProxy) throws Throwable {
		Object result = null;
		Exception error = null;
		
		try{
			result= methodProxy.invokeSuper(service, params);
		}catch(Exception th){
			th.printStackTrace();
			error = th;
		}catch (Throwable th) {
			th.printStackTrace();
			error = new RuntimeException(th);
		} finally {
			if(error != null) throw error;
		}
		return result;
	}

}
