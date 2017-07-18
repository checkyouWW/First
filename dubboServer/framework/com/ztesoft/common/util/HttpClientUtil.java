package com.ztesoft.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.reflect.TypeToken;
import com.ztesoft.inf.util.JsonUtil;

@SuppressWarnings("unchecked")
public class HttpClientUtil {
	
	private int socket_timeout = 10 * 1000;//秒
	private int connect_timeout = 2 * 1000;//秒
	private List<Header> headers = new ArrayList<Header>();
	
	public HttpClientUtil(){
		//DEFAULT
	}
	
	public HttpClientUtil(int socket_timeout, int connect_timeout){
		this.socket_timeout = socket_timeout;
		this.connect_timeout = connect_timeout;
	}
	
	public HttpClientUtil(int socket_timeout, int connect_timeout, List<Header> headers){
		this(socket_timeout, connect_timeout);
		this.headers = headers;
	}
	
	public <T> T postJson(Map<String, Object> param, TypeToken<T> typeToken, String url) throws Exception {
		String result = null;
		T retObj = null;
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        
        RequestConfig requestConfig = RequestConfig.custom()
        		.setSocketTimeout(socket_timeout)
        		.setConnectTimeout(connect_timeout).build();//设置请求和传输超时时间
       
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .evictIdleConnections(5L, TimeUnit.SECONDS)
                .setDefaultRequestConfig(requestConfig)
                .build();
		try {
			HttpPost httpPost = new HttpPost(url);
			for(Header header: headers){
				httpPost.addHeader(header);
			}
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			
			if(param != null){
				Set<String> keys = param.keySet();
				Iterator<String> it= keys.iterator();
				while(it.hasNext()){
					String key = it.next();
					String value = (String) param.get(key);
					nvps.add(new BasicNameValuePair(key, value));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			//String str = JsonUtil.toJson(param);
			//ByteArrayEntity arrayEntity = new ByteArrayEntity(str.getBytes());
			//httpPost.setEntity(arrayEntity);
			
			CloseableHttpResponse response = httpclient.execute(httpPost);
			
			try {
				final int statusCode = response.getStatusLine().getStatusCode();
				
				if (statusCode == HttpStatus.SC_REQUEST_TIMEOUT) {
					throw new Exception("网络连接超时");
				}
				else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					HttpEntity responseEntity = response.getEntity();
					result = "服务器出错";
					if (responseEntity != null) {
						InputStream instream = responseEntity.getContent();
						result = readString(instream);
						instream.close();
					}
					throw new Exception(result);
				}
				else if (statusCode != HttpStatus.SC_OK
						&& statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new Exception("HttpStatusCode:" + statusCode);
				}
				
				HttpEntity responseEntity = response.getEntity();
				
				if (responseEntity != null) {
					InputStream instream = responseEntity.getContent();
					result = readString(instream);
					
					instream.close();
					retObj = (T) result;
					
					if (typeToken != null){
						retObj = JsonUtil.fromJson(result, typeToken);
					}

					if (typeToken != null && retObj == null) {
						retObj = (T) JsonUtil.fromJson(result, typeToken.getRawType());
					}
				}
				
				// and ensure it is fully consumed
				EntityUtils.consume(responseEntity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		
		return retObj;
	}
	
	public static String readString(InputStream in) throws Exception {
		StringBuilder ret = new StringBuilder();

		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				"utf-8"));
		String str = null;
		while ((str = br.readLine()) != null) {
			ret.append(str);
		}
		return ret.toString();
	}

	public static void main(String[] args) {
		HttpClientUtil http = new HttpClientUtil();
		try {
			Map param = new HashMap();
			TypeToken<Map> token = new TypeToken<Map>(){};
			String url = "http://www.baidu.com";
			Map test = http.postJson(param, token, url);
			System.out.println(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public int getSocket_timeout() {
		return socket_timeout;
	}

	public void setSocket_timeout(int socket_timeout) {
		this.socket_timeout = socket_timeout;
	}

	public int getConnect_timeout() {
		return connect_timeout;
	}

	public void setConnect_timeout(int connect_timeout) {
		this.connect_timeout = connect_timeout;
	}
}
