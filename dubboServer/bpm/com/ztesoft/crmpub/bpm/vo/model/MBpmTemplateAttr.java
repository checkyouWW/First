package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmTemplateAttr.TABLE_CODE)
public class MBpmTemplateAttr  extends VO implements IVO{
    public static final IVOMeta META = IVOMeta.getInstance(MBpmTemplateAttr.class);
    public static final String TABLE_CODE = "bpm_template_attr";
    
    public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	
	@DBField @IDField @RootField
    public String  attr_id;
	
	@DBField
	public String  template_id;
	
	@DBField
	public String  input_method;
	
	@DBField
	public String  default_value;
	
	@DBField
	public String  value_from;
	
	@DBField
	public String  value_to;
	
	@DBField
	public String  table_name;
	
	@DBField
	public String  cname;
	
	@DBField
	public String  field_name;
	
	@DBField
	public String  is_editable;
	
	@DBField
	public String  is_nullable;
	
	@DBField
	public String  is_visible;
	
	
	@DBField
	public String  attr_length;
	
	
	@DBField
	public String  colspan;
	
	
	@DBField
	public String  order_id;
	
	@DBField
	public String  check_message;
	
	@DBField
	public String  attr_format;
	
	@DBField
	public String  attr_desc;
	
	@DBField
	public String  value_method;
	
	@DBField
	public String  value_content;
	
	@DBField
	public String  attr_class;
	
	@DBField
    public String  param;
}
