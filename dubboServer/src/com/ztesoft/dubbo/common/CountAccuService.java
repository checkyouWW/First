package com.ztesoft.dubbo.common;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.common.ICountAccuService;

@Service("countAccuService")
@SuppressWarnings("rawtypes")
public class CountAccuService implements ICountAccuService {
	
	@Override
	@Transactional
	public void countDataApply(Map m) {
		String serviceId = Const.getStrValue(m, "service_id");
		String sql = "update c_data_service set apply_count=apply_count+1 where service_id = ? ";
		DAO.update(sql, new String[]{serviceId});
	}
	
	@Override
	@Transactional
	public void countDataDispatch(Map m) {
		String serviceId = Const.getStrValue(m, "service_id");
		String sql = "update c_data_service set dispatch_count=dispatch_count+1 where service_id = ? ";
		DAO.update(sql, new String[]{serviceId});
	}
	
	@Override
	@Transactional
	public void countDataQuery(Map m) {
		int catalogId =-1;
		String seachSQL="";
		String catalogSQl="";
		String seachStr = "";
		try{
			seachStr = Const.getStrValue(m, "seachStr");
			catalogId = Integer.parseInt(StringUtil.getStrValue(m, "catalogId"));
		}catch (Exception e) {
			//TODO
		}
		if(StringUtils.isBlank(seachStr)) {
			return;
		}
		seachSQL = " AND (c.data_name like ? "
				+ " OR c.data_code like ? )";
			
		if(catalogId!=-1){
			catalogSQl = " AND catalog_id= "+ catalogId;
		}
		
		String sql="update c_data_service cds set cds.query_count=cds.query_count+1 where cds.service_id in ("
				+ " select t.service_id from ("
				+ " SELECT a.service_id "
				+ " FROM c_data_service a,c_data_src b,c_data_ability c,meta_system d"
				+ " WHERE  a.service_id=b.service_id"
				+ " AND c.service_id = a.service_id "
				+ " AND b.src_sys_code=d.sys_code"
				+ " AND a.state='00A' "
				+ seachSQL 
				+ catalogSQl
				+ ")t"
				+ ")";
		
		DAO.update(sql, new String[]{"%"+seachStr+"%","%"+seachStr+"%"});
	}
	
	@Override
	@Transactional
	public void countTaskApply(Map m) {
		String serviceId = Const.getStrValue(m, "service_id");
		String sql = "update c_task_service set apply_count=apply_count+1 where service_id = ? ";
		DAO.update(sql, new String[]{serviceId});
	}
	
	@Override
	@Transactional
	public void countTaskSchedule(Map m) {
		String serviceId = Const.getStrValue(m, "service_id");
		String sql = "update c_task_service set schedule_count=schedule_count+1 where service_id = ? ";
		DAO.update(sql, new String[]{serviceId});
	}
}
