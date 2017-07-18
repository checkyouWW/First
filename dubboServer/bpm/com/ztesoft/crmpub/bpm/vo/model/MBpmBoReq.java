package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmBoReq.TABLE_CODE)
public class MBpmBoReq extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoReq.class);
    public static final String TABLE_CODE = "BPM_BO_REQ";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    @DBField @IDField @RootField
    public String  bo_id;
    
    @DBField @NameField
    public String  bo_name;
    
    @DBField
    public String  bo_title;
    
    @DBField
    public String  bo_type_id;
    
    @DBField
    public String  bo_state;
    
    @DBField
    public String  bo_state_name;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  state_date;
    
    @DBField
    public String  create_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  create_date;
    
    @DBField
    public String  parent_bo_id;
    
    @DBField
    public String  parent_bo_type_id;
    
    @DBField
    public String  col1_name;
    
    @DBField
    public String  col1;
    
    @DBField
    public String  col2_name;
    
    @DBField
    public String  col2;
    
    @DBField
    public String  col3_name;
    
    @DBField
    public String  col3;
    
    @DBField
    public String  col4_name;
    
    @DBField
    public String  col4;
    
    @DBField
    public String  col5_name;
    
    @DBField
    public String  col5;
    
    @DBField
    public String  col6_name;
    
    @DBField
    public String  col6;
    
    @DBField
    public String  col7_name;
    
    @DBField
    public String  col7;
    
    @DBField
    public String  col8_name;
    
    @DBField
    public String  col8;
    
    @DBField
    public String  col9_name;
    
    @DBField
    public String  col9;
    
    @DBField
    public String  col10_name;
    
    @DBField
    public String  col10;
    
    @DBField
    public String  col11_name;
    
    @DBField
    public String  col11;
    
    @DBField
    public String  col12_name;
    
    @DBField
    public String  col12;
    
    @DBField
    public String  col13_name;
    
    @DBField
    public String  col13;
    
    @DBField
    public String  col14_name;
    
    @DBField
    public String  col14;
    
    @DBField
    public String  col15_name;
    
    @DBField
    public String  col15;
}
