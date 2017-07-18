package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmWoCC.TABLE_CODE)
public class MBpmWoCC extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmWoCC.class);
    public static final String TABLE_CODE = "BPM_WO_CC";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  cc_wo_id;
    
    @DBField
    public String  wo_id;
    
    @DBField
    public String  wo_type_id;
    
    @DBField @RootField
    public String  flow_id;
    
    @DBField
    public String  task_cc;
    
    @DBField
    public String  task_cc_type;
    
    @DBField
    public String  wo_state;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  state_date;
    
    @DBField
    public String  create_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  create_time;
}
