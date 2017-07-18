package com.ztesoft.dubbo.se.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = SServiceInstAlert.TABLE_CODE)
public class SServiceInstAlert extends VO {
	private static final long serialVersionUID = -6700507703237782504L;
	
	public static final String TABLE_CODE = "s_service_inst_alert";
	
	public static final IVOMeta META = IVOMeta.getInstance(SServiceInstAlert.class);
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField @IncreaseField
	public String alert_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String inst_id;
	
	@DBField
	public String alert_type;
	
	@DBField
	public String start_time;
	
	@DBField
	public String end_time;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String duration;
	
	@DBField
	public String details_msg;
	
	@DBField
	public String service_type;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String service_id;
	
}
