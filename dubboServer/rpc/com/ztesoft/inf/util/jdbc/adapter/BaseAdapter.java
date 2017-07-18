package com.ztesoft.inf.util.jdbc.adapter;

public interface BaseAdapter {

	String IS_EXISTS_TABLE_SQL = "select count(*) from ${table_code}";
	
}
