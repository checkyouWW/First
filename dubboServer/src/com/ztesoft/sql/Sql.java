package com.ztesoft.sql;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.ztesoft.common.util.ParamsConfig;

import spring.util.SpringContextUtil;

/**
 * 封装sql容器,根据数据库类型获取不同的sql
 * @author lwt
 *
 */
public class Sql {

	public Map<String, String> sqls = null;

	public String getSql(String name) {

		return sqls.get(name);
	}

	public Sql() {

	}

	public Sql(Class clazz) {
		super();
		this.init(clazz);
	}
	
	public void init(Class clazz) {
		sqls = new HashMap<String, String>();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field f : fields) {
				f.setAccessible(true);
				sqls.put(f.getName(), (String) f.get(this));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Sql getSqlInstance(String name){
		String db_type = ParamsConfig.getInstance().getParamValue("DATABASE_TYPE");
		String bean = db_type + "_" + name;
		Sql sys = (Sql) SpringContextUtil.getBean(bean);
		if(sys.sqls == null){
			sys.init(sys.getClass());
			
			sys = (Sql) SpringContextUtil.getBean(bean);
		}
		
		return sys;
	}
	
	/**
	 * 系统模块sql
	 *
	 */
	public static class SYS_SQLS {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("SYS_SQLS").getSql(name);
			return sql;
		}
	}
	
	/**
	 * 数据服务模块sql
	 *
	 */
	public static class S_DATA_SQLS {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("S_DATA_SQLS").getSql(name);
			return sql;
		}
	}
	
	/**
	 * 数据配置模块sql
	 *
	 */
	public static class C_DATA_SQLS {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("C_DATA_SQLS").getSql(name);
			return sql;
		}
	}
	
	/**
	 * 任务服务模块sql
	 *
	 */
	public static class S_TASK_SQLS {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("S_TASK_SQLS").getSql(name);
			return sql;
		}
	}
	
	/**
	 * 任务配置模块sql
	 *
	 */
	public static class C_TASK_SQLS {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("C_TASK_SQLS").getSql(name);
			return sql;
		}
	}
	
	/**
	 * BPM流程模块sql
	 *
	 */
	public static class BPM_SQLS_LOCAL {
		
		public static String get(String name){
			String sql = Sql.getSqlInstance("BPM_SQLS_LOCAL").getSql(name);
			return sql;
		}
	}

}
