package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@SuppressWarnings("serial")
@DBObj(tn = DataSynOrderItem.TABLE_CODE)
public class DataSynOrderItem extends VO{
	
	public static final IVOMeta META = IVOMeta.getInstance(DataSynOrderItem.class);
	
	public static final String TABLE_CODE = "DATA_SYN_ORDER_ITEM";
	public static final String PK_ID = "ORDER_ITEM_ID";
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField  @IDField
	public String order_item_id;
	
	@DBField
	public String order_id;
	
	@DBField
	public String src_sys_code;
	
	@DBField
	public String src_owner;
	
	@DBField
	public String src_schema_code;
	
	@DBField
	public String src_table_code;
	
	@DBField
	public String acct_time;
	
	@DBField
	public String create_date;
	
	@DBField
	public String exec_date;
	
	@DBField
	public String finish_date;
	
	@DBField
	public String state;
	
	@DBField
	public String inf_resp_id;
	
	@DBField
	public String lan_id;
	
	@DBField
	public String re_run_flag;
}
