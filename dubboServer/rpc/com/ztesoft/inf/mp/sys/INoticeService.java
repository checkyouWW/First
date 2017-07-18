package com.ztesoft.inf.mp.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "rawtypes" })
public interface INoticeService {

	/**
	 * 
	 * @param params  根据标题模糊查询公告
	 * @return
	 */
	public List<Map> getNoticeByTitle(Map params);
	/**
	 * 
	 * @param params  获取所有公告
	 * @return
	 */
	public List<Map> getAllNotice(Map params);
	
	public RpcPageModel queryNotice(HashMap params);
	
	public boolean saveNotice(Map params);
	
	/**
	 * 
	 * @param params  新增公告
	 * @return
	 */
	public boolean insertNotice(Map params);
	
	/**
	 * 
	 * @param params 修改公告
	 * @return
	 */
	public boolean updateNotice(Map params);
	
	/**
	 * 
	 * @param params  删除公告
	 * @return
	 */
	public boolean deleteNotice(Map params);
	/**
	 * 
	 * @param params  发布公告
	 * @return
	 */
	public boolean releaseNotice(Map params);

	/**
	 * 
	 * @param params  取消发布公告
	 * @return
	 */
	public boolean cancelReleaseNotice(Map params);
}
