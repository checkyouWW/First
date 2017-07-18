package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 数据服务实例能力信息
 *
 */
@DBObj(tn = SDataInst.TABLE_CODE)
public class SDataInst extends VO implements IVO {

	private static final long serialVersionUID = 2604109990807190332L;

	public static final IVOMeta META = IVOMeta.getInstance(SDataInst.class);

	public static final String TABLE_CODE = "s_data_inst";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String data_inst_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String apply_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String service_id;
	
	@DBField
	public String extract_type;
	
	@DBField
	public String data_code;
	
	@DBField
	public String data_name;
	
	@DBField
	public String data_range;
	
	@DBField
	public String start_time;
	
	@DBField
	public String end_time;
	
	@DBField
	public String is_history;
	
	@DBField
	public String history_acct;
	
	@DBField
	public String create_order_time;
	
	@DBField
	public String service_type;
	
	@DBField
	public String view_name;
	
	@DBField
	public String rerun_interval;
	
	@DBField
	public String create_view_where_sql;
	
	@DBField
	public String select_where_sql;
}
