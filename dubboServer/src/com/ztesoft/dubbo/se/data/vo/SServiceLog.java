package com.ztesoft.dubbo.se.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = SServiceLog.TABLE_CODE)
public class SServiceLog extends VO {

	private static final long serialVersionUID = 7530537246212918471L;
	
	public static final String TABLE_CODE = "s_service_inst_schedule_log";
	
	public static final IVOMeta META = IVOMeta.getInstance(SServiceLog.class);
	
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
	public String service_order_id;
	
	@DBField
	public String service_type;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String service_id;
	
	@DBField
	public String log_time;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String result;
	
	@DBField
	public String log_desc;
	
	@DBField
	public String step_name;

}
