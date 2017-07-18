/**
 * 
 */
package com.ztesoft.crmpub.bpm.task.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.util.FlowHelper;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.ListUtil;
import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DAO;
import appfrm.resource.dao.impl.DaoUtils;

/**
 * @author major
 *
 */
public class WorkOrderBaseAction implements IWorkOrderAction {

	protected MsgBean msg = null;
	
	/*
	 * (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction#before()
	 */
	@Override
	public boolean before() throws Exception {
		if(msg.getWoTask() == null){
			this.getWoTask();
		}
		FlowHelper.createAttrInst(msg);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction#execute()
	 */
	@Override
	public boolean execute() throws Exception {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction#after()
	 */
	@Override
	public boolean after() throws Exception {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction#setMsg(com.ztesoft.crmpub.bpm.vo.MsgBean)
	 */
	@Override
	public void setMsg(MsgBean msg) {
		this.msg = msg;
	}

	/* (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction#getWoTask(com.ztesoft.crmpub.bpm.vo.MsgBean)
	 */
	@Override
	public MBpmWoTask getWoTask() {
		//1.获取工单实例
		String woId = msg.getWoId();	
		MBpmWoTask woTask = msg.getWoTask();
		if (StrUtil.isEmpty(woId)&& woTask==null){
			throw new RuntimeException("WorkOrderBaseAction执行失败,原因消息中的工单woId、工单实例均为空") ;
		}
		
		if (StrUtil.isEmpty(woId)){
			woId = woTask.wo_id;
			msg.setWoId(woId);
		}

		if (woTask==null){
			woTask = (MBpmWoTask) MBpmWoTask.getDAO().findById(	msg.getWoId());
			msg.setWoTask(woTask);
		}
		return woTask;
	}
	
	@Override
	public SBpmWoType getWoType() {
		
		SBpmWoType woType = msg.getWoType();
		if( null == woType ){
			MBpmWoTask woTask = this.getWoTask();
			woType = (SBpmWoType)SBpmWoType.getDAO().findById(woTask.wo_type_id);
			return woType;
		}else{
			return woType;
		}
	}
	
	/**
	 * 根据任务单名称得到流程名称
	 * @param woTask
	 * @return
	 * Author : joshui
	 * Date ：2014-7-30
	 */
	public String getBoTitleFromWO(MBpmWoTask woTask){
		String taskTitle = woTask.task_title;
		return taskTitle.substring(0, taskTitle.lastIndexOf("-"));
	}
	
	/**
	 * 处理当前环节的工单执行人。
	 * 目前只更新完成时间、执行时长
	 * @param msg
	 * @throws Exception
	 * Author : joshui
	 * Date ：2014-11-27
	 */
	public void dealCurWoTaskExec(MsgBean msg) throws Exception {
		List<MBpmWoTaskExec> list = (List)MBpmWoTaskExec.getDAO().newQuerySQL(" wo_id=? and task_worker=? ").
			findByCond(msg.getWoTask().wo_id, msg.getOperId());
		for (MBpmWoTaskExec exec : list) {
			exec.exec_date = DaoUtils.getDBCurrentTime();
			
			if(StringUtil.isEmpty(exec.exec_interval)){
				exec.exec_interval = "-1";
			}
			exec.exec_state = BPMConsts.WO_STATE_FINISH; // 执行人状态 joshui
			
			exec.getDAO().updateParmamFieldsByIdSQL(new String[]{"exec_date", "exec_interval", "exec_state"}).update(exec);
		}
		
	}
	
	/**
	 * 当前工单类型是并行工单，只有并行工单都执行完后，才继续往下走
	 * @param wo_id
	 * @param flow_id
	 * @param task_type
	 * @return true 已完成 false 没完成
	 * Author : joshui
	 * Date ：2014-12-11
	 */
	protected boolean isMultiTaskDone(String wo_id, String flow_id, String task_type) {
		if (BPMConsts.TASK_TYPE_MULTI_TASK_AND.equals(task_type)) {
			String sql = 
				"select count(*) as flag from bpm_wo_task t where t.wo_id <> ? and t.resp_oper_id is null AND t.flow_id = ?";
			List<Map<String, String>> countL = DAO.queryForMap(sql, new String[]{wo_id, flow_id});
			if (!"0".equals(countL.get(0).get("flag"))) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<MBpmWoTaskExec> getWoTaskExecs(String wo_id){
		List<MBpmWoTaskExec> list = (List)MBpmWoTaskExec.getDAO().query(" wo_id=? ", wo_id);
		return list;
	}
	
	/**
	 * 获取curr_task_worker的执行人记录
	 * @param list
	 * @param curr_task_worker
	 * @return
	 */
	public List<MBpmWoTaskExec> getCurrWoTaskExec(List<MBpmWoTaskExec> list, String curr_task_worker){
		List<MBpmWoTaskExec> result = new ArrayList<MBpmWoTaskExec>();
		for (MBpmWoTaskExec exec : list) {
			String task_worker = exec.task_worker;
			if(task_worker.equals(curr_task_worker)){
				result.add(exec);
			}
		}
		return result;
	}
	
	/**
	 * 获取不是curr_task_worker的执行人记录
	 * @param list
	 * @param curr_task_worker
	 * @return
	 */
	public List<MBpmWoTaskExec> getOtherWoTaskExec(List<MBpmWoTaskExec> list, String curr_task_worker){
		List<MBpmWoTaskExec> result = new ArrayList<MBpmWoTaskExec>();
		for (MBpmWoTaskExec exec : list) {
			String task_worker = exec.task_worker;
			if(!task_worker.equals(curr_task_worker)){
				result.add(exec);
			}
		}
		return result;
	}
	
	/**
	 * 处理环节的工单执行人。
	 * 目前只更新完成时间、状态、执行时长
	 * @param msg
	 * @throws Exception
	 */
	public void dealWoTaskExec(List<MBpmWoTaskExec> list, String exec_state) throws Exception {
		if(ListUtil.isEmpty(list)){
			return;
		}
		for (MBpmWoTaskExec exec : list) {
			exec.set("exec_state", exec_state);
			exec.set("exec_date", DaoUtils.getDBCurrentTime());
			exec.set("resp_content", msg.getRespContent());
			
			Set<String> updateSet = exec.getUpdateFieldSet();
			if(updateSet != null){
				MBpmWoTaskExec.getDAO().updateParmamFieldsByIdSQL(updateSet.toArray(new String[]{})).update(exec);
			}
		}
	}
}
