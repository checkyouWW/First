package spring.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import spring.simple.MySimpleJdbcTemplate;
import appfrm.app.DAOSystemException;

import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.CrmParamsConfig;
import comx.order.inf.IContext;
import comx.order.inf.XAConnContext;

import exception.CoopException;

/**
 * Description :<br>
 * Copyright 2010 ztesoft<br>
 * Author : zhou.jundi<br>
 * Date : 2012-7-17<br>
 * 
 * Last Modified :<br>
 * Modified by :<br>
 * Version : 1.0<br>
 */
public class DBUtil {

	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static Connection getConnection() {
		return IContext.getContext().getConnection();
	}

	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static Connection getConnection(String dataSource) {
		if(StringUtils.isEmpty(dataSource))
			return IContext.getContext().getConnection();
//			return getConnection();

		return IContext.getContext().getConnection(dataSource);
//		return DataSourceUtils.getConnection((DataSource) SpringContextUtil
//				.getBean(dataSource));
	}

	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static DataSource getDataSource() {
		String paramValue = CrmParamsConfig.getInstance().getParamValue("DEFAULT_JNDI_ID");
		paramValue = paramValue == null ? "default_datasource" : paramValue;
		return (DataSource) SpringContextUtil.getBean(paramValue);
	}

	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static MySimpleJdbcTemplate getSimpleQuery() {
		String paramValue = CrmParamsConfig.getInstance().getParamValue("DEFAULT_JNDI_ID");
		//paramValue = paramValue == null ? "default_datasource" : paramValue;
		paramValue = paramValue == null ? "dataSource" : paramValue;
		return (MySimpleJdbcTemplate) SpringContextUtil
				.getBean(paramValue+"_template");
	}
	
	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static MySimpleJdbcTemplate getSimpleQuery(String dataSource) {
		return (MySimpleJdbcTemplate) SpringContextUtil
				.getBean(dataSource+"_template");
	}

	/**
	 * 
	 * @return Author by zhou.jundi
	 */
	public static JdbcTemplate getJdbcTemplate() {
		return (JdbcTemplate) SpringContextUtil.getBean("jdbcTemplate");
	}
	/**
	 * liu.yuming 2014-03-08
	 * @return
	 */
	public static Connection getXAConnection() {
		IContext ctx = IContext.getContext();
		if(ctx instanceof XAConnContext){
			return ctx.getConnection();
		}
		throw new CoopException(CoopException.ERROR, "获取上下文错误 comx.order.inf.XAConnContext",null);
	}
	/**
	 * liu.yuming 2014-03-08
	 * @return
	 */
	public static Connection getXAConnection(String dataSource) {
		IContext ctx = IContext.getContext();
		if(ctx instanceof XAConnContext){
			return ctx.getConnection(dataSource);
		}
		throw new CoopException(CoopException.ERROR, "获取上下文出错comx.order.inf.XAConnContext",null);
	}
	public static String querySingleValueByXA(String sql, String param, String dataSource) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		String returnValue = "";

		try {
			conn = getXAConnection(dataSource);

			stmt = conn.prepareStatement(sql);

			if (param != null) {
				stmt.setString(1, param);
			}

			result = stmt.executeQuery();
			if (result.next()) {
				returnValue = DAOUtils.trimStr(result.getString(1));
			}
		} catch (Exception se) {
			throw new DAOSystemException("SQLException while execSQL:" + sql + "\n", se);
		} finally {
			DAOUtils.closeResultSet(result);
			DAOUtils.closeStatement(stmt);

		}
		return returnValue;
	}
}
