package com.ztesoft.crmpub.bpm.vo.model;

import java.util.ArrayList;
import java.util.List;

import com.ztesoft.crmpub.bpm.vo.model.his.MLBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.annotaion.SpecField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;
import appfrm.resource.dao.impl.DAO;


@DBObj(tn = MBpmBoFlowInst.TABLE_CODE)
public class MBpmBoFlowInst extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoFlowInst.class);
    public static final String TABLE_CODE = "BPM_BO_FLOW_INST";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
	@SpecField
    public String  spec_id;
    
    @DBField @IDField @RootField
    public String  flow_id;
    
    @DBField
    public String  bo_title;
    
    @DBField
    public String  flow_name;
    
    @DBField
    public String  bo_id;
    
    @DBField
    public String  bo_type_id;
    
    @DBField
    public String  bo_state;
    
    @DBField
    public String  bo_state_name;
    
    @DBField
    public String  table_code;
    
    @DBField
    public String  table_pk_col;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  state_date;
    
    @DBField
    public String  create_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  create_date;
    
    @DBField
    public String  tache_id;
    @DBField
    public String  parent_bo_id;
    
    @DBField
    public String  parent_bo_type_id;
    
    @DBField
    public String  parent_tache_code;
    
    @DBField
    public String  col1_name;
    
    @DBField
    public String  col1;
    
    @DBField
    public String  col2_name;
    
    @DBField
    public String  col2;
    
    @DBField
    public String  col3_name;
    
    @DBField
    public String  col3;
    
    @DBField
    public String  col4_name;
    
    @DBField
    public String  col4;
    
    @DBField
    public String  col5_name;
    
    @DBField
    public String  col5;
    
    @DBField
    public String  col6_name;
    
    @DBField
    public String  col6;
    
    @DBField
    public String  col7_name;
    
    @DBField
    public String  col7;
    
    @DBField
    public String  col8_name;
    
    @DBField
    public String  col8;
    
    @DBField
    public String  col9_name;
    
    @DBField
    public String  col9;
    
    @DBField
    public String  col10_name;
    
    @DBField
    public String  col10;
    
    @DBField
    public String  col11_name;
    
    @DBField
    public String  col11;
    
    @DBField
    public String  col12_name;
    
    @DBField
    public String  col12;
    
    @DBField
    public String  col13_name;
    
    @DBField
    public String  col13;
    
    @DBField
    public String  col14_name;
    
    @DBField
    public String  col14;
    
    @DBField
    public String  col15_name;
    
    @DBField
    public String  col15;
    
    @DBField
    public String  flow_type;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  plan_finish_time;
    
    public String template_id;
    
    @DBField
    public String  parent_wo_id; // 父流程的工单表bpm_wo_task的主键
    
    private  List<MBpmWoTask> workOrders=new ArrayList<MBpmWoTask>();//流程环节任务单
    
    //private  List<MBpmBoChange> boChangeRecords=new ArrayList<MBpmBoChange>();//流程环节变动信息
    
    private  List<MBpmWoCC> workOrderCcs=new ArrayList<MBpmWoCC>();//流程环节工单抄送信息 
    
    private  List<MBpmWoTaskExec> workOrderExecutors=new ArrayList<MBpmWoTaskExec>();//流程工单执行者

    private SBpmBoFlowDef flowDef =null;//流程规格定义
    private SBpmBoFlowTache flowTache = null; //流程环节规格定义
    
	public List<MBpmWoTask> getWorkOrders() {
		return workOrders;
	}

	public void setWorkOrders(List<MBpmWoTask> workOrders) {
		this.workOrders = workOrders;
	}

	public List<MBpmWoCC> getWorkOrderCcs() {
		return workOrderCcs;
	}

	public void setWorkOrderCcs(List<MBpmWoCC> workOrderCcs) {
		this.workOrderCcs = workOrderCcs;
	}

	public List<MBpmWoTaskExec> getWorkOrderExecutors() {
		return workOrderExecutors;
	}

	public void setWorkOrderExecutors(List<MBpmWoTaskExec> workOrderExecutors) {
		this.workOrderExecutors = workOrderExecutors;
	}

	/**
	 * @param flowDef the flowDef to set
	 */
	public void setFlowDef(SBpmBoFlowDef flowDef) {
		this.flowDef = flowDef;
	}

	/**
	 * @return the flowDef
	 */
	public SBpmBoFlowDef getFlowDef() {
		if (flowDef ==null){
			loadFlowDef();
		}
		
		return flowDef;
	}
	
	public SBpmBoFlowDef loadFlowDef() {
		if (flowDef ==null){			
			flowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
		}
		
		if (flowDef ==null){
			throw new RuntimeException("加载流程定义数据失败,原因:可能是对应的业务单流程未配置, bo_type_id =" + bo_type_id);
		}
		
		return flowDef;
	}
	public void setSBpmBoFlowTache(SBpmBoFlowTache flowTache){
		this.flowTache=flowTache;
	}
	public SBpmBoFlowTache getSBpmBoFlowTacheById(String tacheId){
		return (SBpmBoFlowTache)SBpmBoFlowTache.getDAO().findById(tacheId);
	}
	public SBpmBoFlowTache getFlowTache(String boTypeId, String tacheCode){
		List<IVO> flowTaches = SBpmBoFlowTache.getDAO().newQuerySQL(" bo_type_id = ? and tache_code = ? ").findByCond(boTypeId,tacheCode);
		if(flowTaches != null && flowTaches.size() > 0){
			return (SBpmBoFlowTache)flowTaches.get(0);
		}
		return null;
	}
	
	/**
     * 竣工当前流程实例及其工单实例，实例写到历史表
     * @param flowInst
     */
    public void finishOrder() {
    	 //1.竣工工单
	   	 String sqlInsertWoTask = "INSERT INTO l_bpm_wo_task SELECT *  FROM bpm_wo_task WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertWoTask, flow_id);    	
	   	 MBpmWoTask.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //2.竣工工单执行者
	   	 String sqlInsertWoTaskExec = "INSERT INTO l_bpm_wo_task_exec SELECT * FROM bpm_wo_task_exec WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertWoTaskExec, flow_id);    	
	   	 MBpmWoTaskExec.getDAO().deleteByRootId(flow_id);
	            
	     //3.竣工抄送工单
	   	 //String sqlInsertWoCc = "INSERT INTO l_bpm_wo_cc SELECT *  FROM bpm_wo_cc WHERE flow_id = ? ";
	   	 //DAO.update(sqlInsertWoCc, flow_id);    	
	   	 //MBpmWoCC.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //4. 竣工申请单变更记录
	   	 String sqlInsertBoChange = "INSERT INTO l_bpm_bo_change SELECT *  FROM bpm_bo_change WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertBoChange, flow_id);    	
	   	 MBpmBoChange.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //5.竣工消息
	   	 String sqlInsertBoMsg = "INSERT INTO l_bpm_bo_msg SELECT *  FROM bpm_bo_msg WHERE flow_id = ?";
	   	 DAO.update(sqlInsertBoMsg, flow_id);    	
	   	 MBpmBoMsg.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //6.属性实例  
	   	 String sqlInsertAttrInst = "INSERT INTO l_bpm_attr_inst SELECT *  FROM bpm_attr_inst WHERE flow_id = ?";
	   	 DAO.update(sqlInsertAttrInst, flow_id);    	
	   	 MBpmAttrInst.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //7.竣工流程实例    	 
	   	 MLBpmBoFlowInst flowInstHis = new MLBpmBoFlowInst();
	   	 flowInstHis.readFromMap(this.saveToMap());    	 	
	   	 flowInstHis.getDao().insert(flowInstHis);
	   	 this.getDao().deleteById(this);	 
	   	
    }
    
}
