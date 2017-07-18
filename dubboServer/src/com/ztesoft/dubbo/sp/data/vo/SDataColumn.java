package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 数据服务实例字段信息
 *
 */
@DBObj(tn = SDataColumn.TABLE_CODE)
public class SDataColumn extends VO implements IVO {

	private static final long serialVersionUID = 3341727896932891183L;

	public static final IVOMeta META = IVOMeta.getInstance(SDataColumn.class);

	public static final String TABLE_CODE = "s_data_column";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String column_inst_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String data_inst_id;

	@DBField(type = DBField.TYPE_NUMBER)
	public String column_id;
	
	@DBField
	public String is_dst;
	
	@DBField
	public String alg_type;
	
	@DBField
	public String is_acct="";
	
	@DBField
	public String seq="";
	
}
