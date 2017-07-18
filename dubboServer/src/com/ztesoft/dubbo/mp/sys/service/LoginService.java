package com.ztesoft.dubbo.mp.sys.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.MapUtil;
import com.ztesoft.dubbo.inf.util.BDPMd5Utils;
import com.ztesoft.dubbo.inf.util.RsaEncrypt;
import com.ztesoft.dubbo.mp.sys.bo.LoginBO;
import com.ztesoft.inf.mp.sys.ILoginService;
import com.ztesoft.inf.util.JsonUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.ioc.LogicInvokerFactory;
import com.ztesoft.jedis.dao.RedisClient;

/**
 * 登录，不涉及角色切换，一次行查查登录人的所有角色对应的所有权限菜单，本系统自己的登录相关业务，外平台走SSO
 * 
 * @author zhao.jingang
 */
@Service("loginService")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class LoginService implements ILoginService{

	private LoginBO getLoginBO() {
		return LogicInvokerFactory.getInstance().getBO(LoginBO.class);
	}
	
	@Transactional
	@Override
	public Map decryptUserData(Map userData){
		String staff_code = StringUtil.getStrValue(userData, "staff_code");
		String password = StringUtil.getStrValue(userData, "password");
		
		Map returnMap = new HashMap();
		returnMap.put("staff_code", staff_code);
		
		String dpwd  = RSAUtil.decrypt(password);
		password = BDPMd5Utils.encrypt(dpwd, staff_code);
		
		//查询用户
		Map resultMap = getLoginBO().getStaffByCodeAnPwd(staff_code, password);
		if (!MapUtil.isEmpty(resultMap)){
			returnMap.put("password", dpwd);
		}else{
			returnMap.put("password", "");
		}
		
		return returnMap;
			
		
	}
	
	/**
	 * 1. 登录，成功基本信息放入redis
	 */
	@Transactional
	@Override
	public Map loginPost(Map params) {
		Map resultMap = new HashMap();
		String staff_code = StringUtil.getStrValue(params, "staff_code");
		String password = StringUtil.getStrValue(params, "password");
		
		// 0.校验用户名
		// 0.1用户名校验为空，登录失败，返回错误信息
		if(StringUtil.isEmpty(staff_code)){
			resultMap.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			resultMap.put(KeyValues.MSGSIGN, KeyValues.ERRORSTR0);
			return resultMap;
		}
		// 0.2数据库中不存在改用户ID
		resultMap = getLoginBO().getStaffID(staff_code);
		if (MapUtil.isEmpty(resultMap)) {
			resultMap.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			resultMap.put(KeyValues.MSGSIGN, KeyValues.ERRORSTR0);
			return resultMap;
		}
		
		// 2.校验密码
		// 2.1 密码验证码为空，登录失败，返回错误信息
		if (StringUtil.isEmpty(password)) {
			resultMap.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			resultMap.put(KeyValues.MSGSIGN, KeyValues.ERRORSTR2);
			return resultMap;
		}
		
		//加密密码
		password = RSAUtil.decrypt(password);
		String password_rsa = password;
		password = BDPMd5Utils.encrypt(password, staff_code);
		
		// 2.2 如果查询不到用户信息，登录失败，返回错误信息，此次查询比较简单，可能需要更多的用户基本信息字段，
		resultMap = getLoginBO().getStaffByCodeAnPwd(staff_code, password);
		if (MapUtil.isEmpty(resultMap)) {
			resultMap.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			resultMap.put(KeyValues.MSGSIGN, KeyValues.ERRORSTR2);
			return resultMap;
		}
		
		// 获取用户的唯一ID
		String staff_id = StringUtil.getStrValue(resultMap, "staff_id");
		//
		setSysUserInfo(staff_id, password_rsa);
		
		// 登录成功，返回
		resultMap.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
		return resultMap;
	}

	/**
	 * 设置登录用户的相关信息到session,redis缓存，更改用户信息/权限/角色/组织等需要调用此方法
	 * @param password_rsa 
	 */
	public void setSysUserInfo(String staff_id, String password_rsa) {
		//HttpSession session = RequestContextDwr.getContext().getHttpSession();
		Map staffInfo = getLoginBO().getUserInfoById(staff_id);
		password_rsa = RsaEncrypt.encrypt(password_rsa);
		staffInfo.put("password_rsa", password_rsa);
		/* 更新团队信息 */
		getLoginBO().getTeamInfo(staffInfo);

		//session.setAttribute("LoginInfoMap", staffInfo);
		// 将登录人基本信息放入redis，供其它平台调用
		RedisClient redisClient = new RedisClient();
		String key1 = RedisKeyUtil.getSysUserInfoKey(staff_id, SessionHelper.getConsumerSessionId());
		redisClient.set(key1, JsonUtil.toJson(staffInfo));
		redisClient.expire(key1, 60 * 30); // 设置超时时间30分钟
		
		// 将登录人菜单放到redis，供其它平台调用
		List rolePrivilegs = getLoginBO().getPrivileges(staff_id);
		List menus = getLoginBO().getPrivilegeMenus(rolePrivilegs);
		List menuPri = getTreeMenu(menus);
		String key2 = RedisKeyUtil.getSysUserMenuPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		redisClient.set(key2, JsonUtil.toJson(menuPri));
		redisClient.expire(key2, 60 * 30); // 设置超时时间30分钟
		
		// 将登录人按钮权限保存到redis，供其它平台调用
		List btnPris = getLoginBO().getPageBtnPrivilege(menus, staff_id);
		String key3 = RedisKeyUtil.getSysUserBtnPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		redisClient.set(key3, JsonUtil.toJson(btnPris));
		redisClient.expire(key3, 60 * 30); // 设置超时时间30分钟
	}

	/**
	 * 2. 根据登录人ID，获取登录人菜单
	 * 
	 * @return
	 */
	@Transactional
	@Override
	public List getStaffMenu(Map params) {
		String staff_id = StringUtil.getStrValue(params, "staff_id");
		
		// 从redis获取值
		RedisClient redisClient = new RedisClient();
		
		//先获取权限列表
		List rolePrivilegs = getLoginBO().getPrivileges(staff_id);
		//再获取对应的菜单
		List menus = getLoginBO().getPrivilegeMenus(rolePrivilegs);
		
		List menuPri  = getTreeMenu(menus);
		String key2 = RedisKeyUtil.getSysUserMenuPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		redisClient.set(key2, JsonUtil.toJson(menuPri));
		redisClient.expire(key2, 60 * 30); // 设置超时时间30分钟
		
		return menuPri;
	}

	/**
	 * 3. 根据进入页面，获取页面按钮权限
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	@Override
	public List<?> getPageBtnPrivilege(Map params) {
		String staff_id = StringUtil.getStrValue(params, "staff_id");
		// 从redis获取值
		RedisClient redisClient = new RedisClient();
		String key = RedisKeyUtil.getSysUserBtnPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		String s = redisClient.get(key);
		if (s != null) {
			return JsonUtil.fromJson(s, List.class);
		}
		List rolePrivilegs = getLoginBO().getPrivileges(staff_id);
		List menus = getLoginBO().getPrivilegeMenus(rolePrivilegs);
		List btnPris = getLoginBO().getPageBtnPrivilege(menus, staff_id);
		String key3 = RedisKeyUtil.getSysUserBtnPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		
		redisClient.set(key3, JsonUtil.toJson(btnPris));
		redisClient.expire(key3, 60 * 30); // 设置超时时间30分钟
		return btnPris;
	}

	/**
	 * 4. 登出
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	@Override
	public Map logout(Map params) {
		Map resultMap = new HashMap();
		String staff_id = StringUtil.getStrValue(params, "staff_id");
		//String staff_code = SessionHelperUtil.getStaffCode();
		// 记录日志
		//addEntryLog(staff_id, staff_code, KeyValues.LOGOUT, KeyValues.SUCCESS, null);
		// 清除redis缓存数据
		RedisClient redisClient = new RedisClient();
		String key1 = RedisKeyUtil.getSysUserInfoKey(staff_id, SessionHelper.getConsumerSessionId());
		String key2 = RedisKeyUtil.getSysUserMenuPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		String key3 = RedisKeyUtil.getSysUserBtnPrivilege(staff_id, SessionHelper.getConsumerSessionId());
		redisClient.del(key1);
		redisClient.del(key2);
		redisClient.del(key3);
//		HttpSession session = RequestContextDwr.getContext().getHttpSession();
//		if (session != null) {
//			session.invalidate();
//		}
		resultMap.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
		return resultMap;
	}

	
	
	/**
	 * 组装菜单，默认2级菜单
	 * @param sys_id 
	 */
	private List getTreeMenu(List menus) {
		// 获取一级菜单
		List firstMenus = new ArrayList();
		for (int i = 0; i < menus.size(); i++) {
			Map map = (Map) menus.get(i);
			if (StringUtil.getStrValue(map, "parent_menu").equals("-1")) {
				firstMenus.add(map);
			}
		}
		Collections.sort(firstMenus, new MyComparator());
		// 获取一级菜单下的二级菜单
		for (int i = 0; i < firstMenus.size(); i++) {
			Map map1 = (Map) firstMenus.get(i);
			String menu_id = StringUtil.getStrValue(map1, "menu_id"); // 一级菜单的目录ID
			List secondMenus = new ArrayList();
			for (int j = 0; j < menus.size(); j++) {
				Map map2 = (Map) menus.get(j);
				if (StringUtil.getStrValue(map2, "parent_menu").equals(menu_id)) {
					secondMenus.add(map2);
				}
			}
			Collections.sort(secondMenus, new MyComparator());
			((Map) firstMenus.get(i)).put("secondMenus", secondMenus); // 把二级菜单放入一级菜单下
		}
		// 返回
		return firstMenus;
	}
	
	// 自定义排序类
	class MyComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			String order1 = StringUtil.getStrValue((Map) o1, "menu_order");
			String order2 = StringUtil.getStrValue((Map) o2, "menu_order");
			return Integer.parseInt(order1) - Integer.parseInt(order2);
		}
	}
	
}
