package com.ztesoft.crmpub.bpm.vo.spec;

import java.util.HashMap;
import java.util.Map;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = SBpmAttrVal.TABLE_CODE)
public class SBpmAttrVal extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(SBpmAttrVal.class);
    public static final String TABLE_CODE = "bpm_attr_val";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  attr_value_id;
    
    @DBField 
    public String  attr_id;
    
    @DBField @RootField
    public String  template_id;
    
    @DBField @NameField
    public String  attr_value;
    
    @DBField
    public String  attr_value_desc;
    
    @DBField
    public String  seq_no;
    
    // 加上自定义属性，自动属性的名称都以“cus_”前缀，倒三角流程-徐兆民前台需要用到 joshui
    public Map<String, String> cus_attr = new HashMap<String, String>();

	public Map<String, String> getCus_attr() {
		return cus_attr;
	}
}
