package spring.aspect;

import java.lang.reflect.Method;

import net.buffalo.service.invoker.ContextXAMeta;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import comx.order.inf.IContext;

import exception.CoopException;
/**
 * Description : 多数据源AOP初始化上下文，并控制事务（主要支撑多数据源方式） <br>
 * @author liu.yumng  
 * Date : 2014-03-10<br>
 *
 */
@Aspect
public class ContextXAAspect {
	private static final Logger logger = Logger.getLogger(ContextXAAspect.class);
	@Around("@annotation(net.buffalo.service.invoker.ContextXAMeta)")
	public Object doContextProfiling(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature signature = (MethodSignature) pjp.getSignature(); 
		Method method = signature.getMethod(); 
		Object result = null;
		boolean closeContext = false;
		//取得注解，识别要启动的上下文
		ContextXAMeta cm = method.getAnnotation(ContextXAMeta.class);
		if(cm != null){
			IContext c = null;
			try{
				if(IContext.getContext() == null ){
					logger.debug(" 方法 " + method.toString() + " 起动session上下文 : "+ cm.cls());
					//初始化上下文
					Class cls = Class.forName(cm.cls());
					c = (IContext)cls.newInstance();
					IContext.setContext(c);
					c.init();
					closeContext = true;
				}
				//调用方法
				result = pjp.proceed();// 执行该方法
				//提交
				c.commit();
			}catch(Throwable ex){
				ex.printStackTrace();
				if (c != null){
					//回滚
					Throwable casue = ex;
					do{
						casue = casue.getCause();
					}while (casue==null); //获取最底层的异常

					result = c.rollback(casue);
//					throw ex;
				}
				else {
					throw ex;
				}
				if(ex instanceof CoopException){
					if(CoopException.INFO.equals(((CoopException) ex).getExcType())){
						throw ex;
					}
				}
				if(ex instanceof Error){
					CoopException coopException = new CoopException(CoopException.ERROR,ex);
					throw coopException;
				}else if(ex instanceof Exception){
					CoopException coopException = new CoopException(CoopException.ERROR,ex);
					throw coopException;
				}else if(ex.getCause() instanceof CoopException){
					throw ex.getCause();
				}
			} finally{
				if(closeContext) {
					//如果是该入口起动上下文，那么退出时应该关闭
					IContext.setContext(null);
				}
			}
		}
		return result;
	}
}