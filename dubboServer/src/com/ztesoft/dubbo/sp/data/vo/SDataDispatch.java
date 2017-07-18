package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 数据服务实例分发模式
 *
 */
@DBObj(tn = SDataDispatch.TABLE_CODE)
public class SDataDispatch extends VO implements IVO {

	private static final long serialVersionUID = 2604109990807190332L;

	public static final IVOMeta META = IVOMeta.getInstance(SDataDispatch.class);

	public static final String TABLE_CODE = "s_data_dispatch";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String dispatch_id;

	@DBField
	public String data_inst_id;

	@DBField
	public String dispatch_type;
	
	@DBField
	public String ftp_data_type;
	
	@DBField
	public String ftp_ip;
	
	@DBField
	public String ftp_port;
	
	@DBField
	public String ftp_user;
	
	@DBField
	public String ftp_password;
	
	@DBField
	public String ftp_def_dir;
	
	@DBField
	public String ftp_split;
	
	@DBField
	public String import_type;
	
	@DBField
	public String create_table;
	
	@DBField
	public String sqoop_type;
	
	@DBField
	public String db_url;
	
	@DBField
	public String db_user;
	
	@DBField
	public String db_password;
	
	@DBField
	public String db_type;
	
	@DBField
	public String protocol_url;
	
	@DBField
	public String scheme_code;
	
	@DBField
	public String table_code;
	
	@DBField
	public String def_file_name;
	
	@DBField
	public String def_file_code;
	
}
