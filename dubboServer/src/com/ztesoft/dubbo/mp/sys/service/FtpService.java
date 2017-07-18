package com.ztesoft.dubbo.mp.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.FtpBO;
import com.ztesoft.inf.mp.sys.IFtpService;
import com.ztesoft.inf.util.ftp.bean.FtpBean;

@Service
@SuppressWarnings({ "rawtypes" })
public class FtpService implements IFtpService {

	@Autowired
	private FtpBO bo;
	
	/**
	 * 获取ftp服务器列表
	 * @param params
	 * @return
	 */
	@Override
	@Transactional
	public List getFtpList(Map params) {
		List list = bo.getFtpList(params);
		return list;
	}

	/**
	 * 保存团队ftp关联信息
	 * @param params
	 * @return
	 */
	@Transactional
	@Override
	public Map saveOrgFtpRel(Map params) {
		Map result = bo.saveOrgFtpRel(params);
		return result;
	}

	/**
	 * 获取团队默认ftp
	 * @param org_id
	 * @return
	 */
	@Override
	@Transactional
	public FtpBean getDefOrgFtp(String org_id) {
		FtpBean bean = bo.getDefOrgFtp(org_id);
		return bean;
	}

	/**
	 * 获取任务默认ftp
	 * @param org_id
	 * @return
	 */
	@Override
	@Transactional
	public FtpBean getDefTaskFtp() {
		FtpBean bean = bo.getDefTaskFtp();
		return bean;
	}
	
}
