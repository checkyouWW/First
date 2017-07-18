package com.ztesoft.crmpub.bpm.dispatchRule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.task.FetchValueClass;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.resource.dao.impl.DAO;

public class TaskWorkerHelper extends FetchValueClass{

	@Override
	public String execute() throws Exception {
		
		MsgBean bean = this.msg;
		if(bean == null) return "false";
		String bo_type_id =    bean.getBoTypeId();
		String bo_id = bean.getBoId(); 
		String flow_id = bean.getFlowId();
		String wo_id =bean.getWoId();
		String wo_type_id =bean.getWoTypeId();
		List<HashMap> lst1 = bean.getAttrList();
		for(Map attrMap : lst1){
			SBpmTemplateAttr attr = (SBpmTemplateAttr)SBpmTemplateAttr.getDAO().findById(StrTools.getStrValue(attrMap, "name") );
			if("next_auditor".equals(attr.field_name)){
				
				return StrTools.getStrValue(attrMap, "value");
			}
		}
		return "";
//		String taskWorker ="";
//		String bo_type_id = BpmContext.getVar("bo_type_id"); 
//		String bo_id = BpmContext.getVar("bo_id");  
//		String sql = "select * from bpm_wo_task a where a.bo_type_id =? and a.bo_id=?    order by a.wo_id desc ";
//		List lst = DAO.queryForMap(sql, new String[]{bo_type_id,bo_id});
//		if(lst == null ||lst.isEmpty()||lst.size()<2){
//			return "";
//		}
//		String previousWoTypeId = "";
//		String previousTacheCode = "";
//		String flow_id = "";
//		String wo_id = "";
//		String attrId= "";
//		for(int i=0;i< lst.size();i++ ){
//			if(i>1){
//				break;
//			}else if ( i==1){
//				Map map = (Map)lst.get(i);
//				previousWoTypeId = StrTools.getStrValue(map,"wo_type_id");
//				previousTacheCode = StrTools.getStrValue(map,"tache_code");
//				flow_id = StrTools.getStrValue(map,"flow_id");
//				wo_id = StrTools.getStrValue(map,"wo_id");
//				
//			}
//		}
//		
//		SBpmWoType sbtw = (SBpmWoType)SBpmWoType.getDAO().findById(previousWoTypeId);
//		String attrIdSql = "select attr_id from BPM_TEMPLATE_ATTR a where a.template_id = ? and a.FIELD_NAME = ? ";
//		if(sbtw!= null&& StrTools.isNotEmpty( sbtw.template_id)){
//			attrId = DAO.querySingleValue(attrIdSql, new String[]{sbtw.template_id,"next_auditor"});
//		}else{
//			return "";
//		}
//		
//		 
// 
//		taskWorker = getTaskWorker( bo_type_id,  flow_id, wo_id, attrId); //第四个参数 为 下一办理人的attr_id 
//		
//		return taskWorker;
	}
	
	public String getTaskWorker(String bo_type_id, String flow_id,String wo_id, String attrId){
		String sql2 = "select a.attr_value from bpm_attr_inst a" +
		"	where a.bo_type_id =?  and a.flow_id =? and a.wo_id = ?  and a.attr_id =?"; 
		String taskWorker = DAO.querySingleValue(sql2, new String[]{
				bo_type_id,flow_id,wo_id,attrId
				
		});//第四个参数 为 下一办理人的attr_id 
		
		if(StrTools.isNotEmpty(taskWorker) ){
			return taskWorker;
		}else{
			
			return getTaskWorker( bo_type_id,  flow_id, wo_id, attrId);
		}
	}
	

}
