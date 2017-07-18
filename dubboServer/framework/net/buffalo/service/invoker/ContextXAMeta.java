package net.buffalo.service.invoker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * spring AOP aspect支持多数据源查询仿XA方式
 * @author liu.yuming  by 2014-03-07
 *
 */
//加载在VM中，在运行时进行映射
@Retention(RetentionPolicy.RUNTIME)
//限定此annotation只能标示方法
@Target(ElementType.METHOD)
public @interface ContextXAMeta {
	String cls();  //定义对象
}
