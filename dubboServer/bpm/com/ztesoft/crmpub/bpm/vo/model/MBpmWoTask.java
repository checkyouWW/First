package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.annotaion.SortField;
import appfrm.app.annotaion.SpecField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmWoTask.TABLE_CODE)
public class MBpmWoTask extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmWoTask.class);
    public static final String TABLE_CODE = "BPM_WO_TASK";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField 
    public String  wo_id;
    
    @DBField @NameField
    public String  wo_type_id;
    
    @DBField @SpecField 
    public String  wo_state;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  state_date;
    
    @DBField
    public String  work_type;
    
    @DBField
    public String  task_type;
    
    @DBField
    public String  task_title;
    
    @DBField
    public String  task_content;
    
    @DBField
    public String  tache_code;
    
    @DBField
    public String  tache_name;
    
    @DBField
    public String  bo_id;
    
    @DBField
    public String  bo_type_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  exec_date;
    
    @DBField
    public String  create_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)  @SortField(orderBy="asc")
    public String  create_date;
    
    @DBField
    public String  dispatch_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  dispatch_date;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  limit_date;
    
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
    
    @DBField @RootField
    public String  flow_id;
    
    @DBField
    public String  resp_oper_id;
    
    @DBField
    public String  resp_result;
    
    @DBField
    public String  resp_content;
    
    @DBField
    public String  dispatch_tache_code;
}
