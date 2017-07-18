package com.ztesoft.crmpub.bpm.attr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.AbstractAttrVal;
import com.ztesoft.crmpub.bpm.attr.IAttrVal;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.util.AttrValFetchUtil;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

/**
 * 下一步骤处理人
 * @author Tong
 *
 */
public class NextStepAttr extends AbstractAttrVal {
	
    protected String bo_type_id = "";
    protected String tache_code = "";
    protected String curr_tache_code = "";
    protected String next_step_name = "";
    
	@Override
	public void init(SBpmTemplateAttr attr, Map<String, String> param) {
		bo_type_id = StrTools.getStrValue(param, "bo_type_id");
		tache_code = StrTools.getStrValue(param, "tache_code");
		next_step_name = StrTools.getStrValue(param, "value_name");
		curr_tache_code = StrTools.getStrValue(param, "curr_tache_code");
		
		String json_params = attr.param;
		
		this.toJson(json_params);
	}

    @Override
    public List<SBpmAttrVal> fetch(SBpmTemplateAttr attr) {
        List<SBpmAttrVal> result = new ArrayList<SBpmAttrVal>();
        if(StrTools.isEmpty(tache_code)){
            tache_code= BpmContext.getVar("tache_code");
        }
        if(StringUtils.isNotEmpty(attr.value_content)){
            Object[] results = SqlValUtil.format(attr.value_content);
            if( null != results ) {
                List<String> params = (List<String>) results[1];
                List<Map> resultDatas = DAO.queryForMap((String) results[0], (String[]) params.toArray(new String[]{}));
                if (!ListUtil.isEmpty(resultDatas)) {
                    for (Map map : resultDatas) {
                        SBpmAttrVal val = new SBpmAttrVal();
                        val.attr_id = attr.attr_id;
                        val.template_id = attr.template_id;
                        val.attr_value = (String) map.get("attr_id");
                        val.attr_value_desc = (String) map.get("attr_desc");
                        map.remove("attr_id");
                        map.remove("attr_desc");
                        val.cus_attr=map;
                        result.add(val);
                    }
                }
            }
        }else {
            String action = BpmContext.getVar("action");
            List<IVO> vos = SBpmBoFlowTache.getDAO().query("bo_type_id = ? and " +
                            "	tache_code in (select a.tar_tache_code " +
                            "  from bpm_tache_route a " +
                            "  where a.src_tache_code =? " +
                            "  and a.bo_type_id = ? and a.action_type=? and a.state='00A') order by  seq_no  ",
                    bo_type_id, tache_code, bo_type_id,action);
            if (!ListUtil.isEmpty(vos)) {//配置了路由表的情况就根据路由表的配置走
                for (IVO vo : vos) {
                    SBpmAttrVal val = new SBpmAttrVal();
                    val.attr_id = attr.attr_id;
                    val.template_id = attr.template_id;
                    val.attr_value = ((SBpmBoFlowTache) vo).tache_code;
                    if(tache_code.equals(val.attr_value)){//tache_code相同的情况就是内部转派
                        val.attr_value_desc ="内部转派";
                    }else{
                        val.attr_value_desc = ((SBpmBoFlowTache) vo).tache_name;
                    }
                    if(MsgConsts.ACTION_WO_FAIL.equals(action)){
                        val.attr_value_desc="回退--"+val.attr_value_desc;
                    }
                    result.add(val);
                }
            }
            else {//没有配置路由表
            	if(MsgConsts.ACTION_WO_FAIL.equals(action)){//流程审批不通过的时候，返回上一个环节，或者第一个环节
            		SBpmBoFlowDef bpmBoFlowDef =  (SBpmBoFlowDef)SBpmBoFlowDef.getDAO().findById(bo_type_id);
            		try {
            			SBpmBoFlowTache beginTache = bpmBoFlowDef.getBeginTahce();//第一个环节
            			SBpmAttrVal val = new SBpmAttrVal();
                        val.attr_id = attr.attr_id;
                        val.template_id = attr.template_id;
                        val.attr_value = beginTache.tache_code;
                        val.attr_value_desc = beginTache.tache_name;
                        result.add(val);
                        
                        SBpmBoFlowTache prevTache = bpmBoFlowDef.getPrevTacheByInst(tache_code, false);//上一个环节
            			SBpmAttrVal val2 = new SBpmAttrVal();
                        val2.attr_id = attr.attr_id;
                        val2.template_id = attr.template_id;
                        val2.attr_value = prevTache.tache_code;
                        val2.attr_value_desc = prevTache.tache_name;
                        result.add(val2);
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            	else {//流程审批通过的时候，走到下一个环节
            		SBpmBoFlowDef bpmBoFlowDef =  (SBpmBoFlowDef)SBpmBoFlowDef.getDAO().findById(bo_type_id);
            		try {
            			SBpmBoFlowTache nextFlowTache = bpmBoFlowDef.getNextTache(tache_code);
            			SBpmAttrVal val = new SBpmAttrVal();
                        val.attr_id = attr.attr_id;
                        val.template_id = attr.template_id;
                        val.attr_value = nextFlowTache.tache_code;
                        val.attr_value_desc = nextFlowTache.tache_name;
                        result.add(val);
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            }
        }

        return result;
    }

    @Override
    public List set(SBpmTemplateAttr attr, String value) {
        List<IVO> relas = (List<IVO>) SBpmTemplateAttr.getDAO().query(" template_id = ? and field_name = ?",
                new String[]{attr.template_id, "next_auditor"});
        if (relas == null || relas.size() == 0){
            return null;
        }
        
        Map attrFlowParam = this.getAttrFlowParam(bo_type_id);
        
        SBpmTemplateAttr rela = (SBpmTemplateAttr) relas.get(0);
        IAttrVal attrVal = AttrValFetchUtil.getAttrInstance(rela);
        
        Map param = new HashMap();
        param.put("bo_type_id", bo_type_id);
        param.put("tache_code", value);
        param.put("curr_tache_code", curr_tache_code);
        param.put("next_tache_code", value);//当前属性就是下一步骤
        param.put("next_tache_code_98M", "F");
        BpmContext.putVar("next_tache_code", value);
        BpmContext.putVar("next_step_name", next_step_name);
        
        if(attrFlowParam != null){
        	Object obj = attrFlowParam.get("next_tache_code_98M");
        	if(obj != null && obj instanceof List){
        		List codes = (List) obj;
        		//包含下一步骤
        		if(codes.contains(value)){
        			param.put("next_tache_code_98M", "T");
        		}
        	}
        }
        
        attrVal.init(rela, param);
        List<SBpmAttrVal> newList = attrVal.fetch(rela);
        Map changeMap = new HashMap();
        changeMap.put("vo", rela);
        changeMap.put("voVals", newList);
        List changes = new ArrayList();
        changes.add(changeMap);
        return changes;
    }
}
