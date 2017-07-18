package com.ztesoft.inf.mp.sys;

import java.util.List;
import java.util.Map;

/**
 * 登录
 */
@SuppressWarnings({ "rawtypes" })
public interface ILoginService {

	/**
	 * 1. 登录，成功基本信息放入redis
	 */
	public Map loginPost(Map params);
	
	/**
	 * 2. 根据登录人ID，获取登录人菜单
	 * @return
	 */
	public List getStaffMenu(Map params);
	
	/**
	 * 3. 根据进入页面，获取页面按钮权限
	 * @param params
	 * @return
	 */
	public List<?> getPageBtnPrivilege(Map params);
	
	/**
	 * 4. 登出
	 * @param params
	 * @return
	 */
	public Map logout(Map params);

	Map decryptUserData(Map userData);

}
