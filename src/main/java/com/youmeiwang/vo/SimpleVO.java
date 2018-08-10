package com.youmeiwang.vo;

public class SimpleVO {

	private boolean success;

	private Object msg;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public SimpleVO() {
	}

	public SimpleVO(boolean success, Object msg) {
		this.success = success;
		this.msg = msg;
	}
}
