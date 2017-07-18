package com.ztesoft.crmpub.bpm.attr.impl;

import java.util.List;
import java.util.Map;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.attr.AbstractAttrVal;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

/**
 * 审批内容属性类
 * @author Tong
 *
 */
public class RespContentAttr extends AbstractAttrVal {
	
	@Override
	public void init(SBpmTemplateAttr attr, Map<String, String> param) {
		String action = StrTools.getStrValue(param, "action");
		String normalWith = StrTools.getStrValue(param, "normalWith");
		if("1".equals(normalWith) && MsgConsts.ACTION_WO_WITHDRAW.equals(action)){
			action = MsgConsts.ACTION_WO_FINISH;
		}
		if(StrTools.isEmpty(action)){
			action = MsgConsts.ACTION_WO_FINISH;
		}
		String work_type = this.getTacheWorkType(param);
		attr.name = "flow_"+attr.attr_id;
		if(MsgConsts.ACTION_WO_FINISH.equalsIgnoreCase(action)){
			if(BPMConsts.WORK_TYPE_AUDIT.equals(work_type)){
				attr.value = "同意";
			}
			else {
				attr.value = "处理完成";
			}
		}
		else if(MsgConsts.ACTION_WO_FAIL.equalsIgnoreCase(action)){
			if(BPMConsts.WORK_TYPE_AUDIT.equals(work_type)){
				attr.value = "不同意";
			}
		}
	}
	
	private String getTacheWorkType(Map<String, String> param){
		String bo_type_id = StrTools.getStrValue(param, "bo_type_id");
		String tache_code = StrTools.getStrValue(param, "tache_code");
		List<IVO> vos = SBpmBoFlowTache.getDAO().query("bo_type_id = ? and tache_code = ?", bo_type_id, tache_code);
		if(!ListUtil.isEmpty(vos)){
			SBpmBoFlowTache tache = (SBpmBoFlowTache) vos.get(0);
			return tache.work_type;
		}
		return null;
	}
}
