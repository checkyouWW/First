package com.ztesoft.dubbo.se.data.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.data.vo.PublicSchema;
import com.ztesoft.dubbo.mp.sys.bo.FtpBO;
import com.ztesoft.dubbo.se.data.dao.DataScheduleDao;
import com.ztesoft.dubbo.se.data.vo.SDispatchLog;
import com.ztesoft.inf.se.data.IDataScheduleService;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.inf.util.ftp.bean.FtpBean;
import com.ztesoft.sql.Sql;

@Service
@SuppressWarnings({"rawtypes"})
public class DataScheduleService implements IDataScheduleService {
	
	@Resource
	private DataScheduleDao dao;
	
	@Autowired
	private FtpBO ftpBo;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void calCreateOrderTime(String service_type) throws Exception {
		dao.calCreateOrderTime(service_type);
	}
	
	@Override
	@Transactional
	public List getDataServiceInsts(Map params){
		return dao.getDataServiceInsts(params);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map createServiceOrder(Map params) {
		return dao.createServiceOrder(params);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map updateServiceOrder(String service_order_id, Map changes) {
		return dao.updateServiceOrder(service_order_id, changes);
	}
	
	@Override
	@Transactional
	public List getServiceOrders(String state, String service_type) {
		return dao.getServiceOrders(state, service_type);
	}

	@Override
	@Transactional
	public Map getSDataInst(Map params) {
		return dao.getSDataInst(params);
	}
	
	@Override
	@Transactional
	public Map getSDataInstByServiceOrderId(String serviceOrderId){
		return dao.getSDataInstByServiceOrderId(serviceOrderId);
	}

	@Override
	@Transactional
	public Map getDispatchData(String dataInstId){
		return dao.getDispatchData(dataInstId);
	}
	
	/**
	 * 判断ODS和EDW消息表是否就绪
	 */
	@Override
	@Transactional
	public boolean messageReady(Map dataInst) {
		return dao.messageReady(dataInst);
	}

	@Override
	@Transactional
	public Map getTenantInfo(Map order) {
		return dao.getTenantInfo(order);
	}

	@Override
	public String decryptPwd(String pwd) {
		return RSAUtil.decrypt(pwd);
	}
	
	@Override
	@Transactional
	public String getViewName(String serviceOrderId){
		return dao.getViewName(serviceOrderId);
	}
	
	@Override
	@Transactional
	public void updateCreateOrderTime(String serviceOrderId,String newCreateTime){
		dao.updateCreateOrderTime(serviceOrderId,newCreateTime);
	}

	/**
	 * 获取团队对应的库编码
	 * @param org_id
	 * @return
	 */
	@Override
	@Transactional
	public String getOrgSchema(String org_id) {
		String result = dao.getOrgSchema(org_id);
		return result;
	}
	
	@Override
	@Transactional
	public List<Map> getDataColumns(Map m) {
		List<Map> data_columns = dao.getDataColumns(m);
		return data_columns;
	}
	
	@Override
	@Transactional
	public Map getAcctColumnMap(String serviceOrderId){
		return dao.getAcctColumnMap(serviceOrderId);
	}

	@Override
	@Transactional
	public List<Map> getDataColumnList(String service_order_id) {
		return dao.getDataColumnList(service_order_id);
	}
	
	@Override
	@Transactional
	public Map getExtractTypeMap(String dataInstId){
		return dao.getExtractTypeMap(dataInstId);
	}
	
	@Override
	@Transactional
	public RpcPageModel getServiceOrderPage(Map m){
		return dao.getServiceOrderPage(m);
	}
	
	@Override
	@Transactional
	public RpcPageModel getServiceOrderLog(Map m) {
		return dao.getServiceOrderLog(m);
	}

	/**
	 * 视图授权给团队负责人
	 * @param map
	 * @param order 
	 */
	@Override
	@Transactional
	public Map viewPrivToDirector(Map map, Map order) {
		return dao.viewPrivToDirector(map, order);
	}

	@Override
	@Transactional
	public String getDataServiceFileName(String serviceOrderName) {
		return dao.getDataServiceFileName(serviceOrderName);
	}

	@Override
	@Transactional
	public Map getLanColumnData(String service_order_id) {
		return dao.getLanColumnData(service_order_id);
	}

	/**
	 * 获取默认接口机ftp
	 * @return
	 */
	@Override
	@Transactional
	public FtpBean getDefInfFtp() {
		FtpBean bean = ftpBo.getDefInfFtp();
		return bean;
	}
	
	/**
	 * 写分发日志
	 * @param dataInstId
	 * @param dispatchType
	 * @param filePath
	 * @param dispatchState
	 */
	@Override
	@Transactional
	public void addDispatchLog(String dataInstId,String dispatchType,String filePath,String dispatchState,String dispatchId){
		
		if(StringUtil.isEmpty(dispatchId) && StringUtil.isNotEmpty(dataInstId)){
			dispatchId = DAO.querySingleValue("select dispatch_id from s_data_dispatch where data_inst_id=?",
					new String[]{dataInstId});
		}
		
		SDispatchLog log = new SDispatchLog();
		log.create_date = DateUtil.getFormatedDateTime();
		log.dispatch_state = dispatchState;
		log.dispatch_type = dispatchType;
		log.file_path = filePath;
		log.inst_id = dataInstId;
		log.state_date = log.create_date;
		log.dispatch_id = dispatchId;
		log.getDao().insert(log);
	}
 
	@Override
	@Transactional
	public List<Map> getCleanFtpFileList(){
		
		
		return dao.getCleanFtpFileList();
		
	}

	@Override
	@Transactional
	public void updateCleanFtpLogState(String logId, String newstate) {
		this.dao.updateCleanFtpLogState(logId, newstate);
	}

	@Override
	@Transactional
	public String getPublicSchemaCode() {
		PublicSchema ps= PublicSchema.getDefPublicSchema();
		if(ps==null) 
			return "";
		else
			return ps.schema_code;
	}

	@Override
	@Transactional
	public boolean isStaticTable(Map dataInst) {
		return dao.isStaticTable(dataInst);
	}

	@Override
	@Transactional
	public List getColumnWhereList(String service_id, String where_type) {
		return dao.getColumnWhereList(service_id, where_type);
	}
	
	@Transactional
	@Override
	public String getColumnWhereValue(Map params, String expression) {
		return dao.getColumnWhereValue(params,expression);
	}
	
	@Transactional
	@Override
	public String getColumnWhereValueByItem(Map synOrderItem, String expression) {
		return dao.getColumnWhereValueBySynOrderItem(synOrderItem, expression);
	}
}