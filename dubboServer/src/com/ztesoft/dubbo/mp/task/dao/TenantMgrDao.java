package com.ztesoft.dubbo.mp.task.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import appfrm.app.util.SeqUtil;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.dubbo.mp.task.vo.TenantInfo;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

@Repository
@SuppressWarnings({ "rawtypes","unchecked" })
@Deprecated
public class TenantMgrDao {

	@Deprecated
	public PageModel getTenantList(Map params){
		int pagesize = 10;
		int page = 1;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){};
		
		String sql = Sql.C_TASK_SQLS.get("get_tenant_list");
		return DAO.queryForPageModel(sql, pagesize, page, new String[]{});
		
	}
	
	@Deprecated
	public void updateTenantState(String tenantId,String newState){
		String sql = Sql.C_TASK_SQLS.get("update_renant_state");
		DAO.update(sql, new String[]{newState,tenantId});
	}

	@Deprecated
	public List getTenantGroupList(Map params) {
		String sql = Sql.C_TASK_SQLS.get("get_tenantgroup_list");
		return DAO.queryForMap(sql, new String[]{});
	}

	@Deprecated
	public PageModel getTeamList(Map params) {
		int pagesize = 10;
		int page = 1;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){};
		String sql = Sql.C_TASK_SQLS.get("get_team_list");
		return DAO.queryForPageModel(sql, pagesize, page, new String[]{});
	}

	@Deprecated
	public Map addTenant(Map params) {
		
		TenantInfo cti = new TenantInfo();
		cti.readFromMap(params);
		cti.state = KeyValues.STATE_00B;
		cti.create_time = cti.state_time = DateUtil.getFormatedDateTime();
		cti.tenant_code = SeqUtil.getInst().getNext(TenantInfo.TABLE_CODE, TenantInfo.PK_ID);
		
		cti.getDao().insert(cti);
		
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
		
	}
	
}
