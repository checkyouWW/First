package com.ztesoft.inf.sp.data;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
/**
 * 
* @ClassName: IDataHallService 
* @Description: 数据大厅相关接口 的interface
* @author chenminghua
* @date 2016年9月8日 下午4:27:28 
*
 */
public interface IDataHallService {
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月8日 下午4:28:25 
	* @Title: gatDataHallDataList 
	* @Description: 获取数据大厅 右边数据列表 
	* @param @param params
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object gatDataHallDataList(Map params);
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月9日 下午2:22:46 
	* @Title: getDataSample 
	* @Description: 查询样例 
	* @param @param params
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getDataSample(Map params);
	
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
	public Object getDetailData(Map params);

	@Transactional
	Map getDataSourceById(Map params);
}
