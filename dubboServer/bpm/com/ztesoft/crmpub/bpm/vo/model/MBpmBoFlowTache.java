package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmBoFlowTache.TABLE_CODE)
public class MBpmBoFlowTache extends VO implements IVO{
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoFlowTache.class);
    public static final String TABLE_CODE = "bpm_bo_flow_tache";
    
    public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	
	@DBField @IDField @RootField
    public String  bo_type_id;
	
	@DBField 
	public String tache_code="";
	
	@DBField 
	public String tache_name="";
	
	@DBField 
	public String seq_no="";
	
	@DBField 
	public String work_type="";
	
	@DBField 
	public String task_type="";
	
	@DBField 
	public String tache_type="";
	
	@DBField 
	public String bo_state="";
	
	@DBField 
	public String bo_state_name="";
	
	@DBField 
	public String skip_cond_type="";
	
	@DBField 
	public String skip_cond_expr="";
	
	@DBField 
	public String is_syn="";
	
	@DBField 
	public String tache_url="";
	
	@DBField 
	public String dispatch_way="";
	
	@DBField 
	public String syn_handler="";
}
