package com.ztesoft.crmpub.bpm.attr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;
import com.ztesoft.inf.util.JsonUtil;

/**
 * 新增抽象类。
 * Author : joshui
 * Date ：2014-7-18
 */
public abstract class AbstractAttrVal implements IAttrVal  {
	protected Map param = new HashMap();
	
	public List<SBpmAttrVal> fetch(SBpmTemplateAttr attr) {
		return null;
	}
	
	public List set(SBpmTemplateAttr attr, String value) {
		return null;
	}
	
	public String validate(SBpmTemplateAttr attr, String value) {
		return null;
	}
	
	public void init(SBpmTemplateAttr attr, Map<String, String> param) {
	}
	
	public void toJson(String json){
		if(StringUtil.isEmpty(json)){
			return;
		}
		try {
			TypeToken<Map> token = new TypeToken<Map>() {};
			param = JsonUtil.fromJson(json, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public Map getAttrFlowParam(String bo_type_id){
    	try {
			if(null != param && !param.isEmpty()){
				Map flowAttrParam = (Map) param.get(bo_type_id);
				return flowAttrParam;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
}
