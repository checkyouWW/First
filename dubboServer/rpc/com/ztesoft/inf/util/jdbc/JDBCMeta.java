package com.ztesoft.inf.util.jdbc;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ztesoft.inf.util.jdbc.annotation.ColumnTransRule;
import com.ztesoft.inf.util.jdbc.annotation.JDBCAdapater;
import com.ztesoft.inf.util.jdbc.annotation.JdbcDriver;

public class JDBCMeta {

	private String jdbcConnStr = null; 
	private String driverClass = null;
	private Map<String,String> transMap = null;
	private Connection conn = null;
	private String user = null;
	private String password = null;
	private String isExistsTableSql = null;
	private boolean isSucConn = false;
	
	private Logger log = Logger.getLogger(JDBCMeta.class);
	
	public JDBCMeta(String jdbcConnection,String usr,String pwd){
		this(jdbcConnection);
		this.user = usr;
		this.password = pwd;
	}
	
	public JDBCMeta(String jdbcConnection) {
		this.jdbcConnStr = jdbcConnection;
		
		this.transMap = new HashMap<String,String>();
		String dbType = this.getDBType();
		if(this.isEmpty(dbType)) throw new RuntimeException("无法获取数据库类型");
		//获取需要转换的元数据信息
		try {
			Class<?> adapaterClass = Class.forName("com.ztesoft.inf.util.jdbc.adapter."+dbType+"Adapter");
			//获取判断表是否存在的sql
			isExistsTableSql = (String)adapaterClass.getField("IS_EXISTS_TABLE_SQL").get(adapaterClass);
			//获取驱动类
			Annotation[]  annotations = adapaterClass.getAnnotations();
			for(Annotation annotation : annotations){
				if(JdbcDriver.class == annotation.annotationType())
					this.driverClass = ((JdbcDriver)(annotation)).value();
				if(JDBCAdapater.class == annotation.annotationType()){
					ColumnTransRule[] transRules = ((JDBCAdapater)annotation).value();
					for(ColumnTransRule ctr : transRules){
						String [] values = ctr.value();
						if(values.length!=2)
							throw new RuntimeException("转换格式错误："+adapaterClass.toString());
						this.transMap.put(values[0].toLowerCase(), values[1].toLowerCase());
					}
				}
				
			}
		} catch (Exception e) {
			throw new RuntimeException(dbType+"的适配文件未定义");
		}
	}
	
	public boolean createTable(String tableName,List<MetaColumnBean> columnList){
		this.getConnection();
		try{
			try{
				String isExistsSql = isExistsTableSql.replace("${table_code}", tableName);
				this.conn.prepareStatement(isExistsSql).executeQuery();
				return true;
			}catch(Exception e){}
		
			if(columnList==null || columnList.size() == 0) return false;
			
			StringBuilder createTable = new StringBuilder();
			createTable.append("create table ").append(tableName).append("(");
			for(int i=0;i<columnList.size();i++){
				MetaColumnBean bean = columnList.get(i);
				if(i!=0) createTable.append(",");
				createTable.append(bean.getColumnCode()).append(" ");
				String columnType = bean.getColumnType();
				String transColumnType = this.transMap.get(columnType.toLowerCase());
				if(this.isEmpty(transColumnType)) transColumnType = columnType;
				String columnLength = bean.getColumnLength();
				//columnLength非空，将transColumnType里面(后面的全部去掉
				if(this.isNotEmpty(columnLength)){
					int rightIndex = transColumnType.indexOf("(");
					if(rightIndex!=-1){
						transColumnType = transColumnType.substring(0,rightIndex);
					}
				}
				createTable.append(transColumnType);
				if(this.isNotEmpty(columnLength)){
					createTable.append("(").append(columnLength).append(")").append(" ");
				}
			}
			createTable.append(")");
			this.conn.prepareStatement(createTable.toString()).execute();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			this.closeConnection();
		}
		//执行sql辣
		
		
	}
	
	public boolean isAvailableConn(){
		try{
			this.getConnection();
			return this.isSucConn;
		}catch(Exception e){
			log.error(e);
			return false;
		}
	}
	
	private void getConnection(){
		isSucConn = false;
		try {
			Class.forName(this.driverClass);
			if(this.isNotEmpty(user))
				this.conn = DriverManager.getConnection(this.jdbcConnStr,this.user,this.password);
			else
				this.conn = DriverManager.getConnection(this.jdbcConnStr);
			isSucConn = true;
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e);
			isSucConn = false;
		}
		
	}
	
	private void closeConnection(){
		if(this.conn == null) return;
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getDBType(){
		if(this.isEmpty(this.jdbcConnStr))
			throw new RuntimeException("jdbc连接串为空");
		String lowerConnStr = this.jdbcConnStr.toLowerCase().trim();
		return
				lowerConnStr.indexOf("jdbc:mysql")==0?	"Mysql" :
				lowerConnStr.indexOf("jdbc:oracle")==0?	"Oracle":
				lowerConnStr.indexOf("jdbc:sybase")==0? "Sybase":
				"";
	}
	
	private boolean isEmpty(String str){
		if(str == null || "".equals(str)) return true;
		return false;
	}
	
	private boolean isNotEmpty(String str){
		return !this.isEmpty(str);
	}
	
	
	public static void main(String[] args) {
		JDBCMeta jdbcMeta = new JDBCMeta("jdbc:sybase:Tds:10.45.47.18:2638/asiqdemo","dba","sql");
		boolean bool = jdbcMeta.isAvailableConn();
		System.out.println(bool);
	}
	
}
