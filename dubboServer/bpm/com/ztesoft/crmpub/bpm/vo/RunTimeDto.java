package com.ztesoft.crmpub.bpm.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 工单回单调用对象
 * @author lirx
 */
public class RunTimeDto implements Serializable{
	
	
	List<PerformerDto>  performerDtos = new ArrayList<PerformerDto>();//工单执行对象
	   
	String workItemType;//工单项类型  
	
	String operId;//操作员标识
	
	String boId;//业务单标识
	
	String boTypeId;//流程规格标识
	
	String flowId;//流程实例标识
	
	String woId;//任务单标识
	
	String boTitle;//业务单标题
	
	String taskTitle;//任务单标题
	
	String taskContent;//任务单内容
	
	String taskExecId;//任务单执行者标识
	
	String respResult;//回单结果
	
	String respContent;//回单描述
	
	
	public String getRespResult() {
		return respResult;
	}

	public void setRespResult(String respResult) {
		this.respResult = respResult;
	}

	public String getRespContent() {
		return respContent;
	}

	public void setRespContent(String respContent) {
		this.respContent = respContent;
	}

	
	public String getTaskExecId() {
		return taskExecId;
	}

	public void setTaskExecId(String taskExecId) {
		this.taskExecId = taskExecId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getTaskContent() {
		return taskContent;
	}

	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}

	   
	public String getWoId() {
		return woId;
	}

	public void setWoId(String woId) {
		this.woId = woId;
	}

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}


	public List<PerformerDto> getPerformerDtos() {
		return performerDtos;
	}

	public void setPerformerDtos(List<PerformerDto> performerDtos) {
		this.performerDtos = performerDtos;
	}

	public String getWorkItemType() {
		return workItemType;
	}

	public void setWorkItemType(String workItemType) {
		this.workItemType = workItemType;
	}
	
	public String getBoId() {
		return boId;
	}

	public void setBoId(String boId) {
		this.boId = boId;
	}

	public String getBoTypeId() {
		return boTypeId;
	}

	public void setBoTypeId(String boTypeId) {
		this.boTypeId = boTypeId;
	}

	public String getBoTitle() {
		return boTitle;
	}

	public void setBoTitle(String boTitle) {
		this.boTitle = boTitle;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
    
	/**
	 * 添加待执行人
	 * @param performer
	 */
	public void addPerformerDto(PerformerDto performer){
		this.performerDtos.add(performer);
	}


}
