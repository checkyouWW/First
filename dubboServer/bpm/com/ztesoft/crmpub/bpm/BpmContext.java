/**
 * 流程引擎上下文类，用于设置数据库连接、以及存储流程过程信息的线程变量
 */
package com.ztesoft.crmpub.bpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author major
 *
 */
public class BpmContext {
	
	private static ThreadLocal<HashMap> bpmVarsThread = new ThreadLocal<HashMap>();
	
	public static String getVar(String key) {
		HashMap map = bpmVarsThread.get();
		
		if (map ==null){
			return null;
		}
		
		return (String) map.get(key);
	}

	public static void putVar(String key, String value) {
		HashMap map = bpmVarsThread.get();
		
		if (map ==null){
			bpmVarsThread.set(new HashMap());
			map = bpmVarsThread.get();
		}
		map.put(key, value);
	}
	
	public static List getListVar(String key) {
        HashMap map = bpmVarsThread.get();

        if (map ==null){
            return null;
        }

        return (ArrayList) map.get(key);
    }

    public static void putListVar(String key, List value) {
        HashMap map = bpmVarsThread.get();

        if (map ==null){
            bpmVarsThread.set(new HashMap());
            map = bpmVarsThread.get();
        }
        map.put(key, value);
    }

}
