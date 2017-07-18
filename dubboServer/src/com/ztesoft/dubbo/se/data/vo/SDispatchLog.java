package com.ztesoft.dubbo.se.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = SDispatchLog.TABLE_CODE)
public class SDispatchLog extends VO{

	private static final long serialVersionUID = -7383315611084138812L;
	
	public static final String TABLE_CODE = "s_service_inst_dispatch_log";
	
	public static final IVOMeta META = IVOMeta.getInstance(SDispatchLog.class);
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}

	@DBField @IDField @IncreaseField
	public String log_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String inst_id;
	
	@DBField
	public String create_date;
	
	@DBField
	public String dispatch_type;
	
	@DBField
	public String file_path;
	
	@DBField
	public String dispatch_state;
	
	@DBField
	public String state_date;
	
	@DBField
	public String dispatch_id;
	
}
