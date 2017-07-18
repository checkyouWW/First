package com.ztesoft.inf.util;

import java.io.InputStream;
import java.io.Serializable;

public class InputStreamWrap implements Serializable {
	
	private static final long serialVersionUID = -1066956984267473161L;
	
	private InputStream is;
	
	public InputStreamWrap(InputStream is){
		this.is = is;
	}
	
	public InputStream getInputStream(){
		return is;
	}
	
	
}
