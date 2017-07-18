package com.ztesoft.dubbo.mp.sys.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.dubbo.mp.sys.bo.JobMgrBO;
import com.ztesoft.inf.mp.sys.IJobMgrService;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.ioc.LogicInvokerFactory;

/**
 * 定时任务信息管理
 * 
 * @author
 *
 */
@Service("jobMgrService")
@SuppressWarnings("rawtypes")
public class JobMgrService implements IJobMgrService {

	private JobMgrBO getBO() {
		return LogicInvokerFactory.getInstance().getBO(JobMgrBO.class);
	}
	
	@Transactional
	public RpcPageModel getQuartzScheduleJobs(Map params) {
		return getBO().getQuartzScheduleJobs(params);
	}
	
	@Transactional
	public Map addJob(Map params) {
		return getBO().addJob(params);
	}
	
	@Transactional
	public Map editJob(Map params) {
		return getBO().editJob(params);
	}
	
	@Transactional
	public Map deleteJob(Map params) {
		return getBO().deleteJob(params);
	}
	
	@Transactional
	@Override
	public RpcPageModel getLogList(Map params){
		return PageModelConverter.pageModelToRpc(getBO().getLogList(params));
	}

}
