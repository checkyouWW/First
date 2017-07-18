package com.ztesoft.dubbo.mp.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.NoticeBO;
import com.ztesoft.inf.mp.sys.INoticeService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.ioc.LogicInvokerFactory;

import appfrm.resource.dao.impl.DAO;

@Service("noticeService")
@SuppressWarnings({"rawtypes","all"})
public class NoticeService implements INoticeService{

	private NoticeBO getNoticeBO() {
		return LogicInvokerFactory.getInstance().getBO(NoticeBO.class);
	}
	
	/**
	 * 
	 * @param params  根据标题模糊查询公告
	 * @return
	 */
	@Transactional
	@Override
	public List<Map> getNoticeByTitle(Map params){
		return getNoticeBO().getNoticeByTitle(params);
	}
	/**
	 * 
	 * @param params  获取所有公告
	 * @return
	 */
	@Transactional
	public List<Map> getAllNotice(Map params){
		return getNoticeBO().getAllNotice(params);
	}
	
	/**
	 * 查询公告
	 */
	@Transactional
	public RpcPageModel queryNotice(HashMap params) {
		return getNoticeBO().queryNotice(params);
	}
	
	/**
	 * 
	 * @param params  保存公告
	 * @return
	 */
	@Transactional
	public boolean saveNotice(Map params) {
		String action_type = MapUtils.getString(params, "action_type");
		if ("A".equals(action_type)) {
			return getNoticeBO().insertNotice(params);
		} else if ("M".equals(action_type)) {
			return getNoticeBO().updateNotice(params);
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param params
	 *            新增公告
	 * @return
	 */
	@Transactional
	public boolean insertNotice(Map params) {
		return getNoticeBO().insertNotice(params);
	}

	/**
	 * 
	 * @param params
	 *            修改公告
	 * @return
	 */
	@Transactional
	public boolean updateNotice(Map params) {
		return getNoticeBO().updateNotice(params);
	}

	/**
	 * 
	 * @param params
	 *            删除公告
	 * @return
	 */
	public boolean deleteNotice(Map params) {
		return getNoticeBO().deleteNotice(params);
	}

	/**
	 * 
	 * @param params
	 *            发布公告
	 * @return
	 */
	public boolean releaseNotice(Map params) {
		params.put("notice_state", KeyValues.NOTICE_STATE_1);
		return getNoticeBO().updateNoticeState(params);
	}

	/**
	 * 
	 * @param params
	 *            取消发布公告
	 * @return
	 */
	public boolean cancelReleaseNotice(Map params) {
		params.put("notice_state", KeyValues.NOTICE_STATE_0);
		return getNoticeBO().updateNoticeState(params);
	}

}
