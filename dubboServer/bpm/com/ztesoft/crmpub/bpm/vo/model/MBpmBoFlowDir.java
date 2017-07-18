package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmBoFlowDir.TABLE_CODE)
public class MBpmBoFlowDir extends VO implements IVO{
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoFlowDir.class);
    public static final String TABLE_CODE = "bpm_bo_flow_def_dir";
    
    public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	@DBField @IDField @RootField
    public String  flow_dir_id;
	
	@DBField
	public String  flow_dir_name;
	
	@DBField
	public String  parent_dir_id;
	
	@DBField
	public String  state;
	
	@DBField(type=DBField.TYPE_DATE)
	public String  create_date;
	
	@DBField(type=DBField.TYPE_DATE)
	public String  state_date;
	
	@DBField
	public String  dir_desc;

}
