package com.ztesoft.inf.mp.sys;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.ftp.bean.FtpBean;

public interface IFtpService {

	/**
	 * 获取ftp服务器列表
	 * @param params
	 * @return
	 */
	public List getFtpList(Map params);
	
	/**
	 * 保存团队ftp关联信息
	 * @param params
	 * @return
	 */
	public Map saveOrgFtpRel(Map params);
	
	/**
	 * 获取团队默认ftp
	 * @param org_id
	 * @return
	 */
	public FtpBean getDefOrgFtp(String org_id);
	
	/**
	 * 获取任务默认ftp
	 * @param org_id
	 * @return
	 */
	public FtpBean getDefTaskFtp();
}
