package comx.order.inf;

import java.sql.Connection;
import java.util.Map;

import spring.util.IXAConnMgr;
import spring.util.impl.XAConnMgr;
import exception.CoopException;

public class XAConnContext extends IContext {
	private IXAConnMgr xaMgr = null;
	/**
	 * 设置数据库连接在XA模式下只读还是可读写
	 * @param target ：false可读写；true只读
	 */
	public void setReadony(boolean readOnly){
		this.xaMgr.setReadOnly(readOnly);
	}
	
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		this.initSession();
	}

	@Override
	public void initSession() throws Exception {
		// TODO Auto-generated method stub
		if(xaMgr == null){
			this.xaMgr = new XAConnMgr();
			this.xaMgr.setReadOnly(false);
		}
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return this.xaMgr.getConnection();
	}

	@Override
	public Connection getConnection(String dataSource) {
		// TODO Auto-generated method stub
		return this.xaMgr.getConnection(dataSource);
	}
	@Override
	public void commit() throws Exception {
		// TODO Auto-generated method stub
		this.xaMgr.commitAndClose();
	}
	@Override
	public Object rollback(Throwable ex) {
		// TODO Auto-generated method stub
		this.xaMgr.rollbackAndClose();
		return null;
	}

	@Override
	public void addError(String errCode, String message, String... params) {
		throw new CoopException(CoopException.ERROR, message,null);
	}

	@Override
	public void addHint(String hintCode, String message, String... params) {
		throw new CoopException(CoopException.INFO, message,null);
	}


	@Override
	public Map getClientData() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getDynaSQLS(String dynaClazz, String sqlName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setClientData(Map clientData) {
		// TODO Auto-generated method stub
		
	}

}
