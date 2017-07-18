package com.ztesoft.inf.mp.task;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings("rawtypes")
public interface ITaskMgrService {
	 
	RpcPageModel getTaskList(Map params);

	Map disabledService(Map params);

	Map enabledService(Map params);

	Map deleteService(Map params);

	Map addTaskService(Map params);
}
