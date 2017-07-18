package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * s_data_column临时表
 *
 */
@DBObj(tn = MDataColumnChangeNotify.TABLE_CODE)
public class MDataColumnChangeNotify extends VO implements IVO {

	private static final long serialVersionUID = -5162274925886880050L;

	public static final IVOMeta META = IVOMeta.getInstance(MDataColumnChangeNotify.class);

	public static final String TABLE_CODE = "DATA_CHANGE_NOTIFY_COLUMN";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String column_change_id;

	@DBField
	public String data_inst_id = "";

	@DBField
	public String column_id = "";
	
	@DBField
	public String is_dst = "";
	
	@DBField
	public String alg_type = "";
	
	@DBField
	public String is_acct="";
	
	@DBField
	public String seq="";
	
	@DBField
	public String flow_id="";

	@DBField
	public String service_id="";

	@DBField
	public String column_name="";

	@DBField
	public String column_code="";

	@DBField
	public String column_type="";

	@DBField
	public String column_length="";

	@DBField
	public String apply_id="";
}
