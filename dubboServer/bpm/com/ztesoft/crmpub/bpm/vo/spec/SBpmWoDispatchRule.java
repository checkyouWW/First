package com.ztesoft.crmpub.bpm.vo.spec;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = SBpmWoDispatchRule.TABLE_CODE)
public class SBpmWoDispatchRule extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(SBpmWoDispatchRule.class);
    public static final String TABLE_CODE = "BPM_WO_DISPATCH_RULE";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  wo_type_id;
    
    @DBField @RootField
    public String  bo_type_id;
    
    @DBField
    public String  tache_code;
    
    @DBField
    public String  dest_method;
    
    @DBField
    public String  dest_worker;
    
    @DBField
    public String  dest_worker_type;
    
    @DBField
    public String  cc_method;
    
    @DBField
    public String  cc_worker;
    
    @DBField
    public String  cc_worker_type;
    
    @DBField
    public String  dispatch_way;
}
