package com.juyoufuli.common.web.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import com.alibaba.fastjson.JSONObject;
import com.juyoufuli.common.collect.MapUtils;
import com.juyoufuli.common.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;


/**
 * HTTP客户端工具类（支持HTTPS）
 * @version 2017-3-27
 */
public class HttpClientUtils {

	private static RequestConfig requestConfig = null;

	static
	{
		// 设置超时毫秒数、获取请求超时毫秒数、传输毫秒数
		requestConfig = RequestConfig.custom().setConnectTimeout(5000)
			.setConnectionRequestTimeout(3000).setSocketTimeout(15000).build();
	}
	
	/**
	 * http的get请求
	 * @param url
	 */
	public static String get(String url) {
		return get(url, "UTF-8");
	}
	
	/**
	 * http的get请求
	 * @param url
	 */
	public static String get(String url, String charset) {
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet, charset);
	}

	/**
	 * http的get请求 重载专门发送带有header请求
	 * 增加header参数
	 * @param url
	 */
	public static String get(String url, String charset, Map<String,String> header) {
		HttpGet httpGet = new HttpGet(url);
		if (MapUtils.isNotEmpty(header)){
			Set<String> strings = header.keySet();
			for (Iterator<String> i = strings.iterator(); i.hasNext();){
				String key = i.next();
				httpGet.addHeader(key, header.get(key));
			}
		}
		return executeRequest(httpGet, charset);
	}



	/**
	 * http的get请求，增加异步请求头参数
	 * @param url
	 */
	public static String ajaxGet(String url) {
		return ajaxGet(url, "UTF-8");
	}
	
	/**
	 * http的get请求，增加异步请求头参数
	 * @param url
	 */
	public static String ajaxGet(String url, String charset) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
		return executeRequest(httpGet, charset);
	}

	/**
	 * http的post请求，传递map格式参数
	 */
	public static String post(String url, Map<String, String> dataMap) {
		return post(url, dataMap, "UTF-8");
	}

	/**
	 * http的post请求，传递map格式参数
	 */
	public static String post(String url, Map<String, String> dataMap, String charset) {
		HttpPost httpPost = new HttpPost(url);
		try {
			if (dataMap != null){
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : dataMap.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
				formEntity.setContentEncoding(charset);
				httpPost.setEntity(formEntity);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * http的post请求，增加异步请求头参数，传递map格式参数
	 */
	public static String ajaxPost(String url, Map<String, String> dataMap) {
		return ajaxPost(url, dataMap, "UTF-8");
	}

	/**
	 * http的post请求，增加异步请求头参数，传递map格式参数
	 */
	public static String ajaxPost(String url, Map<String, String> dataMap, String charset) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
		try {
			if (dataMap != null){
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : dataMap.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
				formEntity.setContentEncoding(charset);
				httpPost.setEntity(formEntity);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * http的post请求，增加异步请求头参数，传递json格式参数
	 */
	public static String ajaxPostJson(String url, String jsonString) {
		return ajaxPostJson(url, jsonString, "UTF-8");
	}

	/**
	 * http的post请求，增加异步请求头参数，传递json格式参数
	 */
	public static String ajaxPostJson(String url, String jsonString, String charset) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
//		try {
			StringEntity stringEntity = new StringEntity(jsonString, charset);// 解决中文乱码问题
			stringEntity.setContentEncoding(charset);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * 执行一个http请求，传递HttpGet或HttpPost参数
	 */
	public static String executeRequest(HttpUriRequest httpRequest) {
		return executeRequest(httpRequest, "UTF-8");
	}

	/**
	 * 执行一个http请求，传递HttpGet或HttpPost参数
	 */
	public static String executeRequest(HttpUriRequest httpRequest, String charset) {
		CloseableHttpClient httpclient;
		if ("https".equals(httpRequest.getURI().getScheme())){
			httpclient = createSSLInsecureClient();
		}else{
			httpclient = HttpClients.createDefault();
		}
		String result = "";
		try {
			try {
				httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
				CloseableHttpResponse response = httpclient.execute(httpRequest);
				HttpEntity entity = null;
				try {
					entity = response.getEntity();
					result = EntityUtils.toString(entity, charset);
				} finally {
					EntityUtils.consume(entity);
					response.close();
				}
			} finally {
				httpclient.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 创建 SSL连接
	 */
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(ex);
		}
	}


	public static String jsonPost(String url, JSONObject jsonObject, String encoding){
	CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		String response = null;
		try {
			StringEntity s = new StringEntity(jsonObject.toString());
			s.setContentEncoding(encoding);
			s.setContentType("application/json");//发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String result = EntityUtils.toString(res.getEntity());// 返回json格式：
				response = JSONObject.toJSONString(result);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		return response;
	}

}
