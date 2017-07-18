package com.ztesoft.common.ssh;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author xudahu
 *
 */
public final class ObjectUtils {

	private ObjectUtils() {
		
	}

	public static final void decodeObject(Object obj, String srcEncoding,
			String destEncoding) {
		String[] props = ObjectUtils.getPropertyNames(obj);
		for (int i = 0; i < props.length; i++) {
			String name = props[i];
			Object v = ObjectUtils.getProperty(obj, name);
			if (v == null)
				continue;
			if (v instanceof String) {
				try {
					v = new String(((String) v).getBytes(srcEncoding),
							destEncoding);
					ObjectUtils.setProperty(obj, name, v);
				} catch (Exception e) {
				}
			}
		}
	}

	public static final String decodeString(String str, String srcEncoding,
			String destEncoding) {
		String returnStr = "";
		try {
			returnStr = new String(str.getBytes(srcEncoding), destEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public static final void removeProperty(Object obj, String name) {
		try {
			String[] args = name.split("[,]");
			for (int i = 0; i < args.length; i++) {
				PropertyUtils.setProperty(obj, args[i], "");
			}
		} catch (Exception e) {
		}
	}

	public static final int parseInt(String s, int df) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {

		}
		return df;
	}

	public static final double parseDouble(String s, double df) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {

		}
		return df;
	}

	public static final String format(String str, String[] args) {
		Matcher m = Pattern.compile("[{][0-9]+[}]").matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String b = m.group();
			b = b.replaceFirst("[{]", "");
			b = b.replaceFirst("[}]", "");
			int i = Integer.parseInt(b);
			if (i >= 0 && i < args.length) {
				m.appendReplacement(sb, (args[i] == null) ? "" : args[i]);
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static final String formatString(String str, String... args) {
		if(args==null || args.length==0) return str;
		Matcher m = Pattern.compile("[{][0-9]+[}]").matcher(str);
		StringBuffer sb = new StringBuffer();
		
		while (m.find()) {
			String b = m.group();
			b = b.replaceFirst("[{]", "");
			b = b.replaceFirst("[}]", "");
			int i = Integer.parseInt(b);
			if (i >= 0 && i < args.length) {
				m.appendReplacement(sb, (args[i] == null) ? "" : Matcher.quoteReplacement(args[i]));
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static final String format(String str, Object o) {
		if (ObjectUtils.isEmpty(str))
			return "";
		if (ObjectUtils.isEmpty(o))
			return str;
		Matcher m = Pattern.compile("[{][A-Za-z0-9_]+[}]").matcher(str);
		StringBuffer sb = new StringBuffer();
	 
		while (m.find()) {
			String b = m.group();
			b = b.replaceAll("[{]|[}]", "");

			m.appendReplacement(sb, ObjectUtils.getString(o, b));

		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static final boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String) {
			return "".equals(o);
		}

		if (o instanceof List) {
			return ((List<?>) o).size() == 0;
		}
		return false;
	}

	public static final boolean isEmpty(Object o, String name) {
		return ObjectUtils.isEmpty(ObjectUtils.getProperty(o, name));
	}

	public static final String customEncode(String s) {
		char[] buff = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buff.length; i++) {
			char c = buff[i];
			if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90)
					|| (c >= 97 && c <= 122)) { //���ֺ���ĸ
				sb.append(c);
			} else {
				String a = Integer.toHexString(c);
				sb.append("$");
				if (a.length() == 1)//Ū��4λ��ʾһ���ַ���
					sb.append("000");
				if (a.length() == 2)
					sb.append("00");
				if (a.length() == 3)
					sb.append("0");
				sb.append(a);
			}
		}
		buff = null;
		return sb.toString();
	}

	public static final String customDecode(String s) {
		if (ObjectUtils.isEmpty(s))
			return s;

		if (s.indexOf("$") < 0)
			return s;

		char[] buff = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buff.length; i++) {
			char c = buff[i];

			if (c == 36) {
				StringBuffer word = new StringBuffer();
				for (int n = 0; n < 4; n++) {
					word.append(buff[++i]);
				}

				sb.append((char) Integer.parseInt(word.toString(), 16));

			} else
				sb.append(c);
		}

		return sb.toString();
	}

	public static final void copy(Object dest, Object src) {
		try {
			// BeanUtils.copyProperties(dest, src);
			PropertyUtils.copyProperties(dest, src);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final Object getProperty(Object obj, String name) {
		try {
			return PropertyUtils.getProperty(obj, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String getString(Object o, String name) {
		try {

			String v = null;

			if (o instanceof Map) {
				v = (String) ((Map<?, ?>) o).get(name);
				return (v == null) ? "" : v;
			}
			
			v = BeanUtils.getProperty(o, name);
			return (v == null) ? "" : v;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return "";
	}

	public static final int getInt(Object o, String name, int d) {
		try {
			return Integer.parseInt(ObjectUtils.getString(o, name));
		} catch (Exception e) {
			return d;
		}
	}

	public static final double getDouble(Object o, String name, double d) {
		try {
			return Double.parseDouble(ObjectUtils.getString(o, name));
		} catch (Exception e) {
			return d;
		}
	}

	public static final String[] getPropertyNames(Object obj) {
		if (obj == null)
			return new String[0];
		String[] names = new String[0];
		if (obj instanceof LazyDynaBean) {
			DynaProperty[] props = ((LazyDynaBean) obj).getDynaClass()
					.getDynaProperties();
			names = new String[props.length];
			for (int i = 0; i < props.length; i++) {
				names[i] = props[i].getName();
			}
		} else {
			PropertyDescriptor[] props = PropertyUtils
					.getPropertyDescriptors(obj);
			names = new String[props.length-1];
			int n=0;
			for (int i = 0; i < props.length; i++) {
				if("class".equalsIgnoreCase(props[i].getName())) continue;
				
				names[n] = props[i].getName();
				n++;
			}
		}

		return names;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void setProperty(Object obj, String name, Object v) {
		if (ObjectUtils.isEmpty(name))
			return;
		try {
			if (obj instanceof Map) {
				((Map) obj).put(name, v);
				return;
			}
			
		 

			BeanUtils.setProperty(obj, name, v);
		} catch (Throwable e) {
		 
			//e.printStackTrace();
		}

	}

	public static final Date parse(String v, String partten) {
		if (ObjectUtils.isEmpty(partten))
			partten = "yyyy-MM-dd HH:mm:ss";
		try {
			return new SimpleDateFormat(partten).parse(v);
		} catch (Exception e) {
		}
		return new Date();
	}

	public static final String format(Date date, String partten) {
		if (ObjectUtils.isEmpty(partten))
			partten = "yyyy-MM-dd HH:mm:ss";
		try {
			return new SimpleDateFormat(partten, Locale.CHINA).format(date);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static final String ifEmpty(String src,String def){
		return (ObjectUtils.isEmpty(src))?def:src;
	}
	
	public static String replaceBlank(String str)
	{
		//\\s*|\t|\r|
	   Pattern p = Pattern.compile("\n|\t|\r");
	   Matcher m = p.matcher(str);
	   String after = m.replaceAll(""); 
	   return after;
	}
	
}
