package com.ztesoft.inf.common;

import java.util.Map;

public interface ICountAccuService {

	/**
	 * 数据申请次数+1
	 * @param m
	 */
	void countDataApply(Map m);

	/**
	 * 数据分发次数+1
	 * @param m
	 */
	void countDataDispatch(Map m);

	/**
	 * 数据查询次数+1
	 * @param m
	 */
	void countDataQuery(Map m);

	/**
	 * 任务申请次数+1
	 * @param m
	 */
	void countTaskApply(Map m);

	/**
	 * 任务调度次数+1
	 * @param m
	 */
	void countTaskSchedule(Map m);

}
