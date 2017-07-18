package com.ztesoft.inf.mp.task;

import java.util.Map;

@SuppressWarnings("all")
public interface ITaskScheduleService {

	public Map queryServiceOrder(Map params);
	
	public String insertServiceOrder(Map params);
	
	public boolean updateServiceOrder(Map<String, String> params);

}
