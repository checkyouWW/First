package com.ztesoft.inf.mp.sys;

import java.util.Map;
/**
 * 
* @ClassName: IMainService 
* @Description: MainService的interface 
* @author chenminghua
* @date 2016年9月6日 下午1:53:43 
*
 */
@SuppressWarnings({ "rawtypes" })
public interface IMainService {
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月1日 上午10:21:53 
	* @Title: getIndicatorNum 
	* @Description: 获取关键指标数据 
	* @param @param params  空的
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getIndicatorNum(Map params);
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月1日 上午10:25:51 
	* @Title: getNotice 
	* @Description: 获取公告 
	* @param @param params   0 获取所有公告   其他数字  获取 对应的最新条目 
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getNotices(Map params);
	
	
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月2日 上午10:05:54 
	* @Title: getGiveAwayApplyCount 
	* @Description: 数据分发次数
	* @param @param params  无
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getApplyGiveAwayCount(Map params);
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月6日 上午10:19:54 
	* @Title: dataGiveAwayTopTen 
	* @Description:  任务调度次数
	* @param @param params
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getTaskScheduleCount(Map params);
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月6日 下午1:52:09 
	* @Title: getDataGeveAwayTop10 
	* @Description: 获取前10的数据分发
	* @param @param params
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getDataGeveAwayTop10(Map params);
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月6日 下午1:52:17 
	* @Title: getTaskScheduleTop10 
	* @Description:获取前10的任务调度
	* @param @param params
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getTaskScheduleTop10(Map params);
}
