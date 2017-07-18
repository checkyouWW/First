package com.ztesoft.crmpub.bpm.task.action;

import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.util.WoTaskHelper;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DaoUtils;
/**
 * 工单正常回单
 * 
 * @author major
 * 
 */
public class WorkOrderFinishAction extends WorkOrderBaseAction implements IWorkOrderAction {

	public boolean execute() throws Exception {
		/*
		 * 每个对象耦合太多了，set来set去的，交叉引用。 joshui
		 * */
		//临时解决办法 从处理结果attr中获取cus_type为action的属性 add  by xu.zhaomin
		//去掉临时解决办法，路由表加上了action_type字段，支持审批不通过时，也能走路由 joshui
        /*String action=BPMConsts.WO_STATE_FINISH;
        List<HashMap> attrList=msg.getAttrList();
        if(attrList!=null&&attrList.size()>0){
            for(Map attr:attrList){
                String type=StrTools.getStrValue(attr,"cus_type");
                if(StrTools.isNotEmpty(type)&&type.equals("action")){
                    String value=StrTools.getStrValue(attr,"value");
                    if(StrTools.isNotEmpty(value)) {
                        if(value.equals(MsgConsts.ACTION_WO_FINISH)) {
                            action = BPMConsts.WO_STATE_FINISH;
                            break;
                        }else if(value.equals(MsgConsts.ACTION_WO_FAIL)){
                            action = BPMConsts.WO_STATE_FAIL;
                            break;
                        }
                    }
                }
            }
        }*/
        
		// 当前环节工单
		MBpmWoTask woTask = getWoTask();
		BpmContext.putVar("bo_id", msg.getBoId());
		woTask.set("wo_state", BPMConsts.WO_STATE_FINISH);
		woTask.set("resp_content", msg.getRespContent());
		woTask.set("resp_oper_id", msg.getOperId() );
		woTask.set("resp_result",MsgConsts.BPM_SUCCESS );
		woTask.set("state_date",DaoUtils.getDBCurrentTime() );
		String[] fields = woTask.updateFieldSet.toArray(new String[]{});
		MBpmWoTask.getDAO().updateParmamFieldsByIdSQL(fields).update(woTask);
		msg.setBoTitle(getBoTitleFromWO(woTask));
		
		// 当前环节执行人更新
		dealCurWoTaskExec(msg);
		
		// 当前流程实例
		MBpmBoFlowInst flowInst = msg.getFlowInst();
		if (flowInst == null) {
			flowInst = (MBpmBoFlowInst) MBpmBoFlowInst.getDAO().findById(woTask.flow_id);
			msg.setFlowInst(flowInst);
		}
		
		// 当前流程定义
		SBpmBoFlowDef bpmBoFlowDef = flowInst.getFlowDef();
		bpmBoFlowDef.setMsg(msg);
		msg.setFlowDef(bpmBoFlowDef);
		
		// 更新当前流程实例
		SBpmBoFlowTache currFlowTache = bpmBoFlowDef.getTacheByCode(woTask.tache_code);
		msg.setFlowTache(currFlowTache);
        flowInst.set("bo_state",  BPMConsts.BO_STATE.ACTIVITY);
		flowInst.set("bo_state_name", currFlowTache.bo_state_name);
		flowInst.set("state_date", DaoUtils.getDBCurrentTime());
		fields = flowInst.updateFieldSet.toArray(new String[]{});
		MBpmBoFlowInst.getDAO().updateParmamFieldsByIdSQL(fields).update(flowInst);
		
		// 当前工单类型是并行工单，只有并行工单都执行完后，才继续往下走
		if(!isMultiTaskDone(woTask.wo_id, woTask.flow_id, woTask.task_type)){
			return true;
		}
		 
		// 处理下环节工单
		SBpmBoFlowTache nextFlowTache = bpmBoFlowDef.getNextTache(woTask.tache_code);
		if (nextFlowTache == null) {
			SBpmBoFlowTache lastTache = bpmBoFlowDef.getLastTache();
			if(lastTache != null){
				//以最后一个环节的结束状态名称为准
                flowInst.set("bo_state",  BPMConsts.BO_STATE.END);
				flowInst.set("bo_state_name", lastTache.bo_state_name);
			}
			flowInst.finishOrder();
			msg.setResult(MsgConsts.BPM_SUCCESS);
			msg.setResultName(MsgConsts.FINISH_FLOW_SUCCESS_NAME);
		} else {
			WoTaskHelper.genWorkerOrderAndDispatch(flowInst, nextFlowTache, msg);
		}
		
		// 调用环节事件
		SBpmWoType woType = this.getWoType();
		if( null != woType){
			boolean done = this.callBack(woType);
			if( done ){
				sendSms(flowInst, bpmBoFlowDef, nextFlowTache,msg.getNeed_sms());
			}
			return done;
		}
		
		return true;
	}
	
	private boolean callBack(SBpmWoType woType) throws Exception{
		if(StrUtil.isEmpty(woType.finsh_event_handler)){
			return true;
		}
		Class specClass = Class.forName(woType.finsh_event_handler);
		Object handlerObj = specClass.newInstance();
		if ( handlerObj  instanceof ICallBackHandler ) {
			ICallBackHandler handler = (ICallBackHandler)handlerObj;
			return handler.processMsg(msg);
		}else{
			throw new RuntimeException("工单定义【"+woType.wo_type_id+"】的完成处理器类型不正确！需要实现接口【ICallBackHandler】！") ;
		}
	}
	 
	/**
	 * 发送短信
	 * @param flowInst
	 * @param bpmBoFlowDef
	 * @param nextFlowTache
	 * @param need_sms 页面传过来的是否发送短信
	 */
	private void sendSms(MBpmBoFlowInst flowInst, SBpmBoFlowDef bpmBoFlowDef,
			SBpmBoFlowTache nextFlowTache, String need_sms) {/*
		String sms = "";
		
		if (nextFlowTache == null) {
			List<SBpmWoType> woTypes = (List)SBpmWoType.getDAO().query(" bo_type_id = ? and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'", bpmBoFlowDef.bo_type_id);
			if( null!=woTypes && woTypes.size()>0 ){
				
				//如果页面没有传是否发送短信,则使用后台的配置的
				if(StringUtils.isBlank(need_sms)){
					need_sms = woTypes.get(0).need_sms;
				}
				if( "1".equals(need_sms) ){
					List<MLBpmWoTaskExec> execs = (List)MLBpmWoTaskExec.getDAO().query(" flow_id = ? and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'", flowInst.flow_id);
					if( null != execs && execs.size() > 0 ){
						MLBpmWoTaskExec exec = execs.get(0);
						//获取联系人电话
						String mobile = WoTaskHelper.getWorkerMobile(exec.task_worker, exec.worker_type);
                        if(mobile!=null&&!mobile.isEmpty()) {
                        	//获取短信内容
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("flow_name", bpmBoFlowDef.flow_name);
                            params.put("bo_title", flowInst.bo_title);
                            sms = SmsUtil.getSmsSysTemplate(IKeyValuesLocal.SMS_SYS_TEMPLATE_SCENE_CODE_00101, params);
                            if (StringUtils.isBlank(sms)) {
                                sms = "您的流程：" + bpmBoFlowDef.flow_name + "—【" + flowInst.bo_title + "】已经完成,欢迎查看。";
                            }
                            //发送
                            if (StringUtils.isNotBlank(mobile)) {
                                SmsUtil.push(mobile, sms);
                            }
                        }
					}
				}
			}
		}else{
			List<SBpmWoType> woTypes = (List)SBpmWoType.getDAO().query(" bo_type_id = ? and tache_code = ?", bpmBoFlowDef.bo_type_id,nextFlowTache.tache_code);
			if( null!=woTypes && woTypes.size()>0 ){
				
				//如果页面没有传是否发送短信,则使用后台的配置的
				if(StringUtils.isBlank(need_sms)){
					need_sms = woTypes.get(0).need_sms;
				}
				if( "1".equals(need_sms) ){
					
					List<MBpmWoTaskExec> execs = (List)MBpmWoTaskExec.getDAO().query(" wo_id = ? and tache_code = ?", msg.getWoId(),nextFlowTache.tache_code);
					if( null != execs && execs.size() > 0 ){
						for( MBpmWoTaskExec exec: execs ){
							//获取联系人电话
                            String mobile = WoTaskHelper.getWorkerMobile(exec.task_worker, exec.worker_type);
                            if(mobile!=null&&!mobile.isEmpty()) {
                            	 //获取短信内容
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("flow_name", bpmBoFlowDef.flow_name);
                                params.put("bo_title", flowInst.bo_title);
                                sms = SmsUtil.getSmsSysTemplate(IKeyValuesLocal.SMS_SYS_TEMPLATE_SCENE_CODE_00102, params);
                                if (StringUtils.isBlank(sms)) {
                                    sms = "您有新的任务单：" + bpmBoFlowDef.flow_name + "—【" + flowInst.bo_title + "】,请您及时处理。";
                                }

                                //发送
                                if (StringUtils.isNotBlank(mobile)) {
                                    SmsUtil.push(mobile, sms);
                                }
                            }
						}
					}
				}
			}
		}
	*/}

	public void setMsg(MsgBean msg) {
		this.msg = msg;
	}


}
