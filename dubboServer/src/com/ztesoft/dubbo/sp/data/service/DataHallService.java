package com.ztesoft.dubbo.sp.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.springframework.transaction.annotation.Transactional;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.dubbo.common.AttrService;
import com.ztesoft.dubbo.se.data.dao.DataScheduleDao;
import com.ztesoft.inf.sp.data.IDataHallService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

import appfrm.resource.dao.impl.DAO;
import spring.util.SpringContextUtil;
/**
 * 
* @ClassName: DataHallService 
* @Description: 数据大厅相关接口 
* @author chenminghua
* @date 2016年9月8日 下午4:25:17 
*
 */
public class DataHallService implements IDataHallService {
	
	@Resource
	private AttrService attrService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	@Override
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月8日 下午4:28:25 
	* @Title: gatDataHallDataList 
	* @Description: 获取数据大厅 右边数据列表 
	* @param @param
	*  pageSize 分页大小；
	*  pageNo 页码，
	*  catalogId 选择的数据能力目录   -1位全部    ；
	*  orderColumnNo 排序列表编号   1 为apply_count ; 2: dispatch_count ; 3:query_count
	*  orderType 排序策略    DESC  ASC
	*  seachStr  搜索输入的 字符串
	*  
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object gatDataHallDataList(Map params){
		
		int catalogId =-1;
		String orderColumn ="";
		int orderColumnNo=0;
		String orderType ="DESC";
		int pageNo;
		int pageSize;
		
		String seachStr ="";
		//String seachStrType ="";
		
		String seachSQL="";
		
		String catalogSQl="";
		
		Map rereturnMat = new HashMap();
		
		String total;

		try{
			//staff_id = StringUtil.getStrValue(params, "dubbo_staff_id");
			pageSize=Integer.parseInt(StringUtil.getStrValue(params, "pageSize"));
			pageNo=Integer.parseInt(StringUtil.getStrValue(params, "pageNo"));
			
			catalogId = Integer.parseInt(StringUtil.getStrValue(params, "catalogId"));
			orderColumnNo = Integer.parseInt(StringUtil.getStrValue(params, "orderColumnNo"));
			orderType = StringUtil.getStrValue(params, "orderType");
			
			seachStr  = StringUtil.getStrValue(params, "seachStr");
			//seachStrType  = StringUtil.getStrValue(params, "seachStrType");

		}catch (Exception e) {
			// TODO: handle exception
			rereturnMat.put("status", false);
			rereturnMat.put("info", "获取数据时 参数有误");
			return rereturnMat;
		}
		if(orderColumnNo==1){
			orderColumn = "apply_count";
		}else if(orderColumnNo==2){
			orderColumn = "dispatch_count";
		}
		else if(orderColumnNo==3){
			orderColumn = "query_count";
		}
		else{
			rereturnMat.put("status", false);
			rereturnMat.put("info", "获取数据时 参数有误");
			return rereturnMat;
		}
		
		if(!orderType.equals("DESC")){
			orderType = "ASC";
		}
		
		if(seachStr.length()!=0/*||seachStrType.length()!=0*/){
			seachSQL = " AND (c.data_name like ?   escape '/'"
					+ " OR c.data_code like ?   escape '/')";
			seachStr = seachStr.replaceAll("/", "//");
			seachStr = seachStr.replaceAll("_", "/_");
			seachStr = seachStr.replaceAll("%", "/%");
			
			//seachStr = seachStr.replaceAll("[", "/[");
			/*seachStr = seachStr.replaceAll("^", "/^");
			seachStr = seachStr.replaceAll("{", "/{");
			seachStr = seachStr.replaceAll(".", "/.");
			seachStr = seachStr.replaceAll("+", "/+");
			seachStr = seachStr.replaceAll("?", "/?");
			seachStr = seachStr.replaceAll("$", "/$");
			seachStr = seachStr.replaceAll("*", "/*");*/
		}
		if(catalogId!=-1){
			catalogSQl = " AND catalog_id= "+ catalogId;
		}
		
		String sql="SELECT a.service_name,a.service_id,a.catalog_id,"
				+ " a.apply_count ,a.dispatch_count ,a.query_count,"
				+ " b.src_sys_code as sys_id,b.src_sys_code  as sys_name,b.extract_freq ,c.data_code,c.data_name,c.ability_id,c.comments"
				+ " FROM c_data_service a,c_data_src b,c_data_ability c "
				+ " WHERE  a.service_id=b.service_id"
				+ " AND c.service_id = a.service_id "
				+ " AND a.state='00A' "
				+ seachSQL 
				+ catalogSQl
				+ " ORDER BY "+orderColumn +" "+orderType
				+ " limit "+((pageNo-1) *pageSize)+"," +pageSize;
		
		String countSql="SELECT count(a.service_id)"
				+ " FROM c_data_service a,c_data_src b,c_data_ability c "
				+ " WHERE  a.service_id=b.service_id"
				+ " AND c.service_id = a.service_id "
				+ " AND a.state='00A' "
				+ seachSQL 
				+ catalogSQl;
		List list =null;
		String rseachStr="%"+seachStr+"%";
		if(seachStr.length()!=0/*||seachStrType.length()!=0*/){
			list =DAO.queryForMap(sql, new String[]{rseachStr,rseachStr});
			total = DAO.querySingleValue(countSql, new String[]{rseachStr,rseachStr});
			
		}else{
			list =DAO.queryForMap(sql, new String[]{});
			total = DAO.querySingleValue(countSql, new String[]{});
		}
		
		StringBuffer attr_value_sql = new StringBuffer("select dav.attr_value_desc "
				+ " from dc_attribute da,dc_attr_value dav  "
				+ " where da.attr_id = dav.attr_id and "
				+ "da.attr_code = 'EXTRACT_FREQ' and dav.attr_value=? ");
		if(!ListUtil.isEmpty(list)) {
			for(int i=0; i<list.size(); i++) {
				Map t = (Map) list.get(i);
				String extract_freq = StringUtil.getStrValue(t, "extract_freq");
				String attr_value_desc = DAO.querySingleValue(attr_value_sql.toString(), new String[]{extract_freq});
				t.put("extractFreq_name", attr_value_desc);
			}
		}
		
		rereturnMat.put("status", true);
		rereturnMat.put("data", list);
		rereturnMat.put("total", total);
		
		return rereturnMat;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	@Override
	public Object getDataSample(Map params){
		
		String sql="";
		String serviceId;
		Map rereturnMat = new HashMap();
		try{
			serviceId = StringUtil.getStrValue(params, "serviceId");
			

		}catch (Exception e) {
			// TODO: handle exception
			rereturnMat.put("status", false);
			rereturnMat.put("info", "获取数据时 参数有误");
			return rereturnMat;
		}
		
		sql ="select a.sample_id ,b.col_id,b.column_code,"
				+ " b.column_name,b.column_value"
				+ " FROM c_data_sample a,c_data_sample_col b"
				+ " WHERE a.sample_id=b.sample_id"
				+ " AND service_id=?";
		List list = DAO.queryForMap(sql, serviceId);	
		rereturnMat.put("status", true);
		rereturnMat.put("data", list);
		return rereturnMat;
	}
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月13日 上午11:15:41 
	* @Title: getDetailData 
	* @Description: 获取数据服务详情 
	* @param @param serviceId 数据服务id
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	@Override
	public Object getDetailData(Map params){
	
		String serviceId ;
		Map rereturnMat = new HashMap();
		
		List list1; 
		List list2;
		List synWhereList = new ArrayList();
		List disWhereList = new ArrayList();
		Map srcDetails = null;
		try{
			serviceId = StringUtil.getStrValue(params, "serviceId");
			

		}catch (Exception e) {
			rereturnMat.put("status", false);
			rereturnMat.put("info", "获取数据时 参数有误");
			return rereturnMat;
		}
		
		String sql ="select * from c_data_ability where service_id=?";
		String sql2 = Sql.C_DATA_SQLS.get("get_detail_column_list");	//modified by liangzijian
		String sql3 = Sql.C_DATA_SQLS.get("get_data_src_details");
		
		try{
			list1 = DAO.queryForMap(sql, serviceId);
			//获取翻译值进行翻译
			if(list1!=null && list1.size()>0){
				Map tempMap = (Map) list1.get(0);
				String firstDivision = Const.getStrValue(tempMap, "first_division");
				String secondDivision = Const.getStrValue(tempMap, "second_division");
				//获取账期的翻译值
				if(StringUtil.isNotEmpty(firstDivision))
					tempMap.put("first_division_text", this.getTransValue(firstDivision,KeyValues.FIRST_DIVISION_CODE));
				if(StringUtil.isNotEmpty(secondDivision))
					tempMap.put("second_division_text", this.getTransValue(secondDivision,KeyValues.SECOND_DIVISION_CODE));
				//获取抽取时间的翻译值
				String beginDispathTime = Const.getStrValue(tempMap, "begin_dispath_time");
				String endDispathTime = Const.getStrValue(tempMap, "end_dispath_time");
				String extractFreq = DAO.querySingleValue(Sql.C_DATA_SQLS.get("get_extract_freq"), new String[]{serviceId});
				if(StringUtil.isNotEmpty(beginDispathTime))
					tempMap.put("begin_dispath_time_text", this.getTransValue(beginDispathTime,KeyValues.DISPATH_TIME_CODE_PRE+extractFreq));
				if(StringUtil.isNotEmpty(endDispathTime))
					tempMap.put("end_dispath_time_text", this.getTransValue(endDispathTime,KeyValues.DISPATH_TIME_CODE_PRE+extractFreq));
				 
				String ability_id = Const.getStrValue(tempMap, "ability_id");
				String lan_sql = "select lan_id,lan_name from c_data_lan where ability_id = ?";
				List lanList = DAO.queryForMap(lan_sql, ability_id);
				rereturnMat.put("lanList", lanList);
			}
			list2 = DAO.queryForMap(sql2, serviceId);
			List list3 = DAO.queryForMap(sql3, serviceId);
			if(list3!=null && list3.size()>0) {
				srcDetails = (Map) list3.get(0);
				srcDetails.put("extract_freq_text", this.getTransValue(Const.getStrValue(srcDetails, "extract_freq"), KeyValues.EXTRACT_FREQ_CODE));
			}
			
			DataScheduleDao dao = (DataScheduleDao) SpringContextUtil.getBean("dataScheduleDao");
			synWhereList = dao.getColumnWhereList(serviceId, KeyValues.WHERE_TYPE_SYN);
			disWhereList = dao.getColumnWhereList(serviceId, KeyValues.WHERE_TYPE_DISPATCH);
		}catch (Exception e) {
			rereturnMat.put("status", false);
			rereturnMat.put("info", "参数错误或者数据库异常");
			return rereturnMat;
		}
		rereturnMat.put("status", true);
		rereturnMat.put("ablityData", list1);
		rereturnMat.put("columnData", list2);
		rereturnMat.put("srcData", srcDetails);
		rereturnMat.put("synWhereList", synWhereList);
		rereturnMat.put("disWhereList", disWhereList);
		return rereturnMat;
	}

	@Transactional
	@Override
	public Map getDataSourceById(Map params) {
		String id = MapUtils.getString(params, "id");
		String sql = Sql.C_DATA_SQLS.get("select_data_src");
		List<Map> l = DAO.queryForMap(sql + " and cds.service_id = ?", id);
		return l.get(0);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getTransValue(String value,String code){
		List<Map> transList = this.attrService.getStaticAttr(code);
		if(transList==null || transList.size() == 0) return value;
		for(Map transMap : transList){
			if(Const.getStrValue(transMap, "attr_value").equals(value)) 
				return Const.getStrValue(transMap, "attr_value_name");
		}
		return value;
	}
}
