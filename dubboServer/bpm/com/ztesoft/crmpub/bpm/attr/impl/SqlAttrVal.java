package com.ztesoft.crmpub.bpm.attr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.AbstractAttrVal;
import com.ztesoft.crmpub.bpm.attr.IAttrVal;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;

public class SqlAttrVal extends AbstractAttrVal {
	
	private static SqlAttrVal fetch = new SqlAttrVal();

	public List<SBpmAttrVal> fetch(SBpmTemplateAttr attr) {
		
		Object[] results = SqlValUtil.format(attr.value_content);
		if( null != results ){
			List<String> params = (List<String>)results[1];
			
			List<Map> resultDatas = DAO.queryForMap((String)results[0], (String[])params.toArray(new String []{}));
			
			if( !ListUtil.isEmpty(resultDatas) ){
				
				List<SBpmAttrVal> valList = new ArrayList<SBpmAttrVal>();
				
				for( Map<String, String> map: resultDatas ){
					SBpmAttrVal val = new SBpmAttrVal();
					valList.add(val);
					
					val.attr_id = attr.attr_id;
					val.template_id = attr.template_id;
					val.attr_value = (String)map.get("attr_id");
					val.attr_value_desc = (String)map.get("attr_desc");
					
					if(attr.input_method.equals("98C")){
						attr.value = val.attr_value_desc;
						break;
					}
					
					for(Entry<String, String> e: map.entrySet()){
			            if (e.getKey() != null && e.getKey().startsWith("cus_")) {
			            	val.getCus_attr().put(e.getKey(), e.getValue());
						}
			        }
				}
				return valList;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	public static IAttrVal getInstance() {
		return fetch;
	}

	public static void main(String[] args) {
		
		BpmContext.putVar("STAFF_ID", "admin");
		BpmContext.putVar("CITY_ID", "200");
		
		SBpmTemplateAttr attr = new SBpmTemplateAttr();
		attr.value_method = "SQL";
		attr.value_content = " select staff_id as id, staff_name as name from staff where staff_id=${STAFF_ID} ";
		
		fetch.fetch(attr);
		
	}

	@Override
	public List set(SBpmTemplateAttr attr, String value) {
		return null;
	}

	@Override
	public String validate(SBpmTemplateAttr attr, String value) {
		return null;
	}
}
