package com.ztesoft.crmpub.bpm.attr;

import java.util.List;
import java.util.Map;

import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;

public interface IAttrVal {

	public List<SBpmAttrVal> fetch( SBpmTemplateAttr attr );
	
	public List set(SBpmTemplateAttr attr, String value);
	
	public String validate(SBpmTemplateAttr attr, String value);
	
	/**
	 * 模板字段的初始化方法。
	 * 可以设置模板的可见性、可编辑性等属性。
	 * @param attr
	 * @param param
	 * Author : joshui
	 * Date ：2014-7-18
	 */
	public void init(SBpmTemplateAttr attr, Map<String, String> param);
}
