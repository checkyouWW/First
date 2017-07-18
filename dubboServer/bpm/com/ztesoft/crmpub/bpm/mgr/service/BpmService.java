package com.ztesoft.crmpub.bpm.mgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import spring.util.SpringContextUtil;

import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.IAttrVal;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.mgr.bo.BpmFlowBO;
import com.ztesoft.crmpub.bpm.util.AttrValFetchUtil;
import com.ztesoft.crmpub.bpm.util.FlowHelper;
import com.ztesoft.crmpub.bpm.util.WoTaskHelper;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmAttrVal;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmTemplateAttr;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;
import comx.order.inf.IContext;
import comx.order.inf.IKeyValues;
import net.buffalo.service.invoker.ContextMeta;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
public class BpmService {

	
	/**
	 * 启动流程
	 * @param flowData
	 * @param action_type
	 * @param bo_title
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean startFlow(Map<String, Object> flowData, String action_type, String bo_title) throws Exception{
		boolean result = false;

		if(flowData == null || flowData.isEmpty()){
			return result;
		}

		String autoFinishBeginWO = (String)flowData.get("autoFinishBeginWO");//是否自动完成开始环节
		if(IKeyValues.ACTION_TYPE_M.equals(action_type) && StringUtil.isNotEmpty(autoFinishBeginWO) && autoFinishBeginWO.equalsIgnoreCase("true")){
			flowData.put("action", MsgConsts.ACTION_WO_FINISH);
		}
		else if(IKeyValues.ACTION_TYPE_A.equals(action_type)){
			flowData.put("action", MsgConsts.ACTION_NEW_FLOW);
		}
		else{
			return false;
		}

		String oper_id = (String)flowData.get("oper_id");
		if(StrTools.isEmpty(oper_id)){
			oper_id = SessionHelper.getStaffId();
		}
		flowData.put("bo_title", bo_title);
		flowData.put("oper_id", oper_id);

		Map resMap = this.execute(flowData);

		if("0".equals(resMap.get("resCode"))){
			result = true;
		}

		return result;
	}
	
	@Transactional
	public Map execute(Map params) throws Exception {
		Map retMap = new HashMap();

		String action = (String) params.get("action");
		if (StrUtil.isNotEmpty(action)) {
			// 启动流程
			if ( MsgConsts.ACTION_NEW_FLOW.equalsIgnoreCase(action) ) {

				Map map = FlowHelper.startFlow(params, null);
				boolean result = (Boolean)map.get("result");
				retMap.put("flowId", map.get("flowId"));

				if (result) {
					retMap.put("resCode", "0");
					retMap.put("resMsg", "OK!");
				} else {
					retMap.put("resCode", "-1001");
					retMap.put("resMsg", "流程启动失败!");
				}
				
			}else if (MsgConsts.ACTION_WO_FINISH.equalsIgnoreCase(action) || 
					MsgConsts.ACTION_WO_FAIL.equalsIgnoreCase(action) ||
					MsgConsts.ACTION_WO_WITHDRAW.equalsIgnoreCase(action)){

				params.put("oper_id", SessionHelper.getStaffId());
				Map map = WoTaskHelper.returnWoTask(params, null);
				boolean result = (Boolean)map.get("result");
				retMap.put("flowId", map.get("flowId"));
				
				if (result) {
					retMap.put("resCode", "0");
					retMap.put("resMsg", "OK!");
				} else {
					retMap.put("resCode", "-2001");
					retMap.put("resMsg", "工单处理失败!");
				}
			}
		}

		return retMap;
	}

    @Transactional
    public Map submit(Map params) throws Exception {
        Map retMap = new HashMap();
        String staff_id=StrTools.getStrValue(params,"staff_id");
        if(StrTools.isEmpty(staff_id)){
        	 
            try {
				staff_id = SessionHelper.getStaffId();
			} catch (Exception e) {
				
				staff_id = StrTools.getStrValue(params,"end_task_worker");// 自动办结
			}
        }
        params.put("oper_id", staff_id);
        String action = (String) params.get("action");
        List<Map> attrList=(ArrayList)params.get("attrList");
        if(attrList!=null) {
            for (Map attr : attrList) {
                String type = StrTools.getStrValue(attr, "cus_type");
                if (StrTools.isNotEmpty(type) && type.equals("action")) {
                    action = StrTools.getStrValue(attr, "value").toUpperCase();
                }
            }
        }
        if(StrTools.isEmpty(action)){
            action=MsgConsts.ACTION_WO_FINISH;
        }
        params.put("action",action);
        if (StrUtil.isNotEmpty(action)) {
            Map map = WoTaskHelper.returnWoTask(params, null);
            boolean result = (Boolean)map.get("result");
            retMap.put("flowId", map.get("flowId"));

            if (result) {
                retMap.put("resCode", "0");
                retMap.put("resMsg", "处理成功!");
            } else {
                retMap.put("resCode", "-2001");
                retMap.put("resMsg", "工单处理失败!");
                throw new RuntimeException("工单处理失败!");
            }
        }
        
        return retMap;
    }

	@Transactional
	public Map audit(Map params) throws Exception {
		Map retMap = new HashMap();

		params.put("oper_id", SessionHelper.getStaffId());

		String action = (String) params.get("action");
		if (StrUtil.isNotEmpty(action)) {
			Map map = WoTaskHelper.returnWoTask(params, null);
			boolean result = (Boolean)map.get("result");
			retMap.put("flowId", map.get("flowId"));
			
			if (result) {
				retMap.put("resCode", "0");
				retMap.put("resMsg", "处理成功!");
			} else {
				retMap.put("resCode", "-2001");
				retMap.put("resMsg", "工单处理失败!");
			}
		}
		return retMap;
	}
	
	@Transactional
	public Map findDealPerson(Map params) throws Exception {
		String wo_id = (String) params.get("wo_id");

		List<IVO> typeList = MBpmWoTask.getDAO().newQuerySQL(
		" wo_id = ?  ").findByCond(wo_id);

		if( !CollectionUtils.isEmpty(typeList) ){
			MBpmWoTask woType = (MBpmWoTask)typeList.get(0);
			String sql = "select staff_id,staff_name from dm_staff where staff_id = ?";
			List<IVO> staffList = DAO.queryForMap(sql, woType.dispatch_oper_id);

			if( !CollectionUtils.isEmpty(staffList) ){
				Map staffMap = (Map) staffList.get(0);
				return staffMap;
			}else{
				throw new Exception("根据wo_id:"+wo_id+",无法找到对应的派单人，请检查数据！");
			}
		}else{
			throw new Exception("根据wo_id:"+wo_id+",无法找到对应的派单人，请检查数据！");
		}
	}	
	/**
	 * 根据boTypeId查找对应的模板标识（template_id）
	 * @param boTypeId 流程规格定义
	 * @return
	 * @throws Exception 找不到模板规格
	 * @author lidongsheng
	 * @param tache_code 
	 */
	@Transactional
	public String findTemplateIdByBoTypeId(String boId, String boTypeId, String tacheCode) throws Exception{

		String _tacheCode = tacheCode;
		if( StrUtil.isEmpty(_tacheCode) ){
			_tacheCode = BPMConsts.TACHE_CODE_NEW_REQ;
		}

		if(BpmFlowBO.isNextTacheSkip(boId, boTypeId, _tacheCode)){
			return "-1";
		}

		List<IVO> typeList = SBpmWoType.getDAO().newQuerySQL(
		" bo_type_id = ? and tache_code = ? ").findByCond(
				boTypeId,_tacheCode);

		if( !CollectionUtils.isEmpty(typeList) ){
			SBpmWoType woType = (SBpmWoType)typeList.get(0);
			return woType.template_id;
		}else{
			throw new Exception("根据BO_TYPE_ID:"+boTypeId+",无法找到对应流程模板，请检查处理后刷新页面！");
		}

	}

	/**
	 * 查找模板属性
	 * @param params
	 * @return   vo     - 模板属性定义
	 *           voVal  - 模板属性值定义（多值）
	 *           voInst - 模板属性值实例
	 *           {
	 *  		 data:[{vo:MBpmTemplateAttr1,voVals:MBpmAttrVals1,voInst:MBpmAttrInst1}
	 * 			 	  ,{vo:MBpmTemplateAttr2,voVals:MBpmAttrVals2,voInst:MBpmAttrInst2}
	 * 			 	  ,{vo:MBpmTemplateAttr3,voVals:MBpmAttrVals3,voInst:MBpmAttrInst3}
	 * 			 	  ,{vo:MBpmTemplateAttrN,voVals:MBpmAttrValsN,voInst:MBpmAttrInstN}]
	 * 			 }
	 * @throws Exception
	 * @author lidongsheng
	 */
	@Transactional
	public Map findTplAttrs(Map params) {
		//added by joshui. 相当于把findTemplateIdByBoTypeId里设置环节编码默认值的逻辑提前搬到这里，好设置到threadlocal变量里，供sql占位符使用
		if (StringUtil.isEmpty((String) params.get("tache_code"))) {
			params.put("tache_code", BPMConsts.TACHE_CODE_NEW_REQ);
			if("DEMO".equals((String) params.get("bo_type_id"))){
				params.put("tache_code", "10");
			}
		}
		
		putContextVal(params);

		Map retMap = new HashMap();
		List data = new ArrayList();
		String bo_id = (String) params.get("bo_id");
		String bo_type_id = (String) params.get("bo_type_id");
		String tache_code = (String) params.get("tache_code");


		String templateId = "";

		try {
			templateId = this.findTemplateIdByBoTypeId(bo_id, bo_type_id, tache_code);
		} catch (Exception e) {
			retMap.put("error", e.getMessage());
			return retMap;
		}


		List<IVO> attrValList = SBpmAttrVal
		.getDAO()
		.newQuerySQL(
				" attr_id in (select attr_id from BPM_TEMPLATE_ATTR where template_id = ? ) order by SEQ_NO ")
				.findByCond(templateId);

		List<IVO> tplList = SBpmTemplateAttr.getDAO().findByRootId(templateId);
		if( ! CollectionUtils.isEmpty(tplList) ){
			for( IVO vo: tplList ){
				SBpmTemplateAttr attr = (SBpmTemplateAttr)vo;
				
				IAttrVal attrInst = AttrValFetchUtil.getAttrInstanceByClass(attr);
		    	if(attrInst != null){
		    		attrInst.init(attr, params);
		    	}
		    	
				Map rtnObj = new HashMap();
				data.add(rtnObj);

				List valsList = new ArrayList();
				//添加属性值
				rtnObj.put("voVals", valsList);
				for( IVO valVo: attrValList ){
					SBpmAttrVal attrVal = (SBpmAttrVal)valVo;
					if( attrVal.attr_id.equals(attr.attr_id)  ){
						valsList.add(attrVal);
					}
				}
				
				List<SBpmAttrVal> dataList = AttrValFetchUtil.fetch(attr,params);
				
//				String is_visible = attr.is_visible;
//				if(StrTools.isNotEmpty(is_visible) && IKeyValuesLocal.IFTRUE_F.equals(is_visible)){
//					continue;
//				}
				if( null != dataList){
					valsList.addAll(dataList);
				}
				//添加属性定义（规格+实例结合...）
				rtnObj.put("vo", attr);
			}
		}
		retMap.put("data", data);

		return retMap;
	}

	private void putContextVal(Map params) {
		Map<?, ?> map = SessionHelper.getStaffMap();
		this.putVal(map);
		this.putVal(params);
		//默认本地网
		//BpmContext.putVar(BPMConsts.CTX_VAR.SELECT_LAN_ID, SessionHelper.getStaffLanId());
		putSpecialVal(params);
	}
	
	private void putSpecialVal(Map params){
		if(params!=null) {
			String action = getActionVal(params);
			params.put("action", action);
			BpmContext.putVar("action",action);
			
			String curr_tache_code = (String) params.get("tache_code");
			BpmContext.putVar("curr_tache_code", curr_tache_code);
			params.put("curr_tache_code", curr_tache_code);
		}
	}

	private void putVal(Map<?, ?> map) {
		if(map != null){
			Set<?> keySet = map.keySet();
			Iterator<?> it = keySet.iterator();
			while(it.hasNext()){
				String key = (String) it.next();
				if(map.get(key) instanceof String){
					String value = (String) map.get(key);
					BpmContext.putVar(key, value);
				}else if(map.get(key) instanceof List){
                    List value = (List) map.get(key);
                    BpmContext.putListVar(key, value);
                }
			}
		}
	}

    private String getActionVal(Map params) {
        String action = (String) params.get("action");
        List<Map> attrList=(ArrayList)params.get("attrList");
        if(StrTools.isEmpty(action)&&attrList!=null) {
            for (Map attr : attrList) {
                String type = StrTools.getStrValue(attr, "cus_type");
                if (StrTools.isNotEmpty(type) && type.equals("action")) {
                    action = StrTools.getStrValue(attr, "value").toUpperCase();
                }
            }
        }
        if(StrTools.isEmpty(action)){
            action=MsgConsts.ACTION_WO_FINISH;
        }
        return action;
    }

	/**
	 * 属性更新
	 * 
	 * @param attr_id
	 * @param new_value
	 * @throws Exception
	 */
	@Transactional
	public Map updateBpmAttrVal(Map params) throws Exception {
		String attr_id = StrTools.getStrValue(params, "name");
		String new_value = StrTools.getStrValue(params, "value");
		String value_name = StrTools.getStrValue(params, "value_name");
		Map formparams = (Map)params.get("formdata");
		formparams.put("value_name", value_name);
        String action = getActionVal(formparams);
        formparams.put("action",action);
		this.putContextVal(formparams);
		SBpmTemplateAttr attr = (SBpmTemplateAttr) SBpmTemplateAttr.getDAO().findById(attr_id);
		if (attr == null){
			return new HashMap();
		}
		IAttrVal attrVal = AttrValFetchUtil.getAttrInstance(attr);
		if( attrVal != null){
			attrVal.init(attr, formparams);
		}
		// 组装返回参数
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(attrVal == null){
			return retMap;
		}
		List changes = attrVal.set(attr, new_value);
		//变动、联动的属性列表
		retMap.put("changeAttrs", changes);

		return retMap;
	}

    /**
     * 属性更新
     *
     * @param attr_id
     * @param new_value
     * @throws Exception
     */
    @Transactional
    public List findAttrVal(Map params) throws Exception {
        String attr_id = StrTools.getStrValue(params, "name");
        Map formparams = (Map)params.get("formdata");

        SBpmTemplateAttr attr = (SBpmTemplateAttr) SBpmTemplateAttr.getDAO().findById(attr_id);
        if (attr == null){
            return new ArrayList();
        }
        this.putContextVal(formparams);
        List<IVO> relas = (List<IVO>) SBpmTemplateAttr.getDAO().query(" template_id = ? and field_name = ?",
                new String[]{attr.template_id, "next_step"});
        String tache_attr_id=((SBpmTemplateAttr)relas.get(0)).attr_id;
        List<Map> attrList=(ArrayList<Map>)formparams.get("attrList");
        for(Map map:attrList){
            String type=StrTools.getStrValue(map,"cus_type");
            if(StrTools.isNotEmpty(type)&&type.equals("action")){
                BpmContext.putVar("action",StrTools.getStrValue(map,"value").toUpperCase());
            }
            if(StrTools.getStrValue(map,"name").equals(tache_attr_id)){
                String tache_code=StrTools.getStrValue(map,"value");
                formparams.put("tache_code",tache_code);
            }
        }
        IAttrVal attrVal = AttrValFetchUtil.getAttrInstance(attr);
        if( attrVal != null){
            attrVal.init(attr, formparams);
        }
        List list=attrVal.fetch(attr);
        return list;
    }
	
	/**
	 * 属性校验
	 * 
	 * @param attr_id
	 * @param new_value
	 * @throws Exception
	 */
	@Transactional
	public String validate(String attr_id, String curr_value) throws Exception {
		SBpmTemplateAttr attr = (SBpmTemplateAttr) SBpmTemplateAttr.getDAO().findById(attr_id);
		if (attr == null){
			return null;
		}
		IAttrVal attrVal = AttrValFetchUtil.getAttrInstance(attr);
		if(attrVal == null){
			return null;
		}
		String error = attrVal.validate(attr, curr_value);
		return error;
	}

	/**
	 * 
	 * 查找流程环节
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public List getNoSegment(Map params) throws Exception{
		return BpmFlowBO.findSegment(params); 
	}

	/**
	 * 
	 * 查找流程名称
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public String getBoType(Map params) throws Exception{
		return BpmFlowBO.findBoType(params); 
	}

	/**
	 * 查找流程名称 和 目前流程
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public List getBoTypeNTaches(Map params) throws Exception{
		List back = new ArrayList();
		back.add(BpmFlowBO.findBoType(params)); 
		back.add(BpmFlowBO.findCurrentTache(params));
		return back;
	}
	
	@Transactional
	public String setVarBoTypeId(Map params) throws Exception{
		String var_bo_type_id = BpmContext.getVar("bo_type_id"); 
		if(StrTools.isEmpty(var_bo_type_id)){
			BpmContext.putVar("bo_type_id", StrTools.getStrValue(params, "bo_type_id"));
		}
		return "true";
	}

    @Transactional
    public Map findBpmFlowTache(Map params) throws Exception{
        return BpmFlowBO.findBpmBoFlowTache(params);
    }

	@Transactional
	public boolean isBpmBoFlowInst(String flow_id){
		List<IVO> list = MBpmBoFlowInst.getDAO().newQuerySQL(" flow_id = ? ").findByCond(new String[]{flow_id});
		if(list!=null && list.size()>0){
			return true;
		}
		return false;
	}

	/**
	 * 根据流程id找到对应的流程和流程关系（流程图使用）
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public List getTacheRelaListByBoTypeId(Map params) throws Exception{
		List TacheRelaList = BpmFlowBO.getTacheRelaListByBoTypeId(params);
		return TacheRelaList;
	}
    
	@Transactional
	@ContextMeta(cls = IContext.DwrWebContext)
	public String getBpmWoTaskRespContent(Map params)  {
		String boId = (String) params.get("boId");
		String boTypeId = (String) params.get("boTypeId");
		String tacheCode = (String) params.get("tacheCode");
		
		String respContent = "";
		String sql = "";
		List list = null;
		if(StrTools.isNotEmpty(boId) && StrTools.isNotEmpty(boTypeId) && StrTools.isNotEmpty(tacheCode) ){
			sql = " select b.wo_id,b.wo_state,b.state_date,b.bo_id,b.resp_result,b.resp_content  from bpm_wo_task b where b.bo_id = ?   " +
			" and b.tache_code = (select a.tache_code from bpm_bo_flow_tache a where a.bo_type_id =? and a.seq_no = ( " +
			" select t.seq_no-1 from bpm_bo_flow_tache  t where t.bo_type_id =? and t.tache_code =? )) order by b.state_date desc  ";
			list = DAO.queryForMap(sql, boId,boTypeId,boTypeId,tacheCode);
			if(!list.isEmpty() && list.size()>0){
				HashMap map  = (HashMap)list.get(0);
				respContent = (String)map.get("resp_content");
			}
		}
		
		return respContent;
	}
	
	@Transactional
	public boolean isFirstTache(Map<String,String> params) {
		String bo_type_id = params.get("bo_type_id");
		String tacheCode = params.get("tache_code");
		SBpmBoFlowDef bpmBoFlowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
		return bpmBoFlowDef.isFirstTache(tacheCode);
	}
	
	/**
	 * @throws Exception *
	 * 
	* @Description: 审批不通过的时候能够选择的环节，默认是第一个环节跟上一个环节 
	* @params 
	* @return  
	* @date  2015-4-16
	* @author zhang.yongwei
	* @throws
	 */
	@Transactional
	@ContextMeta(cls = IContext.DwrWebContext)
	public List getFailTacheList(Map<String,String> params) throws Exception {
		String flow_id = params.get("flow_id");
		String wo_id = params.get("wo_id");
		String bo_type_id = params.get("bo_type_id");
		String tacheCode = params.get("tache_code");
		
		SBpmBoFlowDef bpmBoFlowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
		MsgBean msg = new MsgBean();
		msg.setFlowId(flow_id);
		msg.setWoId(wo_id);
		msg.setBoTypeId(bo_type_id);
		msg.setParam(params);
		msg.setMsgAction(MsgConsts.ACTION_WO_FAIL);
		bpmBoFlowDef.setMsg(msg);
		
		List result = new ArrayList();
		SBpmBoFlowTache begin = bpmBoFlowDef.getBeginTahce();//第一个环节
		if(tacheCode.equals(begin.tache_code)){
			return result;
		}
		SBpmBoFlowTache prevTache = bpmBoFlowDef.getPrevTache(tacheCode);//bpmBoFlowDef.getPrevTacheByInst(tacheCode, false);//上一个规格环节
		//MBpmWoTask prevInstTacheTask = bpmBoFlowDef.getPrevWoTaskByInst(tacheCode, tacheCode);//上一个实例环节(比如内部转派)
		//SBpmBoFlowTache prevInstTache = null;
		/*if(prevInstTacheTask != null){
			prevInstTache = bpmBoFlowDef.translateTaskToTache(prevInstTacheTask);
		}*/
		if(begin != null){
			result.add(begin.saveToMap());
		}
		if(prevTache != null && bpmBoFlowDef.isExitInInst(prevTache.tache_code)){
			if(begin != null && begin.tache_code.equals(prevTache.tache_code)){
				return result;
			}
			result.add(prevTache.saveToMap());
		}
		return result;
	}
	
}
