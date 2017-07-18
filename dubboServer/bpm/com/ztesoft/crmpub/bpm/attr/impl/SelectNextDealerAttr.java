package com.ztesoft.crmpub.bpm.attr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.AbstractAttrVal;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoDispatchRule;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;

/**
 * 选择下一环节执行人的模板字段
 * Author : joshui
 * Date ：2014-7-18
 */
public class SelectNextDealerAttr extends AbstractAttrVal {
	
    private String bo_type_id = null;
    private String tache_code = null;
    private String next_tache_code = null;
    private String next_tache_code_98M = null;
    
	@Override
	public void init(SBpmTemplateAttr attr, Map<String, String> param) {
        bo_type_id = StrTools.getStrValue(param, "bo_type_id");
        tache_code = StrTools.getStrValue(param, "tache_code");
        next_tache_code = StrTools.getStrValue(param, "next_tache_code");
        next_tache_code_98M = StrTools.getStrValue(param, "next_tache_code_98M");
        String curr_tache_code = BpmContext.getVar("curr_tache_code");
        
        if("T".equals(next_tache_code_98M)){
        	attr.input_method  = "98M";
        }
        
       
        if(StrTools.getStrValue(param, "curr_tache_code") != null){
        	curr_tache_code = StrTools.getStrValue(param, "curr_tache_code");
        }
        String json_params = attr.param;
		
		this.toJson(json_params);
		Map attrFlowParam = this.getAttrFlowParam(bo_type_id);
        if(attrFlowParam != null){
        	//弹出选择框各个环节能选择的org_level
        	Object obj = attrFlowParam.get("org_root_level");
        	if(obj != null && obj instanceof Map){
        		Map level = (Map) obj;
        		String org_root_level = (String) level.get(curr_tache_code);
        		
        		attr.show_field_attr = org_root_level;
        	}
        }
        
        if(StringUtil.isNotEmpty(bo_type_id) && StringUtil.isNotEmpty(tache_code) && StringUtil.isEmpty(next_tache_code)){
			SBpmBoFlowDef def = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
			MsgBean msg = this.prepareParams(param);
			def.setMsg(msg);
			
			try {
				SBpmBoFlowTache next = def.getNextTache(tache_code);
				if(next != null){
					next_tache_code = next.tache_code;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    @Override
    public List<SBpmAttrVal> fetch(SBpmTemplateAttr attr) {
        List<SBpmAttrVal> result = new ArrayList<SBpmAttrVal>();
        if(StringUtils.isNotEmpty(attr.value_content)){
            BpmContext.putVar("tache_code", tache_code);
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
           result = this.getAuditStaffs(attr, bo_type_id, next_tache_code);
        }

        return result;
    }
    
    public List<SBpmAttrVal> getAuditStaffs(SBpmTemplateAttr attr, String bo_type_id, String next_tache_code) {
    	List<SBpmAttrVal> result = new ArrayList<SBpmAttrVal>();
    	
    	String sql = "select a.staff_id as attr_value,a.staff_name attr_value_desc " +
		"from audit_staff a, staff b " +
		"where a.staff_id = b.staff_id " +
		"and b.status_cd = '00A'" +
		"and a.bo_type_id = ? " +
		"and a.tache_code = ? " +
		"and a.audit_type = ?" +
		" union " +
    	"select a.org_id as attr_value,a.org_name attr_value_desc " +
		"from audit_staff a, organization b " +
		"where a.org_id = b.party_id " +
		"and b.status_cd = '00A' " +
		"and a.bo_type_id = ? " +
		"and a.tache_code = ? " +
		"and a.audit_type = ?";
    	
    	String dest_worker_type = "STAFF";
    	//下一环节审批类型是员工、组织还是岗位
    	List<IVO> vos = MBpmWoDispatchRule.getDAO().query(" bo_type_id = ? and tache_code = ? and dest_method = 'ATTR'", 
    			bo_type_id, next_tache_code);
    	if(!ListUtil.isEmpty(vos)){
    		//如果有多个派单规则，是要统一staff或者org方式的
    		MBpmWoDispatchRule rule = (MBpmWoDispatchRule) vos.get(0);
    		dest_worker_type = rule.dest_worker_type;
    	}
    	
		List values = DAO.queryForMap(sql, bo_type_id, next_tache_code, dest_worker_type, bo_type_id, next_tache_code, dest_worker_type);
		
		for(int i=0; i < values.size(); i++){
			Map map = (Map) values.get(i);
			SBpmAttrVal val = new SBpmAttrVal();
			val.attr_id = attr.attr_id;
			val.template_id = attr.template_id;
			val.attr_value = StringUtil.getStrValue(map, "attr_value");
			val.attr_value_desc = StringUtil.getStrValue(map, "attr_value_desc");
			result.add(val);
		}
		
		return result;
	}
    
    @Override
    public List set(SBpmTemplateAttr attr, String value) {
        List<Map> attrList=(List)BpmContext.getListVar("attrList");
        List changes=new ArrayList();
        if(attrList!=null){
            for(Map attrMap:attrList ){
                String attrId=(String)attrMap.get("name");
                String attrValue=(String)attrMap.get("value");
                if(attrId.equals(attr.attr_id)){
                    String relAttrIds=(String)attrMap.get("cus_rel_attr_ids");
                    if(StrTools.isNotEmpty(relAttrIds)) {
                        String[] attrs=relAttrIds.split(",");
                        for(int i = 0;i<attrs.length;i++){
                            SBpmTemplateAttr orgAttr = (SBpmTemplateAttr) SBpmTemplateAttr.getDAO().findById(attrs[i]);
                            if (orgAttr != null) {
                                Map changeMap = new HashMap();
                                changeMap.put("vo", orgAttr);
                                orgAttr.value = attrValue;
                                changes.add(changeMap);
                            }
                        }
                    }
                    String clearAttrIds=(String)attrMap.get("cus_clear_attr_ids");
                    if(StrTools.isNotEmpty(clearAttrIds)) {
                        String[] attrs=clearAttrIds.split(",");
                        for(int i = 0;i<attrs.length;i++) {
                            SBpmTemplateAttr teamAttr = (SBpmTemplateAttr) SBpmTemplateAttr.getDAO().findById(attrs[i]);
                            if (teamAttr != null) {
                                Map changeMap = new HashMap();
                                changeMap.put("vo", teamAttr);
                                teamAttr.value = "";
                                changes.add(changeMap);
                            }
                        }
                    }
                }
            }
        }
        return changes;
    }
    
    private MsgBean prepareParams(Map param) {
		String flow_id = StringUtil.getStrValue(param, "flow_id");
		String wo_id = StringUtil.getStrValue(param, "wo_id");
		String bo_id = StringUtil.getStrValue(param, "bo_id");
		String bo_type_id = StringUtil.getStrValue(param, "bo_type_id");
		String tache_code = StringUtil.getStrValue(param, "tache_code");
		
		MsgBean msg = new MsgBean();
		msg.setBoTypeId(bo_type_id);
		msg.setBoId(bo_id);
		msg.setWoId(wo_id);
		msg.setFlowId(flow_id);
		msg.setWoId(wo_id);
		msg.setParam(param);
		
		BpmContext.putVar("flow_id", flow_id);
		BpmContext.putVar("wo_id", wo_id);
		BpmContext.putVar("bo_id", bo_id);
		BpmContext.putVar("bo_type_id", bo_type_id);
		BpmContext.putVar("tache_code", tache_code);
		
		return msg;
	}
}
