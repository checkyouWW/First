package com.ztesoft.dubbo.mp.audit.handler;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.dubbo.sp.data.vo.SServiceInst;
import com.ztesoft.inf.util.KeyValues;

/**
 * 服务申请单审批通过处理事件
 */
public class ServiceApplyFinishHandler implements ICallBackHandler{
	
	@Override
	public boolean processMsg(MsgBean msg) throws Exception {
		SBpmBoFlowDef bpmBoFlowDef = msg.getFlowDef();
		SBpmBoFlowTache currTache = msg.getFlowTache();
		
		if(bpmBoFlowDef != null && currTache != null){
			String curr_tache_code = currTache.tache_code;
			bpmBoFlowDef.setMsg(msg);
			//获取下一个流程环节			
			SBpmBoFlowTache nextFlowTache = bpmBoFlowDef.getNextTache(curr_tache_code);
			//下一环节不为空，或不被跳过证明还没结束
			if(nextFlowTache != null){
				return true;
			}
		}
		
		String bo_id = msg.getBoId();
		
		if(StringUtils.isBlank(bo_id)){
			return false;
		}

		//更新申请单
		SServiceApply apply = (SServiceApply) SServiceApply.getDAO().findById(bo_id);
		if(apply != null){
			String service_type = apply.service_type;
			String flow_id = msg.getFlowId();
			ServiceApplyModFinishHandler hand = new ServiceApplyModFinishHandler();
			if(service_type.equals(KeyValues.SERVICE_TYPE_DATA)) {
				hand.synDataApplyInfo(bo_id,flow_id);
			}else if(service_type.equals(KeyValues.SERVICE_TYPE_TASK)) {
				hand.synTaskApplyInfo(bo_id,flow_id);
			}else if(service_type.equals(KeyValues.SERVICE_TYPE_SECURITY)) {
				
			}
			apply.set("state", KeyValues.APPLY_STATE_SUCCESS);
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = (String[]) apply.updateFieldSet.toArray(new String[] {});
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		
		//生成服务实例
		String inst_id = SeqUtil.getSeq(SServiceInst.TABLE_CODE, "inst_id");
		
		SServiceInst inst = new SServiceInst();
		Map map = apply.saveToMap();
		inst.readFromMap(map);
		inst.inst_id = inst_id;
		SServiceInst.getDAO().insert(inst);
		
		return true;
	}
}
