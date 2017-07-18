package com.ztesoft.common.util;

import java.util.HashMap;
import java.util.Map;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.inf.util.JsonUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

/**
 * dubbo端的"session",其实是暂存consumer的登录信息,是本身不存在session这个概念的
 * @author
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class SessionHelper {
	
	private static ThreadLocal<Map> sessionLocal = new ThreadLocal<Map>();

	private SessionHelper() {

	}

	public static void createSessionLocal(Map staffMap) {
		sessionLocal.set(staffMap);
	}

	/**
	 * 当前登录系统的人的ID
	 * 
	 * @return
	 */
	public static String getStaffId() {
		return getString("staff_id");
	}

	/**
	 * 当前登录系统的人的工号
	 * 
	 * @return
	 */
	public static String getStaffCode() {
		return getString("staff_code");
	}

	/**
	 * 工号名称
	 */
	public static String getStaffName() {
		return getString("staff_name");
	}
	
	/**
	 * 工号密码(rsa加密跟数据库dm_staff.password 的md5加密方式不一样)
	 * @return
	 */
	public static String getPasswordRsa(){
		return getString("password_rsa");
	}
	
	/**
	 * 工号对应的租户编码
	 * @return
	 */
	public static String getTenantCode(){
		return getString("tenant_code");
	}

	/**
	 * 组织ID
	 */
	public static String getOrgId() {
		return getString("org_id");
	}

	/**
	 * 组织名称
	 */
	public static String getOrgName() {
		return getString("org_name");
	}
	
	/**
	 * 本地网ID
	 */
	public static String getLanId() {
		return getString("lan_id");
	}

	/**
	 * 本地网ID
	 */
	public static void setLanId(String lan_id) {
		setString("lan_id", lan_id);
	}

	/**
	 * 团队ID
	 */
	public static String getTeamId() {
		return getString("team_id");
	}

	
	public static void setTeamId(String teamId) {
		setString("team_id", teamId);
	}

	/**
	 * 团队名称
	 */
	public static String getTeamName() {
		return getString("team_name");
	}
	
	public static void setTeamName(String teamName) {
		setString("team_name", teamName);
	}

	public static String getDefTeamId() {
		return getString("def_team_id");
	}

	public static void setDefTeamId(String teamId) {
		setString("def_team_id", teamId);
	}
	
	public static String getConsumerSessionId(){
		String consumer_session_id = (String) sessionLocal.get().get("consumer_session_id");
		return consumer_session_id;
	}
	
	/**
	 * 是否管理员
	 */
	public static boolean isManager() {
		String is_manager = getString("is_manager");
		return KeyValues.IS_MANAGER_T.equals(is_manager);
	}
	public static String getIsManager() {
		return getString("is_manager");
	}
	
	public static void setVrStaffId(String vr_staff_id) {
		setString("vr_staff_id", vr_staff_id);
	}

	public static String getVrStaffId() {
		return getString("vr_staff_id");
	}
	
	/**
	 * 是否团队负责人
	 */
	public static boolean isDirector() {
		String is_director = getString("is_director");
		return KeyValues.IS_TEAM_DIRECTOR_T.equals(is_director);
	}
	
	public static void setIsDirector(String is_director) {
		setString("is_director", is_director);
	}

	private static String getString(String key) {
		Map staff = getStaffMap();
		Object object = staff.get(key);
		if (object != null && object instanceof String) {
			String result = (String) object;
			return result;
		} else {
			return "";
		}
	}
	
	private static void setString(String key, String value) {
		String staff_id = (String) sessionLocal.get().get("staff_id");
		String consumer_session_id = getConsumerSessionId();
		if (StrTools.isEmpty(staff_id) || StrTools.isEmpty(consumer_session_id)) {
			return;
		}
		String redisKey = RedisKeyUtil.getSysUserInfoKey(staff_id, consumer_session_id);
		RedisClient redisClient = new RedisClient();
		String json = redisClient.get(redisKey);
		if (StrTools.isNotEmpty(json)) {
			Map result = JsonUtil.fromJson(json, Map.class);
			result.put(key, value);
			redisClient.set(redisKey, JsonUtil.toJson(result));
		}
	}

	public static Map getStaffMap() {
		Map result = new HashMap();
		String staff_id = (String) sessionLocal.get().get("staff_id");
		String consumer_session_id = getConsumerSessionId();
		if (StrTools.isEmpty(staff_id) || StrTools.isEmpty(consumer_session_id)) {
			return result;
		}
		String key = RedisKeyUtil.getSysUserInfoKey(staff_id, consumer_session_id);
		RedisClient redisClient = new RedisClient();
		String json = redisClient.get(key);
		if (StrTools.isNotEmpty(json)) {
			result = JsonUtil.fromJson(json, Map.class);
		}
		return result;
	}
}
