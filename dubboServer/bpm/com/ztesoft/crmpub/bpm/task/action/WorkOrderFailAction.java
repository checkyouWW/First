/**
 * 
 */
package com.ztesoft.crmpub.bpm.task.action;

import java.util.List;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.util.WoTaskHelper;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;
import appfrm.resource.dao.impl.DaoUtils;

/**
 * @author major
 *
 */
public class WorkOrderFailAction extends WorkOrderBaseAction implements IWorkOrderAction{
	public boolean execute() throws Exception {
		MBpmWoTask woTask = getWoTask();

		// 获取当前流程实例
		MBpmBoFlowInst flowInst = msg.getFlowInst();
		if (flowInst == null) {
			flowInst = (MBpmBoFlowInst) MBpmBoFlowInst.getDAO().findById(woTask.flow_id);
			msg.setFlowInst(flowInst);
		}

		// 对当前环节回单
		String respContent = msg.getRespContent();
		if (StrUtil.isEmpty(respContent)){
			respContent = "审核未通过，需重新提交!";
		}
		woTask.set("wo_state", BPMConsts.WO_STATE_FAIL);
		woTask.set("resp_content", msg.getRespContent());
		woTask.set("resp_oper_id", msg.getOperId() );
		woTask.set("resp_result",MsgConsts.BPM_SUCCESS );
		woTask.set("state_date",DaoUtils.getDBCurrentTime() );
		String[] fields = woTask.updateFieldSet.toArray(new String[]{});
		MBpmWoTask.getDAO().updateParmamFieldsByIdSQL(fields).update(woTask);
		msg.setBoTitle(getBoTitleFromWO(woTask));
		
		String wo_id = woTask.wo_id;
		List<MBpmWoTaskExec> allExecs = getWoTaskExecs(wo_id);
		// 当前环节执行人更新
		List<MBpmWoTaskExec> currExe = this.getCurrWoTaskExec(allExecs, msg.getOperId());
		this.dealWoTaskExec(currExe, BPMConsts.EXEC_STATE_FAIL);//当前执行人不同意
		//那么把其他人的执行人记录为“未完成”
		List<MBpmWoTaskExec> otherExecs = this.getOtherWoTaskExec(allExecs, msg.getOperId());//同一环节的其他执行人
		this.dealWoTaskExec(otherExecs, BPMConsts.EXEC_STATE_UNFINISHED);
		
		String wo_fail_type = msg.getBackToFirst();
		String back_to_tachecode = msg.getBackToTacheCode();
		boolean back_to_first = StrUtil.isEmpty(wo_fail_type) ? true : (
					//第一个环节							//上一个环节
					"T".equals(wo_fail_type) ? true : false
				);//默认打回第一个环节
		
		
		// 获取前一环节、当前环节
		SBpmBoFlowDef flowDef = flowInst.getFlowDef();
		flowDef.setMsg(msg);
		SBpmBoFlowTache prevFlowTache = null;
		if(StrUtil.isEmpty(back_to_tachecode)){
			prevFlowTache = flowDef.getPrevTacheByInst(woTask.tache_code, back_to_first);
		}else{
			prevFlowTache = flowDef.getTacheByCode(back_to_tachecode);//前台指定了回退的环节
		}
		
		SBpmBoFlowTache currFlowTache = flowDef.getTacheByCode(woTask.tache_code);
		msg.setFlowDef(flowDef);
		msg.setFlowTache(currFlowTache);
		
		String prev_tache_code = prevFlowTache.tache_code;
		// 更新流程信息
		if( flowInst.getFlowDef().isFirstTache(prev_tache_code) ){
			flowInst.set("bo_state","FAIL");
			flowInst.set("bo_state_name", "审核未通过，需重新提交");
		}else{
			SBpmBoFlowTache prevPrevFlowTache = flowDef.getPrevTacheByInst(prev_tache_code, false);
			flowInst.set("bo_state_name", prevPrevFlowTache.bo_state_name); // 没看懂这个地方 joshui
		}
		flowInst.set("state_date", DaoUtils.getDBCurrentTime());
		fields = flowInst.updateFieldSet.toArray(new String[]{});
		MBpmBoFlowInst.getDAO().updateParmamFieldsByIdSQL(fields).update(flowInst);
		
		// 更新其他工单信息
		if( flowInst.getFlowDef().isFirstTache(prev_tache_code) ){
			/**
			 * liu.risheng
			 * 当流程打回到第一个环节后，需要将bpm_wo_task表的wo_state改成FAIL的(除第一个流程外)
			 */
			String sql = "update bpm_wo_task a set a.wo_state=? where a.flow_id=? and a.tache_code<>? ";
			DAO.update(sql, new String[]{BPMConsts.WO_STATE_FAIL,flowInst.flow_id,prev_tache_code});
		}
		
		// 生成下一环节工单和工单处理人
		//如果上一个环节的话，直接可以根据上一个实例来取出处理人，再打回给他
		MBpmWoTask prevInst = flowDef.getPrevWoTaskByInst(woTask.tache_code, prev_tache_code);
		List<IVO> prev_vos = MBpmWoTaskExec.getDAO().query(" wo_id = ? and task_worker = ?", prevInst.wo_id, prevInst.resp_oper_id);
		if(prev_vos != null && prev_vos.size() > 0){
			MBpmWoTaskExec prev_exe = (MBpmWoTaskExec) prev_vos.get(0);
			msg.setWorkerType(prev_exe.worker_type);
		}
		msg.setTaskWorker(prevInst.resp_oper_id);//上一个处理人作为当前处理人
		WoTaskHelper.genWorkerOrderByOper(flowInst, prevFlowTache, msg);
		//WoTaskHelper.genWorkerOrderAndDispatch(flowInst, prevFlowTache, msg);
		// 调用环节事件
		boolean done = true;
		SBpmWoType woType = this.getWoType();
		if( null != woType && StrUtil.isNotEmpty(woType.fail_event_handler) ){
			Class specClass = Class.forName(woType.fail_event_handler);
			Object handlerObj = specClass.newInstance();
			if ( handlerObj  instanceof ICallBackHandler ) {
				ICallBackHandler handler = (ICallBackHandler)handlerObj;
				done = handler.processMsg(msg);
			}else{
				throw new RuntimeException("工单定义【"+woType.wo_type_id+"】的失败处理器类型不正确！需要实现接口【ICallBackHandler】！") ;
			}
		}
		
		// 流程失败暂时不需要发送短信
		// 发送短信
		
		/*if( done ){
			//sendSms(flowInst, prevFlowTache);
			writeInfLog(msg);
		}*/
		
		return true;
	}

	private void sendSms(MBpmBoFlowInst flowInst, SBpmBoFlowTache prevFlowTache) {/*
		String sms = "";
		SBpmBoFlowDef bpmBoFlowDef = flowInst.getFlowDef();
		if( flowInst.getFlowDef().isFirstTache(prevFlowTache.tache_code) ){
			List<SBpmWoType> woTypes = (List)SBpmWoType.getDAO().query(" bo_type_id = ? and tache_code = ?", bpmBoFlowDef.bo_type_id,prevFlowTache.tache_code);
			if( null!=woTypes && woTypes.size()>0 ){
				if( "1".equals(woTypes.get(0).need_sms) ){
					List<MBpmWoTaskExec> execs = (List)MBpmWoTaskExec.getDAO().query(" flow_id = ? and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'", flowInst.flow_id);
					if( null != execs && execs.size() > 0 ){
						MBpmWoTaskExec exec = execs.get(0);
						
						//获取联系人电话
						String mobile = WoTaskHelper.getWorkerMobile(exec.task_worker, exec.worker_type);
						if(mobile!=null&&!mobile.isEmpty()){
							 Map<String, String> params = new HashMap<String, String>();
                             params.put("flow_name", bpmBoFlowDef.flow_name);
                             params.put("bo_title", flowInst.bo_title);
                             sms = SmsUtil.getSmsSysTemplate(IKeyValuesLocal.SMS_SYS_TEMPLATE_SCENE_CODE_00103, params);
                             if (StringUtils.isBlank(sms)) {
                                 sms = "您的流程：" + bpmBoFlowDef.flow_name + "—【" + flowInst.bo_title + "】流程被打回,请及时查看处理。";
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
			List<MBpmWoTask> woTasks = (List)MBpmWoTask.getDAO().query(" flow_id=? and tache_code=? and wo_state=?", flowInst.flow_id,prevFlowTache.tache_code,BPMConsts.WO_STATE_READY);
			if( null!=woTasks && woTasks.size() > 0 ){
				MBpmWoTask task = woTasks.get(0);
				List<SBpmWoType> woTypes = (List)SBpmWoType.getDAO().query(" bo_type_id = ? and tache_code = ?", bpmBoFlowDef.bo_type_id,prevFlowTache.tache_code);
				if( null!=woTypes && woTypes.size()>0 ){
					if( "1".equals(woTypes.get(0).need_sms) ){
						
						List<MBpmWoTaskExec> execs = (List)MBpmWoTaskExec.getDAO().query(" wo_id = ? and tache_code = ?", task.wo_id,prevFlowTache.tache_code);
						if( null != execs && execs.size() > 0 ){
							for( MBpmWoTaskExec exec: execs ){
								//获取联系人电话
                                String mobile = WoTaskHelper.getWorkerMobile(exec.task_worker, exec.worker_type);
                                if(mobile!=null&&!mobile.isEmpty()) {
                                	//获取短信内容
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("flow_name", bpmBoFlowDef.flow_name);
                                    params.put("bo_title", flowInst.bo_title);
                                    sms = SmsUtil.getSmsSysTemplate(IKeyValuesLocal.SMS_SYS_TEMPLATE_SCENE_CODE_00104, params);
                                    if (StringUtils.isBlank(sms)) {
                                        sms = "您有新的任务单：" + bpmBoFlowDef.flow_name + "—【" + flowInst.bo_title + "】被打回,请您及时处理。";
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
		}
	*/}
	
	
	
}
