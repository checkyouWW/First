package com.ztesoft.crmpub.bpm.vo;

import java.io.Serializable;
/**
 * 环节工单执行人
 * @author lirx
 */
public class PerformerDto implements Serializable{
	

    public static final String TYPE_SQL_GET_STAFF="SQL_1";

    private String type;
    private String value;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
