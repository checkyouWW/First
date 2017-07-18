package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.annotaion.ValueField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmAttrInst.TABLE_CODE)
public class MBpmAttrInst extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmAttrInst.class);
    public static final String TABLE_CODE = "bpm_attr_inst";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  attr_inst_id;
    
    @DBField @RootField
    public String  flow_id;
    
    @DBField
    public String  bo_type_id;
    
    @DBField
    public String  tache_code;
    
    @DBField
    public String  attr_id;
    
    @DBField @ValueField
    public String  attr_value;
    
    @DBField
    public String  attr_value2;
    
    @DBField
    public String  template_id;
    
    @DBField 
    public String wo_id;
    
}
