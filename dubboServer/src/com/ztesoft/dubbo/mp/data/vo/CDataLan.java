package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = CDataLan.TABLE_CODE)
public class CDataLan extends VO{

	public static final IVOMeta META = IVOMeta.getInstance(CDataLan.class);

	public static final String TABLE_CODE = "C_DATA_lan";
	public static final String PK_ID = "DATA_LAN_ID";
	
	@DBField  @IDField  @IncreaseField
	public String data_lan_id;
	
	@DBField
	public String ability_id;
	
	@DBField
	public String lan_id;
	
	@DBField
	public String area_id;
	
	@DBField
	public String lan_name;
	
	@DBField
	public String state;
	
}
