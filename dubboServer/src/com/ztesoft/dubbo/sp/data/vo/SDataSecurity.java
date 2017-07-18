package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 安全数据使用申请信息
 *
 */
@DBObj(tn = SDataSecurity.TABLE_CODE)
public class SDataSecurity extends VO implements IVO {

	private static final long serialVersionUID = 9009519145221908446L;

	public static final IVOMeta META = IVOMeta.getInstance(SDataSecurity.class);

	public static final String TABLE_CODE = "s_data_security";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String security_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String apply_id;

	@DBField
	public String data_code;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String data_inst_id;
	
	@DBField(type = DBField.TYPE_NUMBER)
	public String ability_id;
	
	@DBField
	public String create_date;
	
	@DBField
	public String old_data_inst_id;
	
}
