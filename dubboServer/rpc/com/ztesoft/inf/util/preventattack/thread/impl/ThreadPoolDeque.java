package com.ztesoft.inf.util.preventattack.thread.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import com.ztesoft.inf.util.preventattack.thread.IThreadPoolDeque;

/**
 * @Description: 队列实现  </br>
 * @author： huang.shaobin</br>
 * @date： 2016年5月20日
 */
public class ThreadPoolDeque implements IThreadPoolDeque<Runnable> {
	
	private static final Logger log = Logger.getLogger(ThreadPoolDeque.class);

	private static  ThreadPoolDeque threadPoolDeque =new ThreadPoolDeque();
	
	private ExecutorService executors;
	
	//已链接节点的、任选范围的阻塞双端队列，头尾添加删除
	private static Deque<Runnable> deq = new LinkedBlockingDeque<Runnable>();
	
	private static boolean flag = false; //启动/停止开关标识
	
	private ThreadPoolDeque(){
		executors = Executors.newCachedThreadPool();//可变尺寸的线程池
	}
	
	public static ThreadPoolDeque getInstance(){
		if (threadPoolDeque==null) {
			threadPoolDeque=new ThreadPoolDeque();
		}
		return threadPoolDeque;
	}
	
	private Thread executorThread=new Thread(new Runnable() {
		public void run() {
			while(true) {
				if (flag) {
					break;
				}
				Runnable info = deq.poll();
				if (info == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					continue;
				}
				
				executors.execute(info);
			}
		}
	});
	
	
	public void add(Runnable at) {
		deq.offer(at);
	}
	
	public void start() {
		executorThread.setName("ExecutorThread");
		executorThread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
		executorThread.start();
	}
	
	public void stop() {
		try {
			flag = true;
			executorThread.interrupt();
			executors.shutdown();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (log.isInfoEnabled()) {
			log.info("----------------ThreadPoolDeque stop !------------------");
		}
	}
	
	
	
    class ThreadExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
			if (log.isInfoEnabled()) {
				log.warn("----------------------------主线程出现异常，重新启动线程--------------------------------");
			}
			start();
		}
    	
    }
	

	
	
}

