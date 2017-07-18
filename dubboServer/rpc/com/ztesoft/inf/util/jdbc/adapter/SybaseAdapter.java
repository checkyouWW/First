package com.ztesoft.inf.util.jdbc.adapter;

import com.ztesoft.inf.util.jdbc.annotation.ColumnTransRule;
import com.ztesoft.inf.util.jdbc.annotation.JDBCAdapater;
import com.ztesoft.inf.util.jdbc.annotation.JdbcDriver;

@JdbcDriver("com.sybase.jdbc3.jdbc.SybDriver")
@JDBCAdapater({
	@ColumnTransRule({"String","varchar(300)"}),
	@ColumnTransRule({"timestamp","varchar(30)"}),
	@ColumnTransRule({"数字","int"}),
	@ColumnTransRule({"字符串","varchar(300)"}),
	@ColumnTransRule({"日期时间","varchar(30)"})
})
public interface SybaseAdapter extends BaseAdapter {

}
