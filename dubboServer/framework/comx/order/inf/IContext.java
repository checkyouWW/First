package comx.order.inf;

import java.sql.Connection;
import java.util.Map;

import appfrm.app.context.IApp;
import appfrm.app.context.ISession;
import appfrm.app.meta.IDBFieldFactoryMySqlImpl;


/**
 * 核心版本整理
 * context接口
 * @author zhou.jundi
 *
 */
public abstract class IContext {
	
	public static final String DwrWebContext = "comx.order.inf.DwrWebContext";
	
	public static final String TplWebContext = "comx.order.inf.TplWebContext";
	
	public static final Class IDBFieldFactoryClass = IDBFieldFactoryMySqlImpl.class; 
	
	private static ThreadLocal contextWrapper = new ThreadLocal();

	public static IContext getContext()	{
		return (IContext)contextWrapper.get();
	}
	
	protected ISession session;

	public static void setContext(IContext context){
		contextWrapper.set(context);
	}
	
	public IApp getApp() {
		return IApp.getInstance();
	}

	public ISession getSession() {
		return this.session;
	}
	/**
	 * 该方法默认不实现，在XA方式时做方法重写
	 * @param readOnly
	 */
    public void setReadony(boolean readOnly){
    	
    }
	/**
	 * 初始化Session
	 * @throws Exception
	 */
	public abstract void initSession() throws Exception;
	
	
	public abstract Connection getConnection();
	
	public abstract Connection getConnection(String dataSource);

	/**
    * 初始化
    * @throws Exception
    */
    abstract public  void init() throws Exception;

    /**
     * 提交
     * @throws Exception
     */
    abstract public  void commit() throws Exception;

    /**
     * 回滚
     */
    abstract public Object rollback(Throwable ex);

    // 设置客户端数据, 又客户端负责维护，并且有客户端代码在多个请求直接使用，后端框架和规则都不可以访问该数据
    abstract public void setClientData(Map clientData);
	// 获取客户端数据, 例如本地化根据需要设置的预受理信息，接口请求信息等，需要在多个步骤之间共享.
    abstract public Map getClientData();
    
    abstract public String getDynaSQLS(String dynaClazz,String sqlName);
    
    abstract public void addError(String errCode, String message, String...params);

    abstract public void addHint(String hintCode, String message, String...params);
}
