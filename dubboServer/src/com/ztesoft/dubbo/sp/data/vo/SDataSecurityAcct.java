package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 安全数据使用申请账期
 *
 */
@DBObj(tn = SDataSecurityAcct.TABLE_CODE)
public class SDataSecurityAcct extends VO implements IVO {

	private static final long serialVersionUID = 7936157981848785811L;

	public static final IVOMeta META = IVOMeta.getInstance(SDataSecurityAcct.class);

	public static final String TABLE_CODE = "s_data_security_acct";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String acct_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String security_id;

	@DBField
	public String data_acct;
	
	@DBField
	public String key_code;
	
}
