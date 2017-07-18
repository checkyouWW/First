package com.ztesoft.dubbo.mp.task.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.task.bo.TaskScheduleBO;
import com.ztesoft.inf.mp.task.ITaskScheduleService;

@Service
@SuppressWarnings("all")
public class TaskScheduleService implements ITaskScheduleService {
	
	@Autowired
	private TaskScheduleBO taskScheduleBO;

	/**
	 * 查询工单
	 */
	@Transactional
	@Override
	public Map queryServiceOrder(Map params) {
		return taskScheduleBO.queryServiceOrder(params);
	}

	/**
	 * 创建工单
	 */
	@Transactional
	@Override
	public String insertServiceOrder(Map params) {
		return taskScheduleBO.insertServiceOrder(params);
	}

	/**
	 * 更新工单
	 */
	@Transactional
	@Override
	public boolean updateServiceOrder(Map<String, String> params) {
		return taskScheduleBO.updateServiceOrder(params);
	}

}
