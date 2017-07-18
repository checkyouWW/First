package com.ztesoft.inf.mp.task;

import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes" })
public interface ITaskService {

	public String getApplyCode(Map params);
	
	public boolean saveTask(Map params) throws Exception;
	
	public String insertRelyFile(Map params);
	
	public boolean deleteRelyFile(List<Map> files);
	
	public Map queryTask(Map params);

	public Map updateTaskApplyAudit(Map m);
	
	public Map updateTaskApplyInfo(Map params) throws Exception;

	public Map insertMDataChangeNotify(Map params);
	
}
