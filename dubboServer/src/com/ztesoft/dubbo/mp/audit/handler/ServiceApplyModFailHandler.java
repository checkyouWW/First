package com.ztesoft.dubbo.mp.audit.handler;

import org.apache.commons.lang.StringUtils;

import appfrm.resource.dao.impl.DAO;

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
public class ServiceApplyModFailHandler implements ICallBackHandler {
	
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
			boolean isChanged = false;
			if(MsgConsts.ACTION_WO_WITHDRAW.equals(action)){
				if(msg.getBoTypeId().equals(KeyValues.BO_TYPE_ID_SERVICE_APPLY)) {//申请服务流程才对实例状态修改
					apply.set("state", KeyValues.APPLY_STATE_WITHDRAW);
				}else {
					//撤销流程，对审批通过的流程进行撤销，当取消当前撤销流程时，及恢复到之前该申请单通过状态
					apply.set("state", KeyValues.APPLY_STATE_SUCCESS);
				}
			}else {
				apply.set("state", KeyValues.APPLY_STATE_SUCCESS);
			}
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = (String[]) apply.updateFieldSet.toArray(new String[] {});
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		deleteTmpData(msg.getFlowId());
		return true;
	}
	
	public void deleteTmpData(String flow_id) {
		String sql = "delete from data_change_notify where flow_id = ? ";
		String sql1 = "delete from data_change_notify_column where flow_id = ? ";
		DAO.update(sql, new String[]{flow_id});
		DAO.update(sql1, new String[]{flow_id});
	}
	
}
