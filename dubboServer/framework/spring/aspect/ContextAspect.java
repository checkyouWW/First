package spring.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import comx.order.inf.IContext;
import exception.CoopException;
import net.buffalo.service.invoker.ContextMeta;


@Aspect
public class ContextAspect {
	
	@Around("@annotation(org.springframework.transaction.annotation.Transactional)")
	public Object doContextProfiling(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature signature = (MethodSignature) pjp.getSignature(); 
		Method method = signature.getMethod(); 
		Object result = null;
		boolean closeContext = false;
		//取得注解，识别要启动的上下文
		ContextMeta cm = method.getAnnotation(ContextMeta.class);
		IContext c = null;
		try{
			if(IContext.getContext() == null ){
				String cls = "comx.order.inf.DwrWebContext";
				if(cm != null){
					cls = cm.cls();
				}
				//初始化上下文
				Class clz = Class.forName(cls);
				c = (IContext)clz.newInstance();
				IContext.setContext(c);
				c.init();
				closeContext = true;
			}
			//调用方法
			result = pjp.proceed();// 执行该方法
		}catch(Throwable ex){
			ex.printStackTrace();
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
		return result;
	}
}
