package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmBoChange.TABLE_CODE)
public class MBpmBoChange extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoChange.class);
    public static final String TABLE_CODE = "BPM_BO_CHANGE";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  chg_id;
    
    @DBField
    public String  bo_id;
    
    @DBField
    public String  action_type;
    
    @DBField
    public String  attr_id;
    
    @DBField
    public String  cname;
    
    @DBField
    public String  table_name;
    
    @DBField
    public String  field_name;
    
    @DBField
    public String  field_value;
    
    @DBField
    public String  old_field_value;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  handle_time;
    
    @DBField @RootField
    public String  flow_id;
}
