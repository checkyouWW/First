package com.ztesoft.dubbo.mp.audit.handler;

import org.apache.commons.lang.StringUtils;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;

/**
 * 服务申请单审批不通过处理事件
 */
public class ServiceApplyFailHandler implements ICallBackHandler {
	
	@Override
	public boolean processMsg(MsgBean msg) throws Exception {
		SBpmBoFlowDef bpmBoFlowDef = msg.getFlowDef();
		SBpmBoFlowTache currTache = msg.getFlowTache();
		String action = msg.getMsgAction();
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
		
		SServiceApply apply = (SServiceApply) SServiceApply.getDAO().findById(bo_id);
		if(apply != null){
			apply.set("state", KeyValues.APPLY_STATE_FAIL);
			if(MsgConsts.ACTION_WO_WITHDRAW.equals(action)){
				apply.set("state", KeyValues.APPLY_STATE_WITHDRAW);
			}
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = (String[]) apply.updateFieldSet.toArray(new String[] {});
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		ServiceApplyModFailHandler handler = new ServiceApplyModFailHandler();
		//删除临时数据
		handler.deleteTmpData(msg.getFlowId());
		return true;
	}
	
	
}
