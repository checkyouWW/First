package spring.util.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import spring.util.IXAConnMgr;
import spring.util.SpringContextUtil;

import com.ztesoft.common.dao.DAOSystemException;
import com.ztesoft.common.util.CrmParamsConfig;
/**
 * @author liu.yuming  by 2014-03-07
 */
public class XAConnMgr implements IXAConnMgr {
	public static String defDataSource = CrmParamsConfig.getInstance().getParamValue("DEFAULT_JNDI_ID")
			== null ? "default_datasource" : CrmParamsConfig.getInstance().getParamValue("DEFAULT_JNDI_ID");
	protected ThreadLocal<Map<String, Connection>> connCtx = new ThreadLocal<Map<String, Connection>>();
	protected ThreadLocal<Boolean> isReadOnly = new ThreadLocal<Boolean>();
	public XAConnMgr(){
		this.setReadOnly(false);
	}
	@Override
	public void closeConnections() {
		// TODO Auto-generated method stub
		if (null == connCtx.get()) {
            return;
        }
        for (Entry<String, Connection> entry : connCtx.get().entrySet()) {
            Connection conn = entry.getValue();
            try {
                if (null != conn && conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connCtx.set(new HashMap<String, Connection>());
	}

	@Override
	public void commitAndClose() {
		// TODO Auto-generated method stub
		if (null == connCtx.get()) {
            return;
        }
        for (Entry<String, Connection> entry : connCtx.get().entrySet()) {
            Connection conn = entry.getValue();
            try {
                if (!isReadOnly.get()) {
                    conn.commit();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connCtx.set(null);
	}

	@Override
	public void freeConnection(Connection connection) {
		// TODO Auto-generated method stub

	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
//		Connection conn = DataSourceUtils.getConnection((DataSource)SpringContextUtil.getBean(defDataSource));
//		this.setConnection(defDataSource, conn);
		return this.getConnection(null);
	}

	@Override
	public Connection getConnection(String dataSource) {
		// TODO Auto-generated method stub
		Connection conn = null;
		dataSource = (dataSource == null || "".equals(dataSource)) ? defDataSource : dataSource;
		dataSource = dataSource.toLowerCase();
		if("default".equals(dataSource)){
			dataSource = "coopdb";
		}
		if(this.connCtx.get() != null && this.connCtx.get().containsKey(dataSource)){
			conn = this.connCtx.get().get(dataSource);
			try{
				if(conn != null && !conn.isClosed()){
					return conn;
				}
				if(conn != null && conn.isClosed()){
					this.connCtx.get().remove(dataSource);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DAOSystemException("获取数据源错误dataSource="+dataSource+"close");
			}
		}
		conn = DataSourceUtils.getConnection((DataSource)SpringContextUtil.getBean(dataSource));
		this.setConnection(dataSource, conn);
		return conn;
	}

	@Override
	public void rollbackAndClose() {
		// TODO Auto-generated method stub
		if (null == connCtx.get()) {
            return;
        }
        for (Entry<String, Connection> entry : connCtx.get().entrySet()) {
            Connection conn = entry.getValue();
            try {
                if (!isReadOnly.get()) {
                    conn.rollback();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connCtx.set(null);
	}

	@Override
	public void setConnection(String dataSource, Connection connection) {
		// TODO Auto-generated method stub
    	//connCtx.set(null);
    	if (null == connCtx.get()) {
             connCtx.set(new HashMap<String, Connection>());
         }
        connCtx.get().put(dataSource, connection);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		// TODO Auto-generated method stub
		if (readOnly && null == isReadOnly.get()) {
            isReadOnly.set(true);
        } else {
            isReadOnly.set(readOnly);
        }
	}

}
