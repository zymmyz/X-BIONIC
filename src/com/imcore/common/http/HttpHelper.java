package com.imcore.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.util.Log;

public class HttpHelper {
	private static final String DOMAIN_URL = "http://yunming-api.suryani.cn/api";
	private static final String LOG_HTTP_GET_ERROR = "com.imcore.common.http.GetError";
	private static final String LOG_HTTP_POST_ERROR = "com.imcore.common.http.PostError";
	private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
	private static final String CHARSET = "utf-8";

	/*
	 * 根据请求实体从网络上获取json纯文本
	 */
	public synchronized static String execute(RequestEntity entity)
			throws Exception {
		String jsonResult = null;
		String url = DOMAIN_URL + entity.getUrl();
		switch (entity.getMethod()) {
		case HttpMethod.GET:
			if (entity.getTextFields() == null) {
				jsonResult = get(url);
			} else {
				jsonResult = get(url, entity.getTextFields());
			}

			break;

		case HttpMethod.POST:
			if (entity.getTextFields() == null) {
				jsonResult = post(url);
			} else {
				jsonResult = post(url, entity.getTextFields());
			}
			break;
		}
		return jsonResult;
	}

	private synchronized static String get(String url) throws Exception {
		return get(url, null);
	}

	/*
	 * 执行GET请求
	 */
	public synchronized static String get(String url, Map<String, Object> params)
			throws Exception {
		String jsonResult = "";
		InputStream is = null;
		try {
			if (params != null && params.size() > 0) {
				String urlEncodedForm = toUrlEncodedFormParams(params);
				url = url + "?" + urlEncodedForm;
			}

			HttpURLConnection conn = getHttpURLConnection(url);

			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				jsonResult = read(is);
				// Log.i(LOG_HTTP_GET_ERROR, jsonResult);
			} else {
				throw new Exception();
			}

		} catch (MalformedURLException e) {
			Log.e(LOG_HTTP_GET_ERROR, e.getLocalizedMessage());
			e.printStackTrace();
			throw (e);
		} catch (IOException e) {
			Log.e(LOG_HTTP_GET_ERROR, e.getLocalizedMessage());
			e.printStackTrace();
			throw (e);
		} finally {
			closeStream(is);
		}

		return jsonResult;
	}

	private synchronized static String post(String url) throws Exception {
		return post(url, null);
	}

	/*
	 * 执行post请求
	 */
	public synchronized static String post(String url,
			Map<String, Object> params) throws Exception {
		InputStream is = null;
		OutputStream os = null;
		String jsonResult = "";
		try {
			HttpURLConnection conn = getHttpURLConnection(url);
			// 表头的设置
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-type", CONTENT_TYPE_URL_ENCODED);
			conn.setRequestProperty("Charset", CHARSET);

			os = conn.getOutputStream();
			if (params != null && params.size() != 0) {
				String buff = toUrlEncodedFormParams(params);
				String urlEncodedForm = toUrlEncodedFormParams(params);
				os.write(urlEncodedForm.getBytes());
				os.flush();
			}

			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				jsonResult = read(is);
				Log.i("Post", jsonResult);
			} else {
				throw (new Exception());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw (e);
		} catch (IOException e) {
			Log.e(LOG_HTTP_POST_ERROR, e.getLocalizedMessage());
			e.printStackTrace();
			throw (e);
		} finally {
			closeStream(is);
			closeStream(os);
		}
		return jsonResult;
	}

	/*
	 * 通过网络来读取的纯字符串,编码格式一定要与网络源的字符集一致，否则会导致乱码
	 */
	private static String read(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buff = new byte[128];
		int len = 0;
		while ((len = is.read(buff)) != -1) {
			bos.write(buff, 0, len);
		}
		String text = new String(bos.toByteArray(), "utf-8");
		bos.flush();
		closeStream(bos);
		return text;
	}

	/*
	 * 关闭流的操作(InputStrea+OutputStream+其子类)
	 */
	public static void closeStream(Object obj) {
		if (obj != null && obj instanceof InputStream) {
			InputStream is = (InputStream) obj;
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (obj != null && obj instanceof OutputStream) {
			OutputStream os = (OutputStream) obj;
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 获得HttpUrlConnection对象,用于连接网络
	 */
	private static HttpURLConnection getHttpURLConnection(String strURL)
			throws IOException {
		URL url = new URL(strURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(15000);
		return conn;
	}

	public static InputStream getInputStream(String url) {
		InputStream is = null;
		try {
			HttpURLConnection conn = getHttpURLConnection(url);
			is = conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	/*
	 * 由于GET请求方式，后面若有带参数，则将参数转成 key = value&key = value的形式
	 */
	private static String toUrlEncodedFormParams(Map<String, Object> params) {
		StringBuffer strBuff = new StringBuffer();
		Set<String> keySet = params.keySet();
		Iterator<String> i = keySet.iterator();
		while (i.hasNext()) {
			String key = i.next();
			String value = params.get(key).toString();
			strBuff.append(key);
			strBuff.append("=");
			strBuff.append(value);
			if (i.hasNext()) {
				strBuff.append("&");
			}
		}
		return strBuff.toString();
	}

}
