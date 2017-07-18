/**
 * 
 */
package com.ztesoft.crmpub.bpm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.flow.IFlowEngine;
import com.ztesoft.crmpub.bpm.flow.impl.FlowEngineImpl;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.task.IWoTaskEngine;
import com.ztesoft.crmpub.bpm.task.impl.WoTaskEngineImpl;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.RunTimeDto;
import com.ztesoft.crmpub.bpm.vo.model.MBpmAttrInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DaoUtils;

/**
 * @author li.jh
 * 
 */
public class FlowHelper {
	/**
	 * 启动流程
	 * 
	 * @param boId
	 *            业务系统业务单ID
	 * @param flowTypeId
	 *            流程类型ID
	 * @param flowTitle
	 *            业务单标题
	 * @param callBackHandler
	 *            回调函数
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private static boolean startFlow(RunTimeDto runTimeDto,
			ICallBackHandler callBackHandler) throws Exception {
		if (runTimeDto == null) {
			throw new RuntimeException("流程启动参数不正确!");
		}
		// 查询流程定义
		String boTypeId = runTimeDto.getBoTypeId();
		String boId = runTimeDto.getBoId();
		String boTitle = runTimeDto.getBoTitle();		

		MsgBean msg = new MsgBean();
		msg.setFlowId(runTimeDto.getFlowId());	//流程标识
		msg.setBoId(boId);// 业务单ID
		msg.setBoTypeId(boTypeId);// 业务单类型
		msg.setBoTitle(boTitle);// 业务单标题
		msg.setOperId(runTimeDto.getOperId());// 创建人
		msg.setCallBackHandler(callBackHandler);// 业务回调函数
		msg.setMsgAction(MsgConsts.ACTION_NEW_FLOW);// 新建流程动作
		msg.setMsgType(MsgConsts.MSG_TYPE_FLOW);
		msg.setMsgHandleWay(MsgConsts.MSG_HANDLE_SYNC);// 消息同步处理

		IFlowEngine flowEngine = new FlowEngineImpl();

		return flowEngine.putMsg(msg);
	}
	
	/**
	 * 启动流程
	 * @param params
	 * @param callBackHandler
	 * @return
	 * @throws Exception
	 */
	public static Map startFlow(Map params,
			ICallBackHandler callBackHandler) throws Exception {
		Map retMap = new HashMap();
		Boolean result = false;
		if (params ==null || params.size()<=0) {
			throw new RuntimeException("流程启动参数不正确!传入的params为空");
		}
		// 查询流程定义
		String boTypeId = (String)params.get("bo_type_id");
		String boId = (String)params.get("bo_id");
		String flowId = (String)params.get("flow_id");
		String boTitle = (String)params.get("bo_title");
		String operId = (String)params.get("oper_id");
		String autoFinishOrder = (String)params.get("autoFinishOrder");
		List attrList = (List)params.get("attrList");
		MsgBean msg = new MsgBean();
		
		msg.setFlowId(flowId);// 流程ID
		msg.setBoId(boId);// 业务单ID
		msg.setBoTypeId(boTypeId);// 业务单类型
		msg.setBoTitle(boTitle);// 业务单标题
		msg.setOperId(operId);// 创建人
		msg.setCallBackHandler(callBackHandler);// 业务回调函数
		msg.setMsgAction(MsgConsts.ACTION_NEW_FLOW);// 新建流程动作
		msg.setMsgType(MsgConsts.MSG_TYPE_FLOW);
		msg.setMsgHandleWay(MsgConsts.MSG_HANDLE_SYNC);// 消息同步处理
		msg.setAttrList(attrList);
		msg.setRespContent((String)params.get("resp_content") );
		msg.setNeed_sms((String)params.get("need_sms"));
		msg.setParentWoID((String)params.get("parent_wo_id") );
		msg.setParentBoId(StringUtil.getStrValue(params, "parent_bo_id"));
		msg.setParentTacheCode(StringUtil.getStrValue(params, "parent_tache_code"));
		msg.setParentBoTypeId(StringUtil.getStrValue(params, "parent_bo_type_id"));          //设置父流程相关数据
		msg.setTaskTitle(StringUtil.getStrValue(params, "task_title")); // 工单名称，必须要以“-”分隔
		
		msg.setTacheId(StringUtil.getStrValue(params, "tache_id"));
		msg.setPlanFinishTime(FlowHelper.getAttrListValue(msg, "plan_finish_time"));
		
		
		BpmContext.putVar(BPMConsts.CTX_VAR.BO_CREATOR, operId);
		
		IFlowEngine flowEngine = new FlowEngineImpl();
		result= flowEngine.putMsg(msg);
		
		String autoFinishBeginWO = (String)params.get("autoFinishBeginWO"); //是否自动完成开始环节		
		if (result && "true".equalsIgnoreCase(autoFinishBeginWO)){
			//下面应该要重新设置msg的各种值，因为第一个环节和第二个环节的msg是不一样的 modified by joshui
			if (StrUtil.isNotEmpty(StringUtil.getStrValue(params, "task_title_second_tache"))) {
				msg.setTaskTitle(StringUtil.getStrValue(params, "task_title_second_tache")); // 工单名称，必须要以“-”分隔
			}else{
				msg.setTaskTitle(""); 
			}
			
			msg.setMsgAction(MsgConsts.ACTION_WO_FINISH);// 新建流程动作
			msg.setMsgType(MsgConsts.MSG_TYPE_TASK);
	
			IWoTaskEngine woTaskEngine = new WoTaskEngineImpl();
			woTaskEngine.putMsg(msg);
		}
		//流程不处理直接结束(话务小结)
		if("true".equals(autoFinishOrder)){
			MBpmBoFlowInst  inst = msg.getFlowInst();
			if(inst!= null){
				List<MBpmWoTask> woTasks = inst.getWorkOrders();
				if(woTasks.size()>0&&woTasks!=null){
					for(MBpmWoTask woTask:woTasks){
						woTask.set("wo_state", BPMConsts.WO_STATE_FINISH);
						woTask.set("resp_content", msg.getRespContent());
						woTask.set("resp_oper_id", msg.getOperId() );
						woTask.set("resp_result",MsgConsts.BPM_SUCCESS );
						woTask.set("state_date",DaoUtils.getDBCurrentTime() );
						String[] fields = woTask.updateFieldSet.toArray(new String[]{});
						MBpmWoTask.getDAO().updateParmamFieldsByIdSQL(fields).update(woTask);	
						String flow_id = woTask.flow_id;
					}
				}
				List<IVO> Executors = MBpmWoTaskExec.getDAO().findByRootId(flowId);				
				if(Executors.size()>0&&Executors!=null){
					for(IVO VO:Executors){
						MBpmWoTaskExec exec = (MBpmWoTaskExec)VO;
						exec.exec_date = DaoUtils.getDBCurrentTime();
						exec.exec_state = BPMConsts.WO_STATE_FINISH; // 执行人状态 joshui						
						exec.getDAO().updateParmamFieldsByIdSQL(new String[]{"exec_date", "exec_interval", "exec_state"}).update(exec);						
					}
				}
				inst.set("bo_state",  BPMConsts.BO_STATE.END);
				inst.set("bo_state_name", "已完成");
				inst.set("state_date", DaoUtils.getDBCurrentTime());
				String[] fields = inst.updateFieldSet.toArray(new String[]{});
				MBpmBoFlowInst.getDAO().updateParmamFieldsByIdSQL(fields).update(inst);
				inst.finishOrder();
			}
		}
		flowId = msg.getFlowId();
		if(StrTools.isNotEmpty(flowId)){
			params.put("flowId", flowId);
			params.put("flow_id", flowId);//CRMRWGL-2616 (ID:193588)
		}
		
		retMap.put("result", result);
		retMap.put("flowId", flowId);
		return retMap;
	}
	
	/**
	 * 属性实例
	 * 保存模板字段实例值，在动作基类里被调用。 joshui
	 * @param msg
	 * @throws Exception 
	 */
	public static void createAttrInst(MsgBean msg) throws Exception{
		MBpmWoTask woTask = msg.getWoTask();
		String flow_id = woTask.flow_id;
		String bo_type_id = woTask.bo_type_id;
		String tache_code = woTask.tache_code;
		String work_type_id = woTask.wo_type_id;
		SBpmWoType woType = (SBpmWoType)SBpmWoType.getDAO().findById(work_type_id);
		String template_id = woType.template_id;
		List<HashMap> attrList = msg.getAttrList();
		if(attrList == null){
			return;
		}
		
		for(HashMap map : attrList){
			String attr_id = (String) map.get("name");
			String attr_value = (String) map.get("value");
			if(attr_value == null || attr_value.equals("")){
				continue;
			}
			if(!StringUtil.isNum(attr_id)){
				continue;
			}
			MBpmAttrInst attrInst = new MBpmAttrInst();
			attrInst.flow_id = flow_id;
			attrInst.bo_type_id = bo_type_id;
			attrInst.tache_code = tache_code;
			attrInst.template_id = template_id;
			attrInst.attr_id = attr_id;
			attrInst.attr_value = attr_value;
			attrInst.wo_id = woTask.wo_id;
			MBpmAttrInst.getDAO().insert(attrInst);
		}
	}

	/**
	 * 工单回单方法
	 * @deprecated 
	 * @param runTimeDto
	 *            回单运行参数对象
	 * @param callBackHandler
	 *            回调函数
	 * @return boolean
	 */
	private static boolean completeWorkItem(RunTimeDto runTimeDto,
			ICallBackHandler callBackHandler) throws Exception {
		if (runTimeDto == null) {
			throw new RuntimeException("流程参数不正确!");
		}
		MsgBean msg = new MsgBean();
		msg.setWoId(runTimeDto.getWoId());
		msg.setFlowId(runTimeDto.getFlowId());
		// msg.setBoId(runTimeDto.getBoId());
		// msg.setBoTypeId(runTimeDto.getBoTypeId());
		msg.setCallBackHandler(callBackHandler);// 业务回调函数
		
		msg.setOperId(runTimeDto.getOperId());// 回单人
		msg.setMsgAction(MsgConsts.ACTION_UPDATE_FLOW);// 修改流程动作
		msg.setMsgType(runTimeDto.getWorkItemType());// 工单项类型
		msg.setMsgHandleWay(MsgConsts.MSG_HANDLE_SYNC);// 消息同步处理
		IFlowEngine flowEngine = new FlowEngineImpl();
		return flowEngine.putMsg(msg);
	}
	
	
	/**
	 * 作废流程
	 * @param runTimeDto
	 * @param callBackHandler
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private static boolean invalidFlow(RunTimeDto runTimeDto,
			ICallBackHandler callBackHandler) throws Exception {
		if (runTimeDto == null) {
			throw new RuntimeException("流程参数不正确!");
		}
		MsgBean msg = new MsgBean();
		msg.setWoId(runTimeDto.getWoId());
		msg.setFlowId(runTimeDto.getFlowId());
		msg.setCallBackHandler(callBackHandler);// 业务回调函数

		msg.setOperId(runTimeDto.getOperId());// 回单人
		msg.setMsgAction(MsgConsts.ACTION_INVALID_FLOW);// 修改流程动作
		msg.setMsgType(runTimeDto.getWorkItemType());// 工单项类型
		msg.setMsgHandleWay(MsgConsts.MSG_HANDLE_SYNC);// 消息同步处理
		IFlowEngine flowEngine = new FlowEngineImpl();
		return flowEngine.putMsg(msg);
	}
	
	public static String getAttrListValue(MsgBean msg, String key){
		String value = "";
		if(msg == null){
			return value;
		}
		List<HashMap> attrList = msg.getAttrList();
		if(attrList == null){
			return value;
		}
		for (HashMap<String, String> attrMap : attrList) {
			if (key != null && key.equalsIgnoreCase(attrMap.get("name")) && StrUtil.isNotEmpty(attrMap.get("value"))) {
				value = attrMap.get("value");
			}
		}
		
		return value;
	}

}
