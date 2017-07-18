package com.ztesoft.inf.mp.sys;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 定时任务信息管理
 * 
 * @author
 *
 */
public interface IJobMgrService {
	
	public RpcPageModel getQuartzScheduleJobs(Map params);

	public Map addJob(Map params);

	public Map editJob(Map params);

	public Map deleteJob(Map params);

	/**
	 * 获取日志列表
	 * @param params
	 * @return
	 */
	RpcPageModel getLogList(Map params);
}
