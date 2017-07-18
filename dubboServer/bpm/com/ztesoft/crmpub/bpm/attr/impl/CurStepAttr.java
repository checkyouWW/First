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
public class CurStepAttr extends AbstractAttrVal {
	
	@Override
	public void init(SBpmTemplateAttr attr, Map<String, String> param) {
		
		String bo_type_id = StrTools.getStrValue(param, "bo_type_id");
		String tache_code = StrTools.getStrValue(param, "tache_code");
		String tache_name = StrTools.getStrValue(param, "tache_name");
		if( StrTools.isNotEmpty(tache_name)){
			attr.value = tache_name;
			return;
		}
		
		List<IVO> vos = SBpmBoFlowTache.getDAO().query("bo_type_id = ? and tache_code = ?", bo_type_id, tache_code);
		if(!ListUtil.isEmpty(vos)){
			SBpmBoFlowTache tache = (SBpmBoFlowTache) vos.get(0);
			attr.value = tache.tache_name;
		}
	}
}
