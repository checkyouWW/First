package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmBoMsg.TABLE_CODE)
public class MBpmBoMsg extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoMsg.class);
    public static final String TABLE_CODE = "BPM_BO_MSG";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  msg_id;
    
    @DBField
    public String  msg_type;
    
    @DBField
    public String  send_handler;
    
    @DBField
    public String  bo_id;
    
    @DBField
    public String  bo_type_id;
    
    @DBField
    public String  bo_state;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  accept_date;
    
    @DBField
    public String  msg_result;
    
    @DBField
    public String  msg_result_content;
    
    @DBField
    public String  oper_id;
    
    @DBField  @RootField
    public String  flow_id;
    
    @DBField
    public String  wo_id;
    
    @DBField
    public String resp_content;

    @DBField
    public String msg_action;
}
