package com.ztesoft.dubbo.mp.task.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.task.dao.TaskMgrDao;
import com.ztesoft.inf.mp.task.ITaskMgrService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "rawtypes","unchecked" })
public class TaskMgrService implements ITaskMgrService {

	@Resource
	private TaskMgrDao dao;
	
	@Transactional
	public RpcPageModel getTaskList(Map params){
		return PageModelConverter.pageModelToRpc(dao.getTaskList(params));
	}

	@Transactional
	@Override
	public Map disabledService(Map params){
		String serviceId = Const.getStrValue(params, "service_id");
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		if(StringUtil.isEmpty(serviceId)) return returnMap;
		this.dao.updateTaskState(serviceId, KeyValues.STATE_00S);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}
	
	@Transactional 
	@Override
	public Map enabledService(Map params){
		String serviceId = Const.getStrValue(params, "service_id");
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		if(StringUtil.isEmpty(serviceId)) return returnMap;
		this.dao.updateTaskState(serviceId, KeyValues.STATE_00A);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}
	
	@Transactional 
	@Override
	public Map deleteService(Map params){
		String serviceId = Const.getStrValue(params, "service_id");
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		if(StringUtil.isEmpty(serviceId)) return returnMap;
		this.dao.updateTaskState(serviceId, KeyValues.STATE_00X);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}
	
	@Transactional 
	@Override
	public Map addTaskService(Map params){
		return dao.addTaskService(params);
	}
	
}
