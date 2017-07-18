package comx.order.inf;

import java.sql.Connection;
import java.util.Map;

import spring.util.IXAConnMgr;
import spring.util.impl.XAConnMgr;
import appfrm.app.objdelta.ObjDeltaMng;

import com.ztesoft.crm.sqls.SqlsFactory;

import exception.CoopException;

/**
 * APPFRM默认处理上下文，只管理自己的ObjDeltaMng，事务由SPRING管理
 * WebContext.java Class Name : WebContext.java<br>
 * Description :<br>
 *
 * Modified by liu.yuming 2014-03-11 :需要将所有数据库连接管理起来，以备多数据源使用，确保一个事务，相同数据源只取一个连接<br>
 * Version : 1.1<br>
 */
public abstract class WebContext extends IContext {

	private IXAConnMgr xaMgr = new XAConnMgr();
	@Override
	public Object rollback(Throwable e) {

		return null;
	}

	@Override
	public Connection getConnection() {
		return this.xaMgr.getConnection();
//		return DBUtil.getConnection();
	}
	
	@Override
	public Connection getConnection(String dataSource) {
		return this.xaMgr.getConnection(dataSource);
//		return DBUtil.getConnection(dataSource);
	}

	@Override
	public void commit() throws Exception {
		ObjDeltaMng.getInstance().commit();
	}
	public void commitByService() throws Exception {
		this.xaMgr.commitAndClose();
	}

	@Override
	public void init() throws Exception {
		this.initSession();
	}

	private Map clientData;
    // 设置客户端数据, 又客户端负责维护，并且有客户端代码在多个请求直接使用，后端框架和规则都不可以访问该数据
	public void setClientData(Map clientData){
		this.clientData = clientData;
	}
	// 获取客户端数据, 例如本地化根据需要设置的预受理信息，接口请求信息等，需要在多个步骤之间共享.
	public  Map getClientData(){
		return this.clientData;
	}
	
	@Override
	public String getDynaSQLS(String dynaClazz, String sqlName) {
		return SqlsFactory.getDynaSQLS(dynaClazz).getSql(sqlName);
	}
	

	@Override
	public void addError(String errCode, String message, String... params) {
		throw new CoopException(CoopException.ERROR, message,null);
	}

	@Override
	public void addHint(String hintCode, String message, String... params) {
		throw new CoopException(CoopException.INFO, message,null);
	}
}
