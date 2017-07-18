package com.ztesoft.inf.util.jdbc;

import java.util.Comparator;

public class ColumnComparator implements Comparator<MetaColumnBean> {

	/**
	 * o1比o2大，返回-1；o1比o2小，返回1。
	 */
	@Override
	public int compare(MetaColumnBean o1, MetaColumnBean o2) {
		int i1 = Integer.parseInt(o1.getColumnSeq());
		int i2 = Integer.parseInt(o2.getColumnSeq());
		if (i1 > i2){
			return 1;
		}
		if (i1 < i2){
			return -1;
		}
		return 0;
	}
}