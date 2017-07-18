package com.ztesoft.inf.util;

import java.util.HashMap;

/**
 * 这只是一个Map，设置几个常用参数便于生成结果
 */
public class ResultMap extends HashMap<String,Object> {



    public ResultMap(){

    }

    public ResultMap success(){
        this.put("success",1);
        return this;
    }
    public ResultMap failed(){
        this.put("success",0);
        this.put("failed",1);
        return this;
    }

    public ResultMap data(Object data){
        this.put("data",data);
        return this;
    }

    public ResultMap info(String info){
        this.put("info",info);
        return this;
    }
    public ResultMap msg(String msg){
        this.put("message",msg);
        return this;
    }

    public ResultMap code(int code){
        this.put("code",code);
        return this;
    }

    public ResultMap count(int count){
        this.put("count",count);
        return this;
    }

    public ResultMap put(String key,Object value){
        if(value!=null)
            super.put(key,value);
        return this;
    }
}
