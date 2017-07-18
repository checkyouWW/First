package com.ztesoft.dubbo.mp.audit.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import spring.util.SpringContextUtil;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.dubbo.sp.data.vo.MDataChangeNotify;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;

/**
 * 服务申请单审批通过处理事件
 */
public class ServiceApplyCancelFinishHandler implements ICallBackHandler {
	
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
		boolean flag = true;
		if(apply != null){
			BpmService bpmService = (BpmService) SpringContextUtil.getBean("bpmService");
			//获取该申请单对应的流程，撤销 ,bpm_bo_flow_inst表中只有bo_state="ACTICE" and bo_stata="FAIL"
			String sql = "select * from bpm_bo_flow_inst where bo_id = ? and bo_state = ? ";
			List<Map> flowInstList = DAO.queryForMap(sql, new String[]{bo_id,KeyValues.BO_STATE_ACTIVE});
			if(flowInstList != null && !ListUtil.isEmpty(flowInstList)) {
				for(Map inst : flowInstList) {
			        List attrList = new ArrayList();
			        Map p = new HashMap();
			        p.put("attrList", attrList);
			        p.put("action", MsgConsts.ACTION_WO_WITHDRAW);
			        p.put("resp_content", "取消申请");
			        p.put("bo_id", bo_id);

			        Map re = bpmService.audit(p);
			        if(re!=null) {
			        	if(Const.getStrValue(re, "resCode").equals("0")) {
			        		flag = true;
			        	}else {
			        		flag = false;
			        	}
			        }

				}
			}
			
			if(!flag) {return flag;}
			
			apply.set("state", KeyValues.APPLY_STATE_WITHDRAW);
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = (String[]) apply.updateFieldSet.toArray(new String[] {});
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		return flag;
	}
	
}
