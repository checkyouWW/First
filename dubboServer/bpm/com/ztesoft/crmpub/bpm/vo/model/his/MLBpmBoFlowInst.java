package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;

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


@DBObj(tn = MLBpmBoFlowInst.TABLE_CODE)
public class MLBpmBoFlowInst  extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmBoFlowInst .class);
    public static final String TABLE_CODE = "L_BPM_BO_FLOW_INST";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
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
    public String  parent_bo_id;
    
    @DBField
    public String  parent_bo_type_id;
    
    @DBField
    public String  parent_tache_code;
    
    @DBField
    public String parent_wo_id;
    
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

    @SpecField
    public String spec_id;
    
    @SpecField
    public String tache_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  plan_finish_time;
    
    /**
     * 历史表恢复到在途表
     * @param flowInst
     */
    public void resumeOrder() {
    	 //1.竣工工单
	   	 String sqlInsertWoTask = "INSERT INTO bpm_wo_task SELECT *  FROM l_bpm_wo_task WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertWoTask, flow_id);    	
	   	 MLBpmWoTask.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //2.竣工工单执行者
	   	 String sqlInsertWoTaskExec = "INSERT INTO bpm_wo_task_exec SELECT * FROM l_bpm_wo_task_exec WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertWoTaskExec, flow_id);    	
	   	 MLBpmWoTaskExec.getDAO().deleteByRootId(flow_id);
	            
	     //3.竣工抄送工单
	   	 //String sqlInsertWoCc = "INSERT INTO l_bpm_wo_cc SELECT *  FROM bpm_wo_cc WHERE flow_id = ? ";
	   	 //DAO.update(sqlInsertWoCc, flow_id);    	
	   	 //MBpmWoCC.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //4. 竣工申请单变更记录
	   	 String sqlInsertBoChange = "INSERT INTO bpm_bo_change SELECT *  FROM l_bpm_bo_change WHERE flow_id = ? ";
	   	 DAO.update(sqlInsertBoChange, flow_id);    	
	   	 MLBpmBoChange.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //5.竣工消息
	   	 String sqlInsertBoMsg = "INSERT INTO bpm_bo_msg SELECT *  FROM l_bpm_bo_msg WHERE flow_id = ?";
	   	 DAO.update(sqlInsertBoMsg, flow_id);    	
	   	 MLBpmBoMsg.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //6.属性实例  
	   	 String sqlInsertAttrInst = "INSERT INTO bpm_attr_inst SELECT *  FROM l_bpm_attr_inst WHERE flow_id = ?";
	   	 DAO.update(sqlInsertAttrInst, flow_id);    	
	   	 MLBpmAttrInst.getDAO().deleteByRootId(flow_id);
	   	 
	   	 //7.竣工流程实例    	 
	   	 MBpmBoFlowInst flowInstHis = new MBpmBoFlowInst();
	   	 flowInstHis.readFromMap(this.saveToMap());    	 	
	   	 flowInstHis.getDao().insert(flowInstHis);
	   	 this.getDao().deleteById(this);	 
	   	
    }
}
