package com.ztesoft.inf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 类说明: 分页信息的封装类，记录了当前页面信息和当前页的数据。
 *
 * Copyright ? 2006 zte Co.Ltd.
 * All rights reserved.
 * @author fjy
 * @version 1.0
 * @since 2006-3-7
 * @modified  by  fjy  2006-3-7
 */
public class RpcPageModel implements Serializable {
	
	private static final long serialVersionUID = -542925730640364944L;
	//总记录数
	private int totalCount;
	//当前页索引
	private int pageIndex;
	//总页数
	private int pageCount;
	//每页的记录行数
	private int pageSize;
	//当前页数据的对象列表
	private List list;
	//用于存放统计数据的LIST
	private List sumInfoList;
	
	private Map extInfo = new HashMap();
	
	public void addExtProp(String key,String value){
		extInfo.put(key, value);
	}
	
	public void removeExtProp(String key){
		extInfo.remove(key);
	}

	/**
	 * 初始化是空的list。
	 *
	 * PageModel.java构建器
	 */
	public RpcPageModel(){
		this.totalCount = 0;
		this.pageIndex = 1;
		this.pageCount = 1;
		this.pageSize = 10;
		this.list = new ArrayList();
		this.sumInfoList = new ArrayList();
	}

	public List getSumInfoList() {
		return sumInfoList;
	}

	public void setSumInfoList(List sumInfoList) {
		this.sumInfoList = sumInfoList;
	}
	
	public List getRows() {
		return list;
	}

	public void setRows(List list) {
		this.list = list;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public int getTotal() {
		return totalCount;
	}
	public void setTotal(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
