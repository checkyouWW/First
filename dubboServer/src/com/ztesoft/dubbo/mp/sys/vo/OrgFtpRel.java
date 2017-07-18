package com.ztesoft.dubbo.mp.sys.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = OrgFtpRel.TABLE_CODE)
public class OrgFtpRel extends VO implements IVO {

	private static final long serialVersionUID = 5434655755426126381L;

	public static final IVOMeta META = IVOMeta.getInstance(OrgFtpRel.class);

	public static final String TABLE_CODE = "org_ftp_rel";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField @IncreaseField
	public String rel_id;

	@DBField
	public String ftp_id;
	
	@DBField
	public String user;
	
	@DBField
	public String password;
	
	@DBField
	public String path;
	
	@DBField
	public String state;
	
	@DBField
	public String create_date;
	
	@DBField
	public String org_id;

}
