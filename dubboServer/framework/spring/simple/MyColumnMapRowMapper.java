/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spring.simple;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.core.CollectionFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 返回MAP的KEY为小写，VALUE为字符串
 * MyColumnMapRowMapper.java Class Name : MyColumnMapRowMapper.java<br>
 * Description :<br>
 * Copyright 2010 ztesoft<br>
 * Author : zhou.jundi<br>
 * Date : 2012-8-1<br>
 *
 * Last Modified :<br>
 * Modified by :<br>
 * Version : 1.0<br>
 */
public class MyColumnMapRowMapper implements RowMapper {
	
	private static SimpleDateFormat dateFormator = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			Object obj = getColumnValue(rs,i);
			mapOfColValues.put(key.toLowerCase(), obj);
		}
		return mapOfColValues;
	}

	/**
	 * Create a Map instance to be used as column map.
	 * <p>By default, a linked case-insensitive Map will be created if possible,
	 * else a plain HashMap (see Spring's CollectionFactory).
	 * @param columnCount the column count, to be used as initial
	 * capacity for the Map
	 * @return the new Map instance
	 * @see org.springframework.core.CollectionFactory#createLinkedCaseInsensitiveMapIfPossible
	 */
	protected Map createColumnMap(int columnCount) {
		return CollectionFactory.createLinkedCaseInsensitiveMapIfPossible(columnCount);
	}

	/**
	 * Determine the key to use for the given column in the column Map.
	 * @param columnName the column name as returned by the ResultSet
	 * @return the column key to use
	 * @see java.sql.ResultSetMetaData#getColumnName
	 */
	protected String getColumnKey(String columnName) {
		return columnName;
	}

	
	protected String getColumnValue(ResultSet rs, int index)
			throws SQLException {
		Object obj = String.valueOf(rs.getObject(index));
		String className = null;
		if (obj != null) {
			className = obj.getClass().getName();
		}
		if (obj instanceof Blob) {
			obj = new String(rs.getBytes(index));
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if (className != null
				&& ("oracle.sql.TIMESTAMP".equals(className)
						|| "oracle.sql.TIMESTAMPTZ".equals(className)
						|| "oracle.sql.DATE".equals(className) || "java.sql.Timestamp"
						.equals(className))) {
			if (rs.getTimestamp(index) == null)
				obj = "";
			else{
				obj = dateFormator.format(new java.sql.Date(rs.getTimestamp(
						index).getTime()));
			}
		}
		return obj.toString();
	}
	
	

}
