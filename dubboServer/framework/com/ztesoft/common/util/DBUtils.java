package com.ztesoft.common.util;

import com.ztesoft.common.Constants;

public class DBUtils {

	public static String DB_TYPE = ParamsConfig.getInstance().getParamValue("DATABASE_TYPE");
	
	public static String to_date(String value) {
		if (value == null || "".equals(value)) {
			return "";
		}
		String returnValue = "";
		String[] datetime = value.split(" ");

		if (datetime.length == 1) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "str_to_date('" + value + "','%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_date('" + value + "', '%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_date('" + value + "','yyyy-mm-dd')";
			}
		} else if (datetime.length == 2) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "str_to_date('" + value + "','%Y-%m-%d %T')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_date('" + value + "', '%Y-%m-%d %H:%M:%S')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_date('" + value + "','yyyy-mm-dd hh24:mi:ss')";
			}
		}

		return returnValue;
	}
	
	public static String to_date(int format) {
		String returnValue = "";
		if (format == 1) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "str_to_date(?, '%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_date(?, '%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_date(?, 'yyyy-mm-dd')";
			}
		} else if (format == 2) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "str_to_date(?, '%Y-%m-%d %T')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_date(?, '%Y-%m-%d %H:%M:%S')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_date(?, 'yyyy-mm-dd hh24:mi:ss')";
			}
		}
		return returnValue;
	}
	
	public static String to_char(String val, int format) {
		String returnValue = "";

		if (format == 1) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "date_format(" + val + ",'%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_char(" + val + ", '%Y-%m-%d')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_char(" + val + ",'yyyy-mm-dd')";
			}
		} else if (format == 2) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "date_format(" + val + ",'%Y-%m-%d %T')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_char(" + val + ", '%Y-%m-%d %H:%M:%S')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_char(" + val + ",'yyyy-mm-dd hh24:mi:ss')";
			}
		} else if (format == 3) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "date_format(" + val + ",'%Y-%m-%d %H:%i')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_char(" + val + ", '%Y-%m-%d %H:%M')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_char(" + val + ",'yyyy-mm-dd hh24:mi')";
			}
		} else if (format == 4) {
			if (DB_TYPE.equals(Constants.DB_TYPE_MYSQL)) {
				returnValue = "date_format(" + val + ",'%Y-%m-%d %H')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_INFORMIX)) {
				returnValue = "to_char(" + val + ", '%Y-%m-%d %H')";
			} else if (DB_TYPE.equals(Constants.DB_TYPE_ORACLE)) {
				returnValue = "to_char(" + val + ",'yyyy-mm-dd hh24')";
			}
		}

		return returnValue;
	}
	
}
