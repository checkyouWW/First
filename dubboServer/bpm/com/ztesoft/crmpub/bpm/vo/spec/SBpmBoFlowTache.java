package com.ztesoft.crmpub.bpm.vo.spec;

import java.util.ArrayList;
import java.util.List;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = SBpmBoFlowTache.TABLE_CODE)
public class SBpmBoFlowTache extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(SBpmBoFlowTache.class);
    public static final String TABLE_CODE = "BPM_BO_FLOW_TACHE";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	@DBField @IDField
    public String tache_id;
    @DBField @RootField
    public String  bo_type_id;
    
    @DBField  
    public String  tache_code;
    
    @DBField @NameField
    public String  tache_name;
    
    @DBField
    public String  tache_type;
    @DBField
    public String  tache_url;
    @DBField
    public String  seq_no;
    
    @DBField
    public String  work_type;
    
    @DBField
    public String  task_type;
    
    @DBField
    public String  bo_state;
    
    @DBField
    public String  bo_state_name;
    
    @DBField
    public String  skip_cond_expr;
    
    @DBField
    public String  skip_cond_type;
    
    @DBField
    public String  dispatch_way;
    @DBField
    public String  is_syn;
    @DBField
    public String  syn_handler;
    
	
	private  List<IVO> woTypes= null;//工单类型
    
    private  List<IVO> dispatchRules= null;//流程派单规则	

    public SBpmBoFlowTache getSBpmBoFlowTacheById(){
    	return (SBpmBoFlowTache)SBpmBoFlowTache.getDAO().findById(this.tache_id);
    }
	/**
	 * 从数据库加载环节对应的工单类型,一个环节可能会对应多个派单规则
	 * @return
	 */
	private List<IVO> loadWorkOrderTypes() {
		if (woTypes == null){
			woTypes = SBpmWoType.getDAO().query(" bo_type_id=? and tache_code = ? ", this.bo_type_id, this.tache_code);	
		}
		
		if (ListUtil.isEmpty(woTypes)){
			throw new RuntimeException("派单规则加载失败，原因：派单规则未配置：bo_type_id=" + bo_type_id);
		}
		
		return dispatchRules;
	}
	


	/**
	 * 获取工单类型
	 * 目前环节和woType是一对一关系，暂时不要扩展为1对多关系 modified by joshui
	 * @return the workOrderTypes
	 */
	public SBpmWoType getWorkOrderType() {
		if (woTypes == null){
			loadWorkOrderTypes();
		}
		return (SBpmWoType) woTypes.get(0);
	}
	
	/**
	 * 获取环节所有的派单规则
	 * @return
	 */
	public List<IVO> getDispatchRules() {
		if (dispatchRules == null){
			loadDispatchRules();
		}
		
		return dispatchRules;
	}
	
	
	/**
	 * 获取工单类型对应的派单规则
	 * 可以配置多种派单规则 modified by joshui
	 * @param woTypeId
	 * @return
	 */
	public List<SBpmWoDispatchRule> getDispatchRule(String woTypeId) {
		if (dispatchRules == null){
			loadDispatchRules();
		}
		
		List<SBpmWoDispatchRule> rs = new ArrayList<SBpmWoDispatchRule>();
		for (IVO vo : dispatchRules){
			SBpmWoDispatchRule dispatchRule = (SBpmWoDispatchRule) vo;
			if (woTypeId.equalsIgnoreCase(dispatchRule.wo_type_id)){
				rs.add(dispatchRule);
			}
		}
		
		return rs;
	}

	/**
	 * 从数据库加载环节对应的所有的派单规则,一个环节可能会对应多个派单规则
	 * @return
	 */
	public List<IVO> loadDispatchRules() {
		if (dispatchRules == null){
			dispatchRules = SBpmWoDispatchRule.getDAO().query(" bo_type_id=? and tache_code = ? ", this.bo_type_id, this.tache_code);	
		}
		
		if (ListUtil.isEmpty(dispatchRules)){
			throw new RuntimeException("派单规则加载失败，原因：派单规则未配置：bo_type_id=" + bo_type_id);
		}
		
		return dispatchRules;
	}

	/**
	 * 获取工单类型
	 * @return the workOrderTypes
	 */
	public List<IVO> getWorkOrderTypes() {
		if (woTypes == null){
			loadWorkOrderTypes();
		}
		return woTypes;
	}

	
}
