package com.ztesoft.dubbo.se.data.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.powerise.ibss.framework.Const;
import com.ztesoft.dubbo.se.data.dao.LoggerDao;
import com.ztesoft.dubbo.se.data.vo.ServiceOrder;
import com.ztesoft.inf.se.data.ILoggerService;

public class LoggerService implements ILoggerService {

	@Resource
	private LoggerDao dao;
	
	@Override
	@Transactional
	public boolean writeLog(Map<String,Object> params){
		
		//首先根据serviceOrderId获取一些必要的填充数据
		String serviceOrderId = Const.getStrValue(params, "service_order_id");
		ServiceOrder serviceOrder = new ServiceOrder();
		serviceOrder = (ServiceOrder) serviceOrder.getDao().findById(serviceOrderId);
		if(serviceOrder!=null){
			params.put("service_id", serviceOrder.service_id);
			params.put("service_type", serviceOrder.service_type);
		}
		
		//先写监控表
		dao.writeMonitor(params);
		
		//再写日志表
		dao.writeServiceLog(params);
		
		
		return false;
	}
	
	@Override
	@Transactional
	public boolean writeSynLog(Map<String,Object> params){
		dao.writeSynLog(params);
		return true;
	}
	
}
