package com.ztesoft.common.util;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/** 
 * @Description: 记录dubbo服务调用耗时
 * @author joshui  
 * @date 2016年5月17日 下午2:13:56 
 * @version V1.0
 */
@Component
@Aspect
public class CountExeTimeAop {
	private static final Logger logger = Logger.getLogger(CountExeTimeAop.class);
	
//	@Pointcut("execution(* com.ztesoft.dubbo.provider..*.*(..))")
	@Pointcut("execution(* com.ztesoft.inf..*Service.*(..))")
	private void dubboService() {
	}

	@Around("dubboService()")
	public Object doLogExeTime(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("dubbo服务【" + pjp.getTarget().getClass() + 
					"." + pjp.getSignature().getName() + "】开始执行(" + start + ")：");
		}
		Object retVal = pjp.proceed();
		
		long end = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("dubbo服务【" + pjp.getTarget().getClass() + 
					"." + pjp.getSignature().getName() + "】结束执行，执行耗时(" + start + ")："
					+ (end - start));
		}
		return retVal;
	}
}
