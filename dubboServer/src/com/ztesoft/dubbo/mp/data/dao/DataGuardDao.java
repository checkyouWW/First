package com.ztesoft.dubbo.mp.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

@Repository
@SuppressWarnings({"unchecked","rawtypes" })
public class DataGuardDao {

	public RpcPageModel getDataGuards(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		
		String service_type = MapUtils.getString(m, "service_type");
		//告警类型
		String type = MapUtils.getString(m, "type");
		String dataName = MapUtils.getString(m, "data_name");
		String startDate = MapUtils.getString(m, "start_date");
		String endDate = MapUtils.getString(m, "end_date");
		String sql = Sql.C_DATA_SQLS.get("select_data_alerts");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(service_type)) {
			sql += " and sia.service_type = ? ";
			pList.add(service_type);
		}
		
		if(StringUtils.isNotBlank(type)) {
			sql += " and sia.alert_type = ? ";
			pList.add(type);
		}
		
		if(StringUtils.isNotBlank(dataName)) {
			sql += " and cda.data_name like ? ";
			pList.add("%"+dataName+"%");
		}
		
		if(StringUtils.isNotBlank(startDate)) {
			sql += " and sia.start_time > ? ";
			pList.add(startDate);
		}
		
		if(StringUtils.isNotBlank(endDate)) {
			sql += " and sia.end_time < ? ";
			pList.add(endDate);
		}
		
		PageModel p = DAO.queryForPageModel(sql, ps, pi, pList.toArray(new String[]{}));
		
		return PageModelConverter.pageModelToRpc(p);
	}

	public Map getDataGuardInfo(Map m) {
		String alertId = MapUtils.getString(m, "alert_id");
		String sql = Sql.C_DATA_SQLS.get("select_data_alerts_simple");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(alertId)) {
			sql += " and alert_id = ? ";
			pList.add(alertId);
		}
		List<Map> list = DAO.queryForMap(sql, pList.toArray(new String[]{}));
		if(ListUtil.isEmpty(list) || list.size()>1) {
			return null;
		}
		return list.get(0);
	}
	
	public void saveServiceInstAlert(Map m) {
		Map res = new HashMap();
		String instId = MapUtils.getString(m, "inst_id");
		String alertType = MapUtils.getString(m, "alert_type");
		String startTime = MapUtils.getString(m, "start_time");
		String endTime = MapUtils.getString(m, "end_time");
		String duration = MapUtils.getString(m, "duration");
		String detailsMsg = MapUtils.getString(m, "details_msg");
		String serviceType = MapUtils.getString(m, "service_type");
		String serviceId = MapUtils.getString(m, "service_id");
		String sql = Sql.C_DATA_SQLS.get("insert_data_alerts");
		DAO.update(sql, new String[]{instId,alertType,startTime,endTime,duration,detailsMsg,serviceType,serviceId});
	}
}
