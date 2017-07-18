
package com.ztesoft.dubbo.sp.workspace.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.sp.workspace.dao.DataUsedApplyDao;
import com.ztesoft.inf.sp.workspace.IDataUsedApplyService;
import com.ztesoft.inf.util.RpcPageModel;


@Service("dataUsedApplyService")
@SuppressWarnings("rawtypes")
public class DataUsedApplyService implements IDataUsedApplyService {

	@Resource
	private DataUsedApplyDao dao;
	
	@Override
	@Transactional
	public RpcPageModel getApplyPage(Map m) {
		return dao.getApplyPage(m);
	}

	@Override
	@Transactional
	public Map saveDataUsedApply(Map m) throws Exception {
		return dao.saveDataUsedApply(m);
	}
	
	@Override
	@Transactional
	public Map getDataUsedApplyInfo(Map m) {
		return dao.getDataUsedApplyInfo(m);
	}
	
	//更新安全数据使用申请信息（审批）
	@Override
	@Transactional
	public Map updateDataUsedApplyInfo(Map m) {
		return dao.updateDataUsedApplyInfo(m);
	}

	@Override
	@Transactional
	public RpcPageModel getApplyPageDis(Map m) {
		return dao.getApplyPageDis(m);
	}
	
	@Override
	@Transactional
	public RpcPageModel getDataInstApplyList(Map m) {
		return dao.getDataInstApplyList(m);
	}
	
	@Override
	@Transactional
	public List getDataInstDataColumn(Map m) {
		return dao.getDataInstDataColumn(m);
	}
	
}
