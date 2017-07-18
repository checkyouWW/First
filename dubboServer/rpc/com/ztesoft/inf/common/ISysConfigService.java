package com.ztesoft.inf.common;

/**
 * @Description: 系统配置服务接口 </br>
 * @author： huang.shaobin</br>
 * @date： 2016年5月15日
 */
public interface ISysConfigService {

	/**
	 * 获取系统参数
	 * 
	 * @param param_code
	 * @return
	 */
	public String getSystemParamByCode(String param_code);
	
	/**
	 * 获取BDP系统参数
	 * 
	 * @param param_code
	 * @return
	 */
	public String getBdpSystemParamByCode(String param_code);

	/**
	 * @Description:加载所有系统参数到缓存 dc_system_param  </br>
	 * @author：huang.shaobin</br>
	 * @date：2016年6月6日</br>
	 */
	public void loadDcSystemParam();

}
