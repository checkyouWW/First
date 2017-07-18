package com.ztesoft.dubbo.mp.data.util;

import com.ztesoft.common.util.StringUtil;

public class BooleanWrap {

	private boolean result = false;
	private String tips = "";
	private Object data = null;
	
	private static ThreadLocal<BooleanWrap> lastUseBooleanWrap =null;
	
	static{
		lastUseBooleanWrap = new ThreadLocal<BooleanWrap>();
	}
	
	public static BooleanWrap getLASTUSE(){
		return lastUseBooleanWrap.get();
	}
	
	public BooleanWrap(){
		this(false);
	}
	
	public BooleanWrap(boolean b){
		this(b,"");
	}
	
	public BooleanWrap(boolean b,String tips){
		this.result = b;
		this.tips = tips;
	}
	
	public String getTips(){
		return getTips("");
	}
	
	public String getTips(String defaultTips){
		if(StringUtil.isEmpty(tips)) return defaultTips;
		else return tips;
	}
	
	public boolean getResult(){
		lastUseBooleanWrap.set(this);
		return result;
	}
	
	public void setData(Object data){
		this.data = data;
	}
	
	public Object getData(){
		return data;
	}
	
	@Override
	public String toString() {
		return "result:"+this.result+"\t tips:"+this.tips + "\t data:"+this.getData();
	}
	
}
