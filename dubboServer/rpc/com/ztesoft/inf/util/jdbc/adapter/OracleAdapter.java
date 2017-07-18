package com.ztesoft.inf.util.jdbc.adapter;

import com.ztesoft.inf.util.jdbc.annotation.ColumnTransRule;
import com.ztesoft.inf.util.jdbc.annotation.JDBCAdapater;
import com.ztesoft.inf.util.jdbc.annotation.JdbcDriver;

@JdbcDriver("oracle.jdbc.driver.OracleDriver")
@JDBCAdapater({
	@ColumnTransRule({"String","varchar2(300)"}),
	@ColumnTransRule({"int","Number"}),
	@ColumnTransRule({"datetime","date"}),
	@ColumnTransRule({"timestamp","varchar2(30)"}),
	@ColumnTransRule({"数字","Number"}),
	@ColumnTransRule({"字符串","varchar2(300)"}),
	@ColumnTransRule({"日期时间","varchar2(30)"})
})
public interface OracleAdapter extends BaseAdapter{
	
}
