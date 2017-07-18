package com.ztesoft.inf.util.jdbc.adapter;

import com.ztesoft.inf.util.jdbc.annotation.ColumnTransRule;
import com.ztesoft.inf.util.jdbc.annotation.JDBCAdapater;
import com.ztesoft.inf.util.jdbc.annotation.JdbcDriver;

@JdbcDriver("com.mysql.jdbc.Driver")
@JDBCAdapater({
	@ColumnTransRule({"String","varchar(255)"}),
	@ColumnTransRule({"Number","int"})
})
public interface MysqlAdapter extends BaseAdapter{

}
