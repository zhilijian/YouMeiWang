package meikuu.web.vo;

public class ExtraVO {

	private boolean success;
	private String msg;
	private Object data;
	private Object extra;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getExtra() {
		return extra;
	}
	public void setExtra(Object extra) {
		this.extra = extra;
	}
	
	public ExtraVO() {
	}
	public ExtraVO(boolean success, String msg, Object data, Object extra) {
		this.success = success;
		this.msg = msg;
		this.data = data;
		this.extra = extra;
	}
}
