package com.ztesoft.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static  String unCapital(String methodName) {
		char[] resChars =  methodName.toCharArray();
		if(resChars.length >= 1){
				resChars[0] = methodName.substring(0, 1).toLowerCase().charAt(0);
		}
		return new String(resChars);
	}
	
	public static  boolean hasArrValue(String[] valArr,String value) {
		if(valArr.length==0||value==null||"".equals(value)){
			return false;
		}
		for(String val:valArr){
			if(value.equals(val)){
				return true;
			}
		}
		return false;
	}
	
	// 锟斤拷一锟斤拷锟斤拷母转锟斤拷为锟斤拷写
	public static  String capital(String methodName) {
		char[] resChars =  methodName.toCharArray();
		if(resChars.length >= 1){
				resChars[0] = methodName.substring(0, 1).toUpperCase().charAt(0);
		}
		return new String(resChars);
	}
	public static boolean notNull(String s){
		if(s==null||"".equals(s)){
			return false;
		}else{
			return true;
		}
	}
	public static void  main(String [] args){
		String methodName="addPKCompInst";
		
		String fieldName = StringUtil.unCapital(methodName.substring(3));
		// wumingchao: 去锟斤拷锟斤拷锟揭伙拷锟絪
		//fieldName = fieldName.substring(0, fieldName.length() - 1);
		System.out.println(fieldName);
	}
	
	
	/**
     * 锟斤拷16锟斤拷锟狡憋拷示锟斤拷锟絙yte锟斤拷锟斤拷
     * @param b byte[]
     * @return String
     */
    public static String bytes2HexString(byte[] b) {
        String hexStr = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            hexStr += hex;
        }
        return hexStr.toUpperCase();
    }

	public static String join(String seperator, String[] strings) {
		if(strings==null)
			return "";
		int length = strings.length;
		if (length == 0)
			return "";
		StringBuffer buf = new StringBuffer(length * strings[0].length()).append(strings[0]);
		for (int i = 1; i < length; i++) {
			buf.append(seperator).append(strings[i]);
		}
		return buf.toString();
	}

	public static String join(String seperator, Iterator objects) {
		StringBuffer buf = new StringBuffer();
		if (objects.hasNext())
			buf.append(objects.next());
		while (objects.hasNext()) {
			buf.append(seperator).append(objects.next());
		}
		return buf.toString();
	}
	
	public static String joinOper(String seperator, Iterator objects,String oper) {
		StringBuffer buf = new StringBuffer();
		if (objects.hasNext())
			objects.next();
			buf.append(oper);
		while (objects.hasNext()) {
			objects.next();
			buf.append(seperator).append(oper);
		}
		return buf.toString();
	}
	
	
	public static String joinWithQMarks(String seperator, String[] strings) {
		
		List list=Arrays.asList(strings);
		return joinWithQMarks(seperator,list.iterator());
	}
	
	public static String joinWithQMarks(String seperator, Iterator objects) {
		StringBuffer buf = new StringBuffer();
		if (objects.hasNext())
			buf.append("'").append(objects.next()).append("'");
		while (objects.hasNext()) {
			buf.append(seperator).append("'").append(objects.next()).append("'");
		}
		return buf.toString();
	}

	public static boolean booleanValue(String tfString) {
		String trimmed = tfString.trim().toLowerCase();
		return trimmed.equals("true") || trimmed.equals("t");
	}
	
	public static boolean isEqual(String o, boolean c){
		
		if(o==null||"".equals(o))
			return false;
		return o.equals(String.valueOf(c).toLowerCase());
		
	}
	
	public static boolean isEqual(String o, String c){
		if(isEmpty(o)){
			o = ""; 
		}
		if(isEmpty(c)){
			c = ""; 
		} 
		return o.equals(c); 
	}

	public static String toString(Object[] array) {
		int len = array.length;
		if (len == 0)
			return "";
		StringBuffer buf = new StringBuffer(len * 12);
		for (int i = 0; i < len - 1; i++) {
			buf.append(array[i]).append(", ");
		}
		return buf.append(array[len - 1]).toString();
	}

	public static String[] toArray(List list) {
		int len = list.size();
		if (len == 0)
			return null;
		String[] array = new String[len];
		for (int i = 0; i < len; i++) {
			array[i] = list.get(i).toString();
		}
		return array;
	}

	public static boolean isNotEmpty(String string) {
		return string != null && string.length() > 0;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static String truncate(String string, int length) {
		if (string.length() <= length) {
			return string;
		} else {
			return string.substring(0, length);
		}
	}

	
	public static  String getStrValue(Map m , String name) {
		Object t = m.get(name) ;
		if(t == null )
			return "" ;
		return ((String)m.get(name)).trim() ;
	}
	
	public static String toUpperCase(String str) {
		return str == null ? null : str.toUpperCase();
	}

	public static String toLowerCase(String str) {
		return str == null ? null : str.toLowerCase();
	}

	public static HashMap toMap(String[] array) {

		if(array==null)
			return null;
		int len = array.length;
		
		
		if (len == 0)
			return null;
		
		HashMap map=new HashMap();
		
		for (int i = 0; i < len; i++) {
			map.put(array[i],array[i]);
		}
		return map;
		
	}/*--repl锟斤拷锟斤拷with锟芥换 -1为锟芥换锟街凤拷text锟斤拷锟斤拷锟叫碉拷repl--*/
	public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }
	/*--max为锟斤拷锟芥换锟侥革拷锟斤拷--*/
	public static String replace(String text, String repl, String with, int max) {
        if (text == null || repl==null || with == null || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

	/**
	 * 锟斤拷锟斤拷锟斤拷锟街凤拷转锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 锟斤拷锟截达拷锟斤拷歉锟�
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String max(String str1,String str2){
		int num1 = Integer.parseInt(str1);
		int num2 = Integer.parseInt(str2);
		if(num1>num2){
			return str1;
		}else{
			return str2;
		} 
	}
	
	/**
	 * 锟斤拷锟斤拷锟斤拷锟街凤拷转锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 锟斤拷锟斤拷小锟斤拷锟角革拷
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String min(String str1,String str2){
		int num1 = Integer.parseInt(str1);
		int num2 = Integer.parseInt(str2);
		if(num1>num2){
			return str2;
		}else{
			return str1;
		} 
	}
	/**
	 * 校锟斤拷锟角凤拷为锟斤拷锟斤拷
	 * */
	public static  boolean isNum(String num) {		
		
	   if(num==null ||num.equals(""))return false;
	   
	   if(num.startsWith("-")){
		   return isNum(num.substring(1));
	   }
	   
	   Pattern pattern = Pattern.compile("^\\d+$");
	   Matcher isNum = pattern.matcher(num);
	   return isNum.matches();
	}
	
	//锟斤拷止锟斤拷锟斤拷锟斤拷锟� 锟斤拷锟叫讹拷"1234,222"锟角凤拷锟�123",锟斤拷锟街憋拷锟斤拷锟絊tring.indexOf()锟斤拷锟斤拷,锟斤拷锟斤拷为锟斤拷
	//锟斤拷锟斤拷锟斤拷源锟街凤拷锟斤拷锟斤拷址锟斤拷前锟襟都硷拷锟较分革拷锟斤拷志,锟斤拷锟斤拷卸锟�,1234,222,"锟斤拷锟角凤拷锟�,123,",锟斤拷锟斤拷筒锟斤拷锟斤拷锟斤拷锟斤拷锟�
	
	/**
	* @Title: isIndexOf
	* @Description: 锟叫断帮拷锟较�
	* @param   str  源锟街凤拷
	* @param   subStr 锟斤拷锟街凤拷
	* @param   splitFlag 源锟街凤拷姆指锟斤拷锟街�
	* @return  true 锟斤拷   false  锟斤拷锟斤拷
	* @throws
	*/
	public static boolean isIndexOf(String str, String subStr, String splitFlag) {
		
		if (isEmpty(str)) {
			return false;
		}
		
		//锟斤拷锟街凤拷为锟斤拷,锟斤拷为锟斤拷
		if (isEmpty(subStr)) {
			return true;
		}
		
		if (null == splitFlag) {
			splitFlag = "";
		}
		
		String tmpStr = splitFlag+str+splitFlag;
		String tmpSubStr = splitFlag+subStr+splitFlag;
		
		return tmpStr.indexOf(tmpSubStr) >= 0;
		
	}
	
	/**
	 * 锟斤拷隆String
	 * @param warn
	 * @return
	 */
	public static Object cloneMySelf(Object object) {
		Object cloneObj = null;
		ObjectOutputStream oo = null;
		ObjectInputStream oi = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(out);
			oo.writeObject(object);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			oi = new ObjectInputStream(in);
			cloneObj = (Object) oi.readObject();
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(oo!=null){
				try {
					oo.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			if(oi!=null){
				try {
					oi.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}	
		}
		return cloneObj;
	}

	//锟斤拷一锟斤拷HashMap拼锟秸筹拷key:val|..锟侥革拷式
	public static String hashMapToString(HashMap sqlGetKeyVals){
		StringBuffer data=new StringBuffer("");
		if(sqlGetKeyVals!=null)
		for(Iterator it=sqlGetKeyVals.entrySet().iterator();it.hasNext();){
			Map.Entry map=(Map.Entry)it.next();
			String key=(String)map.getKey();
			String val=(String)map.getValue();
			data.append(key);
			data.append(":");
			data.append(val);
			data.append("|");
		}
		return data.toString();
	}
	//锟斤拷一锟斤拷key:val|..锟斤拷式锟斤拷String拼锟秸筹拷HashMap
	public static HashMap StringToHashMap(String str){
		if(str==null || str.equals(""))return null;
		String []_hashmap=str.split("\\|");
		if(_hashmap==null || _hashmap.length==0)return null;
		HashMap ret=new HashMap();
		for(int i=0;i<_hashmap.length;i++){
			String it=_hashmap[i];
			if(it==null || it.equals(""))continue;
			String []_maps=it.split(":");
			if(_maps==null || _maps.length!=2)continue;
			String key=_maps[0];
			String val=_maps[1];
			ret.put(key, val);
		}
		return ret;
	}
	
	/**
	 * 校锟斤拷锟街凤拷锟角凤拷匹锟斤拷锟斤拷锟斤拷锟斤拷式 add by xiaof
	 * */
	public static  boolean isMatche(String checkStr,String regex) {		
		
	   if(checkStr==null ||checkStr.equals(""))return false;
	   
	   
	   Pattern pattern = Pattern.compile(regex);
	   Matcher isMathe = pattern.matcher(checkStr);
	   return isMathe.matches();
	}

	public static boolean isMatcheIP(String ip) {
		String patterStr = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		return isMatche(ip,patterStr);
	}

	public static String isEmptyDefalut(String val,String defVal){
			if(isEmpty(val)){return defVal;}
			return val;
	}

	public static String safe(String val){
		if(val==null){
			return "";
		}
		return val;
	}
	
	 /**
     * 锟斤拷取锟街凤拷某锟斤拷龋锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷址锟�
     * @param str
     * @return
     */
    public static int getStrLength(String str) {
    	if(isEmpty(str)) {
    		return 0;
    	}
    	char[] charArr = str.toCharArray();
    	int charCount = 0;
    	for(int i = 0; i < charArr.length ; i++) {
    		if(isChinese(charArr[i])) {
    			charCount+= 2;
    		} else {
    			charCount++;
    		}
    	}
    	return charCount;
    }
    
    public static final boolean isChinese(char c) {
    	Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    	if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    	    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
    	    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    	    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
    	    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
    	    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
    	    return true;
    	}
    	return false;
    }
    
	public static String getPatchStr(String str, int size) {
		String returnValue = "";
		int len = size - str.length();
		for (int i = 0; i < len; i++) {
			returnValue += "0";
		}
		return returnValue;
	}
	
	 /**
	 * 锟结供删锟斤拷锟街凤拷前锟斤拷目崭锟侥癸拷锟斤拷
	 * 
	 * @return
	 */
	public static String trimStr(String str) {

		if (null == str)
			return "";
		else
			return str.trim();

	}

	public static boolean isFloat(String num) {
		if(isNum(num))return true;
		String reg="^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
		
		if(num==null ||num.equals(""))return false;
		   
		if(num.startsWith("-")){
			return isMatche(num.substring(1), reg);
		}
		return isMatche(num, reg);
	}
	
	/**
	 * 根据生成规则生成TransactionId字段
	 * @return
	 */
	public static String produceTransactionId() {
		Random random = new Random(System.currentTimeMillis());
		return String.valueOf(random.nextLong()%10000000000000L);
	}
}
