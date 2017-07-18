package com.ztesoft.crmpub.bpm.vo.spec;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.annotaion.SortField;
import appfrm.app.annotaion.SpecField;
import appfrm.app.annotaion.ValueField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = SBpmTemplateAttr.TABLE_CODE)
public class SBpmTemplateAttr extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(SBpmTemplateAttr.class);
    public static final String TABLE_CODE = "bpm_template_attr";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @RootField
    public String  template_id;
    
    @DBField @IDField
    public String  attr_id;
    
    @DBField
    public String  input_method;
    
    @DBField @ValueField
    public String  default_value;
    
    @DBField
    public String  value_from;
    
    @DBField
    public String  value_to;
    
    @DBField
    public String  table_name;
    
    @DBField @NameField
    public String  cname;
    
    @DBField @SpecField
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
    
    @DBField @SortField(orderBy="asc")
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
    
    public String  name;
    
    public String  value;
    
    public String show_field_attr;
    
}
