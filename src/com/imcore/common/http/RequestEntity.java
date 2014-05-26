package com.imcore.common.http;

import java.util.Map;

public class RequestEntity {
   private String url;
   private int method = HttpMethod.POST;
   private Map<String,Object> textFields;
   
   private RequestEntity(){
	   
   }
   
   /*
    * 指定的请求链接的地址
    */
	public RequestEntity(String url) {
		this.url = url;
	}
	
	

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            指定的请求链接地址
	 * @param textFields
	 *            请求参数，纯文本，不包含文件域
	 */
	public RequestEntity(String url, Map<String, Object> textFields) {
		this.url = url;
		this.textFields = textFields;
	}
    
	
	public RequestEntity(String url,int method,Map<String,Object> textFields){
		this.url = url;
		this.method = method;
		this.textFields = textFields;
	}
   
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public Map<String, Object> getTextFields() {
		return textFields;
	}

	public void setTextFields(Map<String, Object> textFields) {
		this.textFields = textFields;
	}
   
}
