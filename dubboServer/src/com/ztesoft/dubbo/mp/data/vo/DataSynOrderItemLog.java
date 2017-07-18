package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@SuppressWarnings("serial")
@DBObj(tn = DataSynOrderItemLog.TABLE_CODE)
public class DataSynOrderItemLog extends VO{
	
	public static final IVOMeta META = IVOMeta.getInstance(DataSynOrderItemLog.class);
	
	public static final String TABLE_CODE = "DATA_SYN_ORDER_ITEM_LOG";
	public static final String PK_ID = "log_id";
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField  @IDField
	public String log_id;
	
	@DBField
	public String order_item_id;
	
	@DBField
	public String log_desc;
	
	@DBField
	public String create_date;
	
	@DBField
	public String exec_date;
	
	@DBField
	public String state;
}
