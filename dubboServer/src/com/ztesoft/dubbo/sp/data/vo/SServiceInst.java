package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 服务实例
 *
 */
@DBObj(tn = SServiceInst.TABLE_CODE)
public class SServiceInst extends VO implements IVO {

	private static final long serialVersionUID = 2604109990807190332L;

	public static final IVOMeta META = IVOMeta.getInstance(SServiceInst.class);

	public static final String TABLE_CODE = "s_service_inst";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String inst_id;
	   
	@DBField
	public String apply_id;

	@DBField
	public String apply_code;

	@DBField
	public String apply_name;
	
	@DBField
	public String service_id;
	
	@DBField
	public String service_type;
	
	@DBField
	public String eff_date;
	
	@DBField
	public String exp_date;
	
	@DBField
	public String apply_date;
	
	@DBField
	public String apply_staff_id;
	
	@DBField
	public String org_id;
	
	@DBField
	public String state;
	
	@DBField
	public String state_date;
	
	@DBField
	public String last_data_acct;
	
	@DBField
	public String last_data_date;
	
}
