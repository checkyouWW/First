package com.ztesoft.crmpub.bpm.vo.spec;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.annotaion.SortField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = SBpmTemplateExt.TABLE_CODE)
public class SBpmTemplateExt extends VO implements IVO {
	public static final String TABLE_CODE = "bpm_template_ext";
	public  static final IVOMeta META = IVOMeta.getInstance(SBpmTemplateExt.class);
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return IVOMeta.getInstance(SBpmTemplateExt.class).getDAOMeta().getDAO();
    }
	
	//扩展信息分类
	@DBField  @IDField 
	public String template_ext_id;
	//扩展信息名称
	@DBField @NameField
	public String template_ext_name;
	//参与人类型标识
	@DBField @RootField
	public String template_id;
	//顺序
	@DBField @SortField(orderBy="asc")
	public String order_by;
	
	@DBField
	public String is_base_info;
	
	
}
