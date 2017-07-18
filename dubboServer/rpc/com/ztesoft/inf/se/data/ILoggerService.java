package com.ztesoft.inf.se.data;

import java.util.Map;

public interface ILoggerService {

	boolean writeLog(Map<String,Object> params);

	boolean writeSynLog(Map<String,Object> params);
}
