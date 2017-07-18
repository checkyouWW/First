package com.ztesoft.inf.se.data;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.inf.util.ftp.bean.FtpBean;

/**
 * 数据服务调度服务提供类
 * @author lwt
 *
 */
@SuppressWarnings("rawtypes")
public interface IDataScheduleService {
	
	/**
	 * 根据服务类型计算工单的生成时间
	 * @param service_type
	 * @throws Exception
	 */
	public void calCreateOrderTime(String service_type) throws Exception;
	
	public List getDataServiceInsts(Map params);

	public Map createServiceOrder(Map params);
	
	public Map updateServiceOrder(String service_order_id, Map changes);
	
	public List getServiceOrders(String state, String service_type);
	
	public Map getSDataInst(Map order);

	/**
	 * 判断ODS和EDW消息表是否就绪
	 * @param dataInst
	 * @return
	 */
	public boolean messageReady(Map dataInst);

	public Map getDispatchData(String dataInstId);
	
	/**
	 * 获取租户信息
	 * @param order
	 * @return
	 */
	public Map getTenantInfo(Map order);
	
	/**
	 * 解密
	 * @param pwd
	 * @return
	 */
	public String decryptPwd(String pwd);

	/**
	 * 获取视图名
	 * @param serviceOrderId
	 * @return
	 */
	public String getViewName(String serviceOrderId);

	/**
	 * 更新dataInst表的创建时间
	 * @param serviceOrderId
	 * @param newCreateTime
	 * @return
	 */
	public void updateCreateOrderTime(String serviceOrderId, String newCreateTime);
	
	/**
	 * 获取团队对应的库编码
	 * @param org_id
	 * @return
	 */
	public String getOrgSchema(String org_id);
	
	/**
	 * data_inst_id 获取data_columns
	 */
	public List<Map> getDataColumns(Map m);

	public List<Map> getDataColumnList(String service_order_id);

	Map getExtractTypeMap(String dataInstId);

	/**
	 * 获取工单列表
	 * @param m
	 * @return
	 */
	RpcPageModel getServiceOrderPage(Map m);


	Map getSDataInstByServiceOrderId(String serviceOrderId);


	/**
	 * 获取工单日志
	 * @param m
	 * @return
	 */
	RpcPageModel getServiceOrderLog(Map m);

	/**
	 * 视图授权给团队负责人
	 * @param map
	 * @param order 
	 */
	public Map viewPrivToDirector(Map map, Map order);

	public String getDataServiceFileName(String strValue);

	Map getAcctColumnMap(String serviceOrderId);

	Map getLanColumnData(String service_order_id);
	
	/**
	 * 获取默认接口机ftp
	 * @return
	 */
	public FtpBean getDefInfFtp();

	void addDispatchLog(String dataInstId, String dispatchType,
			String filePath, String dispatchState,String dispatchId);

	List<Map> getCleanFtpFileList();

	public void updateCleanFtpLogState(String logId,
			String newstate);
	
	/**
	 * 获取公共的schemaCode
	 */
	public String getPublicSchemaCode();

	public boolean isStaticTable(Map dataInst);
	
	public List getColumnWhereList(String service_id, String where_type);
	
	public String getColumnWhereValue(Map serviceOrder, String expression);
	
	public String getColumnWhereValueByItem(Map synOrderItem, String expression);
}