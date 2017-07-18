package com.ztesoft.inf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author liang.weitong
 * 
 * JSON操作工具类，引用在MapUtil里和ListUtil里
 *
 */
public class JsonUtil {
	public static final String EMPTY_JSON = "{}";
	public static final String EMPTY_JSON_ARRAY = "[]";
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
	public static final double SINCE_VERSION_10 = 1.0d;
	public static final double SINCE_VERSION_11 = 1.1d;
	public static final double SINCE_VERSION_12 = 1.2d;
	public static final double UNTIL_VERSION_10 = SINCE_VERSION_10;
	public static final double UNTIL_VERSION_11 = SINCE_VERSION_11;
	public static final double UNTIL_VERSION_12 = SINCE_VERSION_12;

	public JsonUtil() {
		super();
	}

	public static String toJson(Object target, Type targetType, boolean isSerializeNulls, Double version, String datePattern, boolean excludesFieldsWithoutExpose) {
		if (target == null)
			return EMPTY_JSON;
		GsonBuilder builder = new GsonBuilder();
		if (isSerializeNulls)
			builder.serializeNulls();
		if (version != null)
			builder.setVersion(version.doubleValue());
		if (isBlank(datePattern))
			datePattern = DEFAULT_DATE_PATTERN;
		builder.setDateFormat(datePattern);
		if (!excludesFieldsWithoutExpose) {
			builder.excludeFieldsWithoutExposeAnnotation();
		}
		return toJson(target, targetType, builder);
	}

	public static String toJson(Object target) {
		return toJson(target, null, false, null, null, true);
	}

	public static String toJson(Object target, String datePattern) {
		return toJson(target, null, false, null, datePattern, true);
	}

	public static String toJson(Object target, Double version) {
		return toJson(target, null, false, version, null, true);
	}

	public static String toJson(Object target, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, null, null, excludesFieldsWithoutExpose);
	}

	public static String toJson(Object target, Double version, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, version, null, excludesFieldsWithoutExpose);
	}

	public static String toJson(Object target, Type targetType) {
		return toJson(target, targetType, false, null, null, true);
	}

	public static String toJson(Object target, Type targetType, Double version) {
		return toJson(target, targetType, false, version, null, true);
	}

	public static String toJson(Object target, Type targetType, boolean excludesFieldsWithoutExpose) {
		return toJson(target, targetType, false, null, null, excludesFieldsWithoutExpose);
	}

	public static String toJson(Object target, Type targetType, Double version, boolean excludesFieldsWithoutExpose) {
		return toJson(target, targetType, false, version, null, excludesFieldsWithoutExpose);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, TypeToken<T> token, String datePattern) {
		if (isBlank(json)) {
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (isBlank(datePattern)) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = builder.create();
		try {
			return (T) gson.fromJson(json, token.getType());
		} catch (Exception ex) {
			System.out.println(json + " 无法转换为 " + token.getRawType().getName() + " 对象!");
			return null;
		}
	}

	public static <T> T fromJson(String json, TypeToken<T> token) {
		return (T) fromJson(json, token, null);
	}

	public static <T> T fromJson(String json, Class<T> clazz, String datePattern) {
		if (isBlank(json)) {
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (isBlank(datePattern)) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = builder.create();
		try {
			return (T) gson.fromJson(json, clazz);
		} catch (Exception ex) {
			System.out.println(json + " 无法转换为 " + clazz.getName() + " 对象!");
			return null;
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return (T) fromJson(json, clazz, null);
	}

	public static Object parserJsonToMap(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(json);
		if (jsonElement.isJsonArray()) {
			return parserArrays(jsonElement.getAsJsonArray());
		} else if (jsonElement.isJsonNull()) {
			return null;
		} else if (jsonElement.isJsonObject()) {
			return parserJsonObjecet(jsonElement.getAsJsonObject());
		} else if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsString();
		}
		return null;
	}

	private static Map<String, Object> parserJsonObjecet(JsonObject jsonObject) {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
		for (Map.Entry<String, JsonElement> entry : set) {
			if (entry.getValue().isJsonPrimitive()) {
				map.put(entry.getKey(), entry.getValue().getAsString());
			} else if (entry.getValue().isJsonNull()) {
				map.put(entry.getKey(), "null");
			} else if (entry.getValue().isJsonArray()) {
				map.put(entry.getKey(), parserArrays(entry.getValue().getAsJsonArray()));
			} else if (entry.getValue().isJsonObject()) {
				map.put(entry.getKey(), parserJsonObjecet(entry.getValue().getAsJsonObject()));
			}
		}
		return map;
	}

	private static List<Object> parserArrays(JsonArray jsonArray) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			if (jsonElement.isJsonNull()) {
				list.add("null");
			} else if (jsonElement.isJsonArray()) {
				list.add(parserArrays(jsonElement.getAsJsonArray()));
			} else if (jsonElement.isJsonObject()) {
				list.add(parserJsonObjecet(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				list.add(jsonElement.getAsString());
			}
		}
		return list;
	}

	public static String toJson(Object target, Type targetType, GsonBuilder builder) {
		if (target == null)
			return EMPTY_JSON;
		Gson gson = null;
		if (builder == null) {
			gson = new Gson();
		} else {
			gson = builder.create();
		}
		String result = EMPTY_JSON;
		try {
			if (targetType == null) {
				result = gson.toJson(target);
			} else {
				result = gson.toJson(target, targetType);
			}
		} catch (Exception ex) {
			System.out.println("目标对象 " + target.getClass().getName() + " 转换 JSON 字符串时，发生异常！");
			if (target instanceof Collection<?> || target instanceof Iterator<?> || target instanceof Enumeration<?> || target.getClass().isArray()) {
				result = EMPTY_JSON_ARRAY;
			}
		}
		return result;
	}

	public static boolean isBlank(String str) {
		if (null == str || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String convertStreamToString(InputStream is) {
		return convertStreamToString(is, null);
	}

	public static String getSafeString(String str) {
		if (str == null)
			return "";
		return str;
	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is, Charset charset) {
		BufferedReader reader;
		if (charset == null) {
			reader = new BufferedReader(new InputStreamReader(is));
		} else {
			reader = new BufferedReader(new InputStreamReader(is, charset));
		}
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}