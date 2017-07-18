package com.ztesoft.dubbo.se.data.dao;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.dubbo.mp.data.vo.DataSynOrderItemLog;
import com.ztesoft.dubbo.se.data.vo.SServiceInstAlert;
import com.ztesoft.dubbo.se.data.vo.SServiceLog;
import com.ztesoft.sql.Sql;

import appfrm.resource.dao.impl.DAO;

@Repository
public class LoggerDao {
	
	public void writeMonitor(Map<String,Object> params){
		String serviceOrderId = Const.getStrValue(params, "service_order_id");
		String alertType = Const.getStrValue(params, "alert_type");
		//根据serviceOrderId和alertType尝试获取唯一的告警ID
		String alertId = DAO.querySingleValue(Sql.S_DATA_SQLS.get("get_alert_id"), new String[]{serviceOrderId,alertType});
		//如果记录没被插入
		if(StringUtils.isEmpty(alertId)){
			//新增一条新的
			SServiceInstAlert newAlert = new SServiceInstAlert();
			newAlert.readFromMap(params);
			newAlert.start_time = DateUtil.getFormatedDateTime();
			newAlert.end_time = newAlert.start_time;
			newAlert.duration = "0";
			//if(StringUtils.isEmpty(newAlert.inst_id))
			newAlert.inst_id = serviceOrderId;
			//插入sql
			newAlert.getDao().insert(newAlert);
			//得到最新的alertId
			alertId = DAO.querySingleValue(Sql.S_DATA_SQLS.get("get_alert_id"), new String[]{serviceOrderId,alertType});
			//alertId仍然为空（此情况在正常逻辑下不会存在），直接抛运行时异常
			if(StringUtils.isEmpty(alertId))
				throw new RuntimeException("新记录无法合法生成...或新记录生成后的数据与预期不符");
			
		}
		//如果记录已经存在
		else{
			//记录存在，更新end_time,duration,details_msg三个字段
			SServiceInstAlert updateAlert = new SServiceInstAlert();
			updateAlert = (SServiceInstAlert) updateAlert.getDao().findById(alertId);
			String detailsMsg = Const.getStrValue(params, "details_msg");
			
			//逻辑上不会存在
			if(updateAlert==null)
				throw new RuntimeException("记录在使用过程中被外部删除，无法执行日志写入的操作");
			updateAlert.set("end_time", DateUtil.getFormatedDateTime());
			if(StringUtils.isNotEmpty(detailsMsg))
				updateAlert.set("details_msg", detailsMsg);
			
			try {
				SServiceInstAlert.getDAO().updateParmamFieldsByIdSQL(updateAlert.updateFieldSet.toArray(new String[]{})).update(updateAlert);
				//更新时间
				DAO.update(Sql.S_DATA_SQLS.get("update_alert_duration_time"), new String[]{alertId});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writeServiceLog(Map<String,Object> params){
		
		String serviceOrderId = Const.getStrValue(params, "service_order_id");
		
		SServiceLog slog = new SServiceLog();
		slog.readFromMap(params);
		slog.service_order_id = serviceOrderId;
		slog.log_time = DateUtil.getFormatedDateTime();
		slog.getDao().insert(slog);
		
	}
	
	public void writeSynLog(Map<String,Object> params){
		String order_item_id = Const.getStrValue(params, "order_item_id");
		
		DataSynOrderItemLog log = new DataSynOrderItemLog();
		log.readFromMap(params);
		log.log_id = SeqUtil.getSeq(DataSynOrderItemLog.TABLE_CODE, DataSynOrderItemLog.PK_ID);
		log.order_item_id = order_item_id;
		log.create_date = DateUtil.getFormatedDateTime();
		log.exec_date = DateUtil.getFormatedDateTime();
		log.getDao().insert(log);
	}
	
}
