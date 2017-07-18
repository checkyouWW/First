package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@SuppressWarnings("serial")
@DBObj(tn = CDataAblity.TABLE_CODE)
public class CDataAblity extends VO{
	
	public static final IVOMeta META = IVOMeta.getInstance(CDataAblity.class);
	
	public static final String TABLE_CODE = "C_DATA_ABILITY";
	public static final String PK_ID = "ABILITY_ID";
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField  @IDField
	public String ability_id;
	
	@DBField
	public String service_id;
	
	@DBField
	public String data_code;
	
	@DBField
	public String data_name;
	
	@DBField
	public String first_division;
	
	@DBField
	public String second_division;
	
	@DBField
	public String begin_dispath_time;
	
	@DBField
	public String end_dispath_time;
	
	@DBField
	public String comments;
	
	@DBField
	public String is_same_src;
	
	@DBField
	public String lan_division;
	
	@DBField
	public String acct_division;
	
	@DBField
	public String supply_freq;
	
	@DBField
	public String is_static_table;
	
}
