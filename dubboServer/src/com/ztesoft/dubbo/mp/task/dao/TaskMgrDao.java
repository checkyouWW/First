package com.ztesoft.dubbo.mp.task.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.dubbo.mp.task.vo.CTaskService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

import comx.order.inf.IKeyValues;

@Repository
@SuppressWarnings({ "rawtypes","unchecked" })
public class TaskMgrDao {

	
	public PageModel getTaskList(Map params){
		
		
		int page = 1;
		int pagesize = 10;
		
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){};
		
		
		String sql = Sql.C_TASK_SQLS.get("get_task_list");
		
		return DAO.queryForPageModel(sql, pagesize, page, new String[]{IKeyValues.STATE_00X});
		
		
	}
	
	public void updateTaskState(String taskId,String newState){
		String sql = Sql.C_TASK_SQLS.get("update_task_state");
		DAO.update(sql, new String[]{newState,taskId});
	}

	public Map addTaskService(Map params) {
		
		CTaskService taskService = new CTaskService();
		taskService.readFromMap(params);
		taskService.apply_count = taskService.schedule_count = "0";
		taskService.create_time = taskService.state_time = DateUtil.getFormatedDateTime();
		taskService.state = KeyValues.STATE_00B;
		
		taskService.getDao().insert(taskService);
		
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		
		return returnMap;
	}
	
}
