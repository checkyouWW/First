package com.ztesoft.inf.util.preventattack.thread;

/**
 * @Description: </br>
 * @author： huang.shaobin</br>
 * @date： 2016年5月20日
 */
public interface IThreadPoolDeque<T> {
	
	void add(T run);

	void start();
	
	void stop();
	
}
