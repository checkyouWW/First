package com.ztesoft.dubbo.se.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = ServiceOrder.TABLE_CODE)
public class ServiceOrder extends VO implements IVO {

	private static final long serialVersionUID = 2604109990807190332L;

	public static final IVOMeta META = IVOMeta.getInstance(ServiceOrder.class);

	public static final String TABLE_CODE = "service_order";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField @IncreaseField
	public String service_order_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String data_inst_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String service_inst_id;
	
	@DBField
	public String service_id;
	
	@DBField
	public String service_type;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String apply_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String ability_id;
	
	@DBField
	public String create_date;
	
	@DBField
	public String state_date;
	
	@DBField
	public String state;
	
	@DBField
	public String inf_resp_id;
	
	@DBField
	public String apply_staff_id;
	
	@DBField
	public String org_id;
	
	@DBField
	public String acct_time;
	
	@DBField
	public String his_acct_time;
	
	@DBField
	public String lan_num;
	
	@DBField
	public String re_run_flag;
	
	@DBField
	public String create_view_where_sql;
	
	@DBField
	public String select_where_sql;
}
