package com.ztesoft.crmpub.bpm.mgr.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.util.ListUtil;
import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BpmFlowBO {
	
	@SuppressWarnings("unused")
	private static final String CLASS_B_SQLS = "com.ztesoft.crm.chn.org.sqls.B_SQLS";
	
	public static List findSegment(Map params) {
		String bo_type_id = (String)params.get("bo_type_id");
		String bo_id = (String)params.get("bo_id");
		String flow_id = (String)params.get("flow_id");
		List<IVO> vos = SBpmBoFlowTache.getDAO().query(" bo_type_id=? order by seq_no asc", bo_type_id);
		SBpmBoFlowDef bpmBoFlowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
		MsgBean msg = new MsgBean();
		msg.setBoId(bo_id);
		msg.setParam(params);
		msg.setFlowId(flow_id);
		bpmBoFlowDef.setMsg(msg);
		List<IVO> newList = new ArrayList<IVO>();
		if(vos != null && vos.size() > 0){
			for(IVO vo : vos){
				SBpmBoFlowTache bpmBoFlowTache =  (SBpmBoFlowTache) vo;
				try {
					boolean flag = bpmBoFlowDef.isSkip(bpmBoFlowTache);
					//被跳过的环节不显示
					if(flag){
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				newList.add(vo);
			}
		}
		
		return newList;
	}
	
	public static boolean isNextTacheSkip(String bo_id, String bo_type_id, String tache_code){
		SBpmBoFlowDef bpmBoFlowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
		MsgBean msg = new MsgBean();
		msg.setBoId(bo_id);
		bpmBoFlowDef.setMsg(msg);
		try {
			putLastTache(bpmBoFlowDef);
			SBpmBoFlowTache next = bpmBoFlowDef.getNextTacheBySeq(tache_code);
			if(next == null){
				BpmContext.putVar("next_tache_code", "END");
				return false;
			}
			boolean flag = bpmBoFlowDef.isSkip(next);
			if(flag){
				BpmContext.putVar("next_tache_code", "SKIP");
			} else {
				BpmContext.putVar("next_tache_code", next.tache_code);
			}
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 实际最后的环节，但不是bpm_bo_flow_tache配置的最后一个环节，因为后面的环节有可能被跳过
	 * @param bpmBoFlowDef
	 */
	private static void putLastTache(SBpmBoFlowDef bpmBoFlowDef) {
		try {
			SBpmBoFlowTache begin = bpmBoFlowDef.getBeginTahce();
			findLastTache(bpmBoFlowDef, begin.tache_code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void findLastTache(SBpmBoFlowDef bpmBoFlowDef, String curr_tache_code) throws Exception {
		SBpmBoFlowTache next = bpmBoFlowDef.getNextTache(curr_tache_code);
		if(next != null){
			findLastTache(bpmBoFlowDef, next.tache_code);
		} else {
			BpmContext.putVar("last_tache_code", curr_tache_code);
		}
	}

	public static String findBoType(Map params){
		String bo_type_id = (String)params.get("bo_type_id");
		String[] varParams = new String[1];
		varParams[0] = bo_type_id;
		String sql = "select flow_name from bpm_bo_flow_def where bo_type_id=?";
		List list = DAO.queryForMap(sql,varParams);
		Map map = (Map)list.get(0);
		String flow_name = (String)map.get("flow_name");
		return flow_name;		
	}
	
	public static Map findCurrentTache(Map params){
		String bo_id = (String)params.get("bo_id");
		String flowId = (String)params.get("flowId");
		if( StrUtil.isEmpty(bo_id) ){
			return null;
		}
		
		String[] varParams = new String[2];
		varParams[0] = bo_id;
		varParams[1] = flowId;
		String sql = "select tache_code,wo_state from bpm_wo_task where wo_state!='FAIL' and bo_id = ? and flow_id = ? order by wo_id asc";
		List list = DAO.queryForMap(sql,varParams);
		if (ListUtil.isEmpty(list)) {
			sql = "select tache_code,wo_state from l_bpm_wo_task where wo_state!='FAIL' and bo_id = ? and flow_id = ? order by wo_id asc";
			list = DAO.queryForMap(sql,varParams);
		}
		
		if( !ListUtil.isEmpty(list) ){
			Map data = new HashMap();
			for( int i=0; i<list.size(); i++ ){
				Map object = (Map)list.get(i);
				data.put(object.get("tache_code"), object);
			}
			return data;
		}else{
			return null;
		}
	}

    public static Map findBpmBoFlowTache(Map params) {
    	String bo_type_id = (String)params.get("bo_type_id");
    	String tache_code = (String)params.get("tache_code");
    	List<IVO> vos = SBpmBoFlowTache.getDAO().query(" bo_type_id=? order by seq_no asc", bo_type_id);
    	if(vos != null && vos.size() > 0){
    		for(IVO vo : vos){
    			SBpmBoFlowTache bpmBoFlowTache =  (SBpmBoFlowTache) vo;
    			if(bpmBoFlowTache.tache_code.equals(tache_code)){
    				Map result = bpmBoFlowTache.saveToMap();
    				result.put("tache_size", vos.size());
    				return result;
    			}
    		}
    	}

    	return new HashMap();
    }
    
    /**
	 * 根据流程id找到对应的流程和流程关系（流程图使用）
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List getTacheRelaListByBoTypeId(Map params){
		String bo_type_id = StrTools.getStrValue(params, "bo_type_id");

		String sql = "SELECT t.tache_code, t.tache_name, t.top, t.left, t.title, t.text FROM bpm_bo_flow_tache t WHERE t.bo_type_id = ? order by t.seq_no";
		String nextSql = 
				"SELECT n.tar_tache_code tache_code, t.tache_name, n.comments as conn_label " +
				"  FROM bpm_bo_flow_tache t, bpm_tache_route n " + 
				" WHERE t.tache_code(+) = n.tar_tache_code " + 
				"   AND t.bo_type_id(+) = n.bo_type_id " + 
				"   and n.src_tache_code = ? " + 
				"   AND n.bo_type_id = ? order by t.seq_no";
		List<Map> tacheList = DAO.queryForMap(sql, new String[]{bo_type_id});
		int top = 150;
		int left = 300;
		for(Map tacheMap : tacheList){
			String tache_code = StrTools.getStrValue(tacheMap, "tache_code");
			
			//位置
			Map position = new HashMap();
			position.put("top", StrUtil.isNotEmpty((String)tacheMap.get("top")) ? (String)tacheMap.get("top") : top+"");
			position.put("left",  StrUtil.isNotEmpty((String)tacheMap.get("left")) ? (String)tacheMap.get("left") : left+"");
			top += 120;
			tacheMap.put("position", position);
			//下一环节
			List<Map> nextList = DAO.queryForMap(nextSql, new String[]{tache_code, bo_type_id});
			if(ListUtil.isEmpty(nextList)){
				SBpmBoFlowDef bpmBoFlowDef = (SBpmBoFlowDef) SBpmBoFlowDef.getDAO().findById(bo_type_id);
				MsgBean msg = new MsgBean();
				msg.setBoId(StrTools.getStrValue(params, "bo_id"));
				bpmBoFlowDef.setMsg(msg);
				try {
					SBpmBoFlowTache next = bpmBoFlowDef.getNextTacheBySeq(tache_code);
					SBpmBoFlowTache last = bpmBoFlowDef.getLastTache();
					if(next == null && tache_code.equals(last.tache_code)){
						next = last;
						next.tache_code = "end";
					}
					if(next != null){
						Map nextMap = new HashMap();
						nextMap.put("tache_code", next.tache_code);
						nextMap.put("tache_name", next.tache_name);
						if(nextList == null){
							nextList = new ArrayList();
						}
						nextList.add(nextMap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tacheMap.put("next", nextList);
		}
		
		return tacheList;
	}
	
}
