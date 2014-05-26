package com.imcore.common.http;

import com.imcore.common.util.JsonUtil;

public class ResponseJsonEntity {
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String data;
	private String message;

	private ResponseJsonEntity() {

	}

	public static ResponseJsonEntity fromJSON(String json) {
		ResponseJsonEntity entity = new ResponseJsonEntity();
		entity.status = Integer.parseInt(JsonUtil.getJsonValueByKey(json,
				"status"));
		if (entity.status == 200) {
			entity.data = JsonUtil.getJsonValueByKey(json, "data");
		} else {
			entity.message = JsonUtil.getJsonValueByKey(json, "message");
		}
		return entity;
	}

}
