package com.ztesoft.dubbo.mp.task.service;

import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.task.bo.TaskBO;
import com.ztesoft.inf.mp.task.ITaskService;

@Service
@SuppressWarnings({ "rawtypes" })
public class TaskService implements ITaskService {
	
	@Autowired
	private TaskBO taskBO;

	/**
	 * 获取申请单号
	 */
	@Transactional
	@Override
	public String getApplyCode(Map params) {
		return taskBO.getApplyCode();
	}

	/**
	 * 保存任务申请单
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public boolean saveTask(Map params) throws Exception {
		return taskBO.saveTask(params);
	}

	/**
	 * 写依赖文件记录
	 */
	@Transactional
	@Override
	public String insertRelyFile(Map params) {
		return taskBO.insertRelyFile(params);
	}

	/**
	 * 删除依赖文件记录
	 */
	@Transactional
	@Override
	public boolean deleteRelyFile(List<Map> files) {
		return taskBO.deleteRelyFile(files);
	}
	
	/**
	 * 查询任务申请单
	 */
	@Transactional
	@Override
	public Map queryTask(Map params) {
		return taskBO.queryTask(params);
	}

	/**
	 * 修改任务申请（审批）（bdmp）
	 */
	@Transactional
	@Override
	public Map updateTaskApplyAudit(Map m) {
		return taskBO.updateTaskApplyAudit(m);
	}
	
	/**
	 * 修改任务申请（所有状态）（bdsp）
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public Map updateTaskApplyInfo(Map params) throws Exception {
		return taskBO.updateTaskApplyInfo(params);
	}
	
	/**
	 * 插入临时表数据
	 * @param params
	 * @return
	 */
	@Transactional
	@Override
	public Map insertMDataChangeNotify(Map params) {
		return taskBO.insertMDataChangeNotify(params);
	}
}
