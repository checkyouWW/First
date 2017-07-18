package spring.util;

import java.sql.Connection;
/**
 * 支持多数据源查询仿XA方式
 * @author liu.yuming  by 2014-03-07
 */
public interface IXAConnMgr {
	/**
	 * 获取一个默认数据源的数据库连接
	 * @return
	 */
	public Connection getConnection();
	/**
	 * 获取一个指定数据源名的数据库连接
	 * @param dataSource
	 * @return
	 */
	public Connection getConnection(String dataSource);
	/**
	 * 释放数据库连接
	 * @param connection
	 */
	public void freeConnection(Connection connection);
	/**
	 * 释放当前会话所有数据库连接
	 */
    public void closeConnections();
	/**
	 * 设置当前数据库连接，用来统一提交事务、释放资源。
	 * @param dataSource
	 * @param connection
	 */
	void setConnection(String dataSource, Connection connection);
	/**
	 * 提交并关闭连接
	 */
	public void commitAndClose();
	/**
	 * 回滚并关闭连接
	 */
	public void rollbackAndClose();
	/**
	 * 设置连接只读属性
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly);
	
}
