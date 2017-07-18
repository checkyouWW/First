package com.ztesoft.dubbo.mp.data.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.data.dao.AlgorithmDao;
import com.ztesoft.inf.mp.data.IAlgorithmMgrService;
import com.ztesoft.inf.util.RpcPageModel;


@Service("algorithmService")
@SuppressWarnings("rawtypes")
public class AlgorithmService implements IAlgorithmMgrService {

	@Resource
	private AlgorithmDao dao;
	
	@Override
	@Transactional
	public RpcPageModel getAlgorithmPage(Map m) {
		return dao.getAlgorithmPage(m);
	}
	
	@Override
	@Transactional
	public RpcPageModel getFieldPage(Map m) {
		return dao.getFieldPage(m);
	}
	
	@Override
	@Transactional
	public Map addAlgorithm(Map m) {
		return dao.addAlgorithm(m);
	}
	
	@Override
	@Transactional
	public Map addField(Map m) {
		return dao.addField(m);
	}
	
//	@Override
//	@Transactional
//	public Map updateAlgorithm(Map m) {
//		return dao.updateAlgorithm(m);
//	}
	
//	@Override
//	@Transactional
//	public Map updateField(Map m) {
//		return dao.updateField(m);
//	}
	
	@Override
	@Transactional
	public Map upAlgorithm(Map m) {
		return dao.upAlgorithm(m);
	}
	
	@Override
	@Transactional
	public Map upField(Map m) {
		return dao.upField(m);
	}
	
	@Override
	@Transactional
	public Map downAlgorithm(Map m) {
		return dao.downAlgorithm(m);
	}
	
	@Override
	@Transactional
	public Map downField(Map m) {
		return dao.downField(m);
	}
	
	@Override
	@Transactional
	public Map deleteAlgorithm(Map m) {
		return dao.deleteAlgorithm(m);
	}
	
	@Override
	@Transactional
	public Map deleteFiled(Map m) {
		return dao.deleteFiled(m);
	}
}
