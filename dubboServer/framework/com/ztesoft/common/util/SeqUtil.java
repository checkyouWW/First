package com.ztesoft.common.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import appfrm.app.DAOSystemException;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;

/**
 * 序列工具类
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SeqUtil {

	private static String DB_TYPE = ParamsConfig.getInstance().getParamValue("DATABASE_TYPE");

	/**
	 * 跟据表名/字段获取对应的序列
	 */
	public static String getSeq(String tableCode, String fieldCode) {
		try {
			String sql = "select sequence_code from sequence_management where lower(table_code)=lower(?) and lower(field_code)=lower(?)";
			List<Map> list = DAO.queryForMap(sql, new String[] { tableCode, fieldCode });
			if (list.size() > 0) {
				String sequenceCode = MapUtils.getString(list.get(0), "sequence_code");
				synchronized (sequenceCode) {
					String seq_sql = "";
					if ("MYSQL".equalsIgnoreCase(DB_TYPE)) {
						seq_sql = "select nextval('" + sequenceCode + "') seq_value from dual";
					} else {
						seq_sql = "select " + sequenceCode + ".nextval seq_value from dual";
					}
					String seq = DBUtil.getSimpleQuery().querySingleValue(seq_sql, new String[] {});
					return StringUtils.isEmpty(seq) ? "-1" : seq;
				}
			} else {
				throw new DAOSystemException("取序列出错,不存在的序列:table_code:" + tableCode + " field_code:" + fieldCode + "!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOSystemException("取序列出错:" + e);
		}
	}

}
