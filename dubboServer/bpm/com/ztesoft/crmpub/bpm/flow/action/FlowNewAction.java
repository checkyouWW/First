/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow.action;

import java.util.List;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.handler.IHandler;
import com.ztesoft.crmpub.bpm.util.WoTaskHelper;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.spec.BpmSpecCache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;

import appfrm.app.util.SeqUtil;
import appfrm.app.util.StrUtil;

/**
 * @author li.jh
 *
 */

public class FlowNewAction extends AbstractFlowAction implements IFlowAction {
	public boolean execute() throws Exception {
		boolean result = false;

		String boTypeId = msg.getBoTypeId();//业务单类型ID
		if(StrUtil.isEmpty(boTypeId)){
			throw new RuntimeException("无法启动流程,原因：传入的boTypeId为空");
		}

		// 1. 加载流程、环节规格数据
		
		SBpmBoFlowDef flowDef =BpmSpecCache.getFlowDef(boTypeId);
		flowDef.setMsg(msg);
		SBpmBoFlowTache beginTache = flowDef.getBeginTahce();
		if (beginTache == null) {
			throw new RuntimeException("无法启动流程,原因：流程没有配置开始环节TACHE_TYPE=BEGIN, 请检查环节配置表BPM_BO_FLOW_TACHE!");
		}

		// 2.生成流程实例等
		MBpmBoFlowInst flowInst = new MBpmBoFlowInst();	
		if( StrUtil.isNotEmpty( msg.getFlowId() ) ){
			flowInst.flow_id = msg.getFlowId();
		}else{
			String flowId = SeqUtil.getInst().getNext(flowInst.getTableCode(), "FLOW_ID");
			msg.setFlowId(flowId);
			flowInst.flow_id = flowId;
		}
		flowInst.bo_type_id =boTypeId;
		flowInst.flow_name = flowDef.flow_name;//流程名称
		if (StrUtil.isNotEmpty(msg.getBoTitle())){
			flowInst.bo_title =  msg.getBoTitle();//业务单名称
		}else {
			flowInst.bo_title = flowDef.flow_name;
		}		
		flowInst.bo_id    =  msg.getBoId();//业务单ID
		flowInst.bo_state =  beginTache.bo_state;//业务单状态
		flowInst.table_code = beginTache.tache_code;//当前流程环节编码
		flowInst.bo_state_name = BPMConsts.FIRST_TACHE_STATE_NAME;//业务单状态名称
		flowInst.table_code    = flowDef.table_code;//业务表
		flowInst.table_pk_col  = flowDef.table_pk_col;//业务表主键
		flowInst.create_oper_id = msg.getOperId();// 流程创建人
		flowInst.create_date    = DateUtil.getFormatedDateTime();// 流程创建时间
		flowInst.state_date = flowInst.create_date;
		flowInst.template_id = flowDef.template_id;//场景/模板ID act.act_id
		flowInst.parent_wo_id = msg.getParentWoID();	//父流程的工单表bpm_wo_task的主键，如果有父流程的话
		flowInst.parent_bo_id = msg.getParentBoId();
		flowInst.parent_bo_type_id = msg.getParentBoTypeId();
		flowInst.parent_tache_code = msg.getParentTacheCode();
		flowInst.plan_finish_time = msg.getPlanFinishTime();
		flowInst.tache_id = msg.getTacheId();
		
		flowInst.getDao().insert(flowInst);
		//3.生成流程实例的同时触发流程创建事件  liu.yuming 2013-07-16 start
		String className = flowDef.create_handler;
		if(className != null && !"".equals(className)){
			Class clazz = Class.forName(className);
			IHandler handler = (IHandler)clazz.newInstance();
			handler.setMsg(flowInst);
			handler.setMsgBean(msg); //added by joshui
			handler.excuete();
		}
		//end;
		
		// 4.生成开始环节工单，并派给创建人
		WoTaskHelper.genWorkerOrderAndDispatch(flowInst, beginTache, msg);
		
		// 5.执行业务回调函数
		ICallBackHandler callBackHandler = msg.getCallBackHandler();
		if (callBackHandler != null) {
			callBackHandler.processMsg(msg);
		}
		
		// 6.包装返回
		msg.setResult(MsgConsts.BPM_SUCCESS);
		msg.setResultName(MsgConsts.NEW_FLOW_SUCCESS_NAME);
		msg.setFlowInst(flowInst);
		result = true;
		
		return result;
	}

	private List loadAttrTemlate(String flowTypeId) {
		// TODO 加载业务单流程属性模板
		return null;
	}

}
