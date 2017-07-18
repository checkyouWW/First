/**
 * 
 */
package com.ztesoft.crmpub.bpm.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.AbstractEventHandler;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoMsg;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import exception.CoopException;

/**
 * @author major
 * 
 */
public class MsgBean {
	
	private List<HashMap> attrList= new ArrayList<HashMap>();

	public List<HashMap> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<HashMap> attrList) {
		this.attrList = attrList;
	}

	private String msgType;    //工单项类型
	private String msgAction;  //消息动作
    private String flowId;     //流程流水ID
    private String boTitle;    //业务单标题
    private String boId;       //业务单ID，一般为业务表的主键
    private String boTypeId;   //业务单类型ID
    
    private String woId;       //工单ID
    private String woTypeId;   //工单类型ID
    private String respContent; //回单内容
    private String need_sms; //是否发送短信
    private String taskWorker; //工单执行者
    private String workerType; //工单执行者类型
    private String taskTitle; //工单标题。必须要以"-"分隔
    
    private String backToFirst;//回退到第一个环节
    private String backToTacheCode;//回退到具体环节
    
	private String isNormal;   //是否正常撤单（归档）
    private String withdrawType;  //撤单类型，当存在多种撤单时判断撤单类型
    
    
    private AbstractEventHandler msgSender; // 给流程发消息的事件处理器

    private String operId;  //事件的操作员        
    private String msgHandleWay;  //消息处理方式
    private String result; //处理结果 参见：MsgConsts        
    private String resultName; //处理结果名称
    
    protected ICallBackHandler callBackHandler;   //回调接口，用于流程流转完毕后调用业务模块，比如持久化业务数据、工单数据等
    private String cbHandleResult; //回调处理结果 -999 未处理 1处理完成  0处理失败, 参见：BPMConsts
    private String cbHandleMessage; //回调处理结果消息

    private String boState; //流程状态
    private String boStateName; //流程状态
    
    private String parentWoID; //父流程的工单表bpm_wo_task的主键
    
	
    private String parentTacheCode ;
    private String parentBoId;
    private String parentBoTypeId;
    
    private String tacheId;
    
    private String plan_finish_time;
    
    private Map param;
    
	private MBpmBoFlowInst flowInst;
	
	private MBpmWoTask woTask;
	
	private SBpmBoFlowTache flowTache;
	
	private SBpmBoFlowDef flowDef;
	
	private SBpmWoType woType;
	
	private Map flowGrade; //评分Map
	
    public SBpmWoType getWoType() {
		return woType;
	}

	public void setWoType(SBpmWoType woType) {
		this.woType = woType;
	}

	public MBpmBoFlowInst getFlowInst() {
		return flowInst;
	}

	public void setFlowInst(MBpmBoFlowInst flowInst) {
		this.flowInst = flowInst;
	}
	
	
	public String getBoState() {
		return boState;
	}

	public void setBoState(String nextState) {
		this.boState = nextState;
	}

	public String getBoId() {
		return boId;
	}

	public void setBoId(String boId) {
		this.boId = boId;
	}
	
	public MsgBean() {

	}
	
	public String getBoTypeId() {
		return boTypeId;
	}

	public void setBoTypeId(String boTypeId) {
		this.boTypeId = boTypeId;
	}


	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public AbstractEventHandler getMsgSender() {
		return msgSender;
	}

	public void setMsgSender(AbstractEventHandler msgSender) {
		this.msgSender = msgSender;
	}



	public void setOperId(String operId) {
		this.operId = operId;
	}

	public String getOperId() {
		return operId;
	}

	public void setMsgHandleWay(String msgHandleWay) {
		this.msgHandleWay = msgHandleWay;
	}

	public String getMsgHandleWay() {
		return msgHandleWay;
	}
	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}


	public ICallBackHandler getCallBackHandler() {
		return callBackHandler;
	}

	public void setCallBackHandler(ICallBackHandler callBackHandler) {
		this.callBackHandler = callBackHandler;
	}
	



	public String getCbHandleResult() {
		return cbHandleResult;
	}

	public void setCbHandleResult(String cbHandleResult) {
		this.cbHandleResult = cbHandleResult;
	}

	public String getCbHandleMessage() {
		return cbHandleMessage;
	}

	public void setCbHandleMessage(String cbHandleMessage) {
		this.cbHandleMessage = cbHandleMessage;
	}

	public String getWoId() {
		return woId;
	}

	public void setWoId(String woId) {
		this.woId = woId;
	}

	public String getMsgAction() {
		return msgAction;
	}

	public void setMsgAction(String msgAction) {
		this.msgAction = msgAction;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultName() {
		return resultName;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	public String getBoTitle() {
		return boTitle;
	}

	public void setBoTitle(String boTitle) {
		this.boTitle = boTitle;
	}

	public void add2AttrMaps(String attrId, HashMap attrMap) {
		attrMap.put(attrId, attrMap);
	}

	public RunTimeDto getRunTimeDto() {
		return null;
	}

	/**
	 * @param woTask the woTask to set
	 */
	public void setWoTask(MBpmWoTask woTask) {
		this.woTask = woTask;
	}

	/**
	 * @return the woTask
	 */
	public MBpmWoTask getWoTask() {
		return woTask;
	}

	public void setBoStateName(String boStateName) {
		this.boStateName = boStateName;
	}

	public String getBoStateName() {
		return boStateName;
	}

	public void setRespContent(String respContent) {
		this.respContent = respContent;
	}

	public String getRespContent() {
		return respContent;
	}

	 /**
     * 保存消息
     * @param msg
     * @return
     */
    public static boolean saveBoMsg(MsgBean msg){
    	MBpmBoMsg boMsg = new MBpmBoMsg();
   	 
		boMsg.flow_id = msg.getFlowId();
		boMsg.msg_action = msg.getMsgAction();
		boMsg.msg_type = msg.getMsgType();
		boMsg.send_handler = msg.getMsgSender() != null ? msg.getMsgSender().toString() : "";
		boMsg.bo_id = msg.getBoId();
		boMsg.bo_type_id = msg.getBoTypeId();
		boMsg.wo_id = msg.getWoId();
		boMsg.msg_result = msg.getResult();
		boMsg.oper_id = msg.getOperId();
		boMsg.bo_state = msg.getBoState();
		boMsg.resp_content = msg.getRespContent();		
		boMsg.accept_date = DaoUtils.getDBCurrentTime();
		
		boMsg.getDao().insert(boMsg);
    	return true;
    }

	public void setWoTypeId(String woTypeId) {
		this.woTypeId = woTypeId;
	}

	public String getWoTypeId() {
		return woTypeId;
	}

	public void setTaskWorker(String taskWorker) {
		this.taskWorker = taskWorker;
	}

	public String getTaskWorker() {
		return taskWorker;
	}

	public void setWorkerType(String workerType) {
		this.workerType = workerType;
	}

	public String getWorkerType() {
		return workerType;
	}

	public void setFlowTache(SBpmBoFlowTache flowTache) {
		this.flowTache = flowTache;
	}

	public SBpmBoFlowTache getFlowTache() {
		return flowTache;
	}

	public void setFlowDef(SBpmBoFlowDef flowDef) {
		this.flowDef = flowDef;
	}

	public SBpmBoFlowDef getFlowDef() {
		return flowDef;
	}

	public void setParam(Map param) {
		this.param = param;
	}

	public Map getParam() {
		return param;
	}

	public String getParentWoID() {
		return parentWoID;
	}

	public void setParentWoID(String parentWoID) {
		this.parentWoID = parentWoID;
	}

	public String getParentBoId() {
		return parentBoId;
	}

	public void setParentBoId(String parentBoId) {
		this.parentBoId = parentBoId;
	}

	public String getParentBoTypeId() {
		return parentBoTypeId;
	}

	public void setParentBoTypeId(String parentBoTypeId) {
		this.parentBoTypeId = parentBoTypeId;
	}

	public String getParentTacheCode() {
		return parentTacheCode;
	}

	public void setParentTacheCode(String parentTacheCode) {
		this.parentTacheCode = parentTacheCode;
	}

	public String getBackToFirst() {
		return backToFirst;
	}

	public void setBackToFirst(String backToFirst) {
		this.backToFirst = backToFirst;
	}

	public String getTacheId() {
		return tacheId;
	}

	public void setTacheId(String tacheId) {
		this.tacheId = tacheId;
	}

	public String getIsNormal() {
		return isNormal;
	}

	public void setIsNormal(String isNormal) {
		this.isNormal = isNormal;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}

	public String getTaskTitle() {
		return taskTitle;
	}
	

	public String getNeed_sms() {
		return need_sms;
	}

	public void setNeed_sms(String need_sms) {
		this.need_sms = need_sms;
	}

	/**
	 * 
	 * @param taskTitle
	 * Author : joshui
	 * Date ：2014-7-29
	 */
	public void setTaskTitle(String taskTitle) {
		if (StrUtil.isNotEmpty(taskTitle) && taskTitle.indexOf("-") == -1) {
			// @TODO joshui 必须要以"-"分隔，否则抛异常。因为action里使用了“-”来split这个字段
			//throws new CoopException()
		}
		
		this.taskTitle = taskTitle;
	}

	public Map getFlowGrade() {
		return flowGrade;
	}

	public void setFlowGrade(Map flowGrade) {
		this.flowGrade = flowGrade;
	}

	public String getPlanFinishTime() {
		return plan_finish_time;
	}

	public void setPlanFinishTime(String plan_finish_time) {
		this.plan_finish_time = plan_finish_time;
	}
	
	public String getBackToTacheCode() {
		return backToTacheCode;
	}

	public void setBackToTacheCode(String backToTacheCode) {
		this.backToTacheCode = backToTacheCode;
	}


}
