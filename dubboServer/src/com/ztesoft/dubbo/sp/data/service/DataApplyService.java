package com.ztesoft.dubbo.sp.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.sp.data.dao.DataApplyDao;
import com.ztesoft.inf.mp.sys.IFtpService;
import com.ztesoft.inf.sp.data.IDataApplyService;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.inf.util.ftp.bean.FtpBean;

import spring.util.SpringContextUtil;

/**
 * 数据申请服务接口
 * @author lwt
 *
 */
@Service
@SuppressWarnings({"rawtypes"})
public class DataApplyService implements IDataApplyService {
	
	@Resource
	private DataApplyDao dao;
	
	/**
	 * 保存申请数据
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public Map saveApply(Map params) throws Exception {
		Map result = dao.saveApply(params);
		return result;
	}
	
	@Override
	@Transactional
	public List getDataColumnList(Map params){
		return dao.getDataColumnList(params);
	}
	
	@Override
	@Transactional
	public Map getApplyData(Map params){
		return dao.getApplyData(params);
	}
	
	@Override
	@Transactional
	public RpcPageModel getDataColumnPageModel(Map params){
		return PageModelConverter.pageModelToRpc(dao.getDataColumnPageModel(params));
	}
	
	@Override
	@Transactional
	public RpcPageModel getApplyDataColumn(Map params){
		return PageModelConverter.pageModelToRpc(dao.getApplyDataColumn(params));
	}
	
	//修改数据申请（审批）
	@Override
	@Transactional
	public Map updateDataApplyInfoAudit(Map m) {
		return dao.updateDataApplyInfoAudit(m);
	}
	
	@Override
	@Transactional
	public Map getApplyCode(Map params){
		return dao.getApplyCode(params);
	}
	
	@Override
	@Transactional
	public Map validateDispatch(Map params){
		return dao.validateDispatch(params);
	}
	
	@Override
	@Transactional
	public Map updateDataApplyInfo(Map params) throws Exception{
		return dao.updateDataApplyInfo(params);
	}
	
	@Override
	@Transactional
	public Map getDispatchInfo(Map params) {
		return dao.getDispatchInfo(params);
	}
	
	@Override
	@Transactional
	public Map getInteDispatchInfo(Map m) {
		IFtpService ftpService = (IFtpService) SpringContextUtil.getBean("ftpService");
		String org_id = StringUtil.getStrValue(m, "org_id");
		FtpBean bean = ftpService.getDefOrgFtp(org_id);
		Map result = new HashMap();
		result.put("ftp_ip", bean.ip);
		result.put("ftp_ip_val", bean.ip);
		result.put("ftp_port", bean.port);
		result.put("ftp_user", bean.user);
		result.put("ftp_password", bean.password);
		result.put("ftp_def_dir", bean.path);
		return result;
	}
	
	@Override
	@Transactional
	public Map validateFtpFileName(Map params){
		return dao.validateFtpFileName(params);
	}
	@Override
	@Transactional
	public boolean canGetPassWd(Map m) {
		return dao.canGetPassWd(m);
	}
	
	@Override
	@Transactional
	public List getCanSelectedDataRange(Map param){
		return dao.getCanSelectedDataRange(param);
	}
}