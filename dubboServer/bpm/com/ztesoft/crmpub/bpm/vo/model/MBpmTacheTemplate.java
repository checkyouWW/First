package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmTacheTemplate.TABLE_CODE)
public class MBpmTacheTemplate extends VO implements IVO{
    public static final IVOMeta META = IVOMeta.getInstance(MBpmTacheTemplate.class);
    public static final String TABLE_CODE = "bpm_tache_template";
    
    public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	
	@DBField @IDField @RootField
    public String  template_id;
	
	@DBField
	public String  template_name;
	
	@DBField
	public String  template_desc;
	
	@DBField(type=DBField.TYPE_DATE)
	public String  create_date;
	
	@DBField
	public String  oper_staff;
}
