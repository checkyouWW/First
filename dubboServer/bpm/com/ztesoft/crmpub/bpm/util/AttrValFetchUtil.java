package com.ztesoft.crmpub.bpm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.util.StrUtil;

import com.ztesoft.crmpub.bpm.attr.IAttrVal;
import com.ztesoft.crmpub.bpm.attr.impl.SqlAttrVal;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

@SuppressWarnings("unchecked")
public class AttrValFetchUtil {

	private static Map<String,IAttrVal> attrValMap = new HashMap<String,IAttrVal>();
	
	static {
		attrValMap.put(BPMConsts.ATTR_VAL_FETCH_TYPE.SQL, SqlAttrVal.getInstance());
	}
	
	public static IAttrVal getAttrInstance( SBpmTemplateAttr attr ){
		String value_method = attr.value_method;
		if(StrUtil.isNotEmpty(value_method)){
			IAttrVal attrVal = attrValMap.get(value_method);
			if( null != attrVal ){
				return attrVal;
			}
			if(BPMConsts.ATTR_VAL_FETCH_TYPE.CLASS.equals(value_method)){
				IAttrVal valAttr = getAttrInstanceByClass(attr);
				return valAttr;
			}
		}
		return null;
	}
	
	public static IAttrVal getAttrInstanceByClass( SBpmTemplateAttr attr ){
		String attr_class = attr.attr_class;
		if(StrUtil.isNotEmpty(attr_class)){
			Class<? extends IAttrVal> attrClass;
			try {
				ClassLoader globeLoader = Thread.currentThread().getContextClassLoader();
				attrClass = (Class<? extends IAttrVal>) globeLoader.loadClass(attr_class);
				IAttrVal valAttr = (IAttrVal) attrClass.newInstance();
				return valAttr;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static List<SBpmAttrVal> fetch( SBpmTemplateAttr attr,Map<String, String> params ){
		IAttrVal attrVal = getAttrInstance(attr);
		if( null != attrVal ){
			attrVal.init(attr, params);
			return attrVal.fetch(attr);
		}
		
		return null;
	}
	
	
}
