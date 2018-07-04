package com.gclfax.common.utils;

/**
 *
 */
public class ErrorInfo {

	public String msg;
	public int code;
	public static String FRIEND_INFO  = "亲、由于系统繁忙。";
	public static String PROCESS_INFO = "系统已把错误信息发送到后台管理员,会尽快的处理,给您带来的不便,敬请原谅。";
	public int client;

	/** 参数错误 */
	public static final int CODE_PARAMETER_ERROR = -10000;
	/** 数据库错误 */
	public static final int CODE_DB_ERROR = -20000;
	/** 数据库错误 - 记录不存在 */
	public static final int CODE_DB_ERROR_RECORD_NO_EXIST = -20001;
	/** 业务请求失败 */
	public static final int CODE_REQUEST_FAILED = -30000;

	
	/**返回页面路径**/
	public String returnUrl;
	/**返回页面描述**/
	public String returnMsg;
	
	/**
	 * 汇付返回的错误码
	 */
	public String chinaPnrErrorCode="";
	
	public ErrorInfo() {
		this.code = 0;
		this.msg = "";
	}

	public ErrorInfo(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public void clear() {
		this.code = 0;
		this.msg = "";
	}

	@Override
	public String toString() {
		return "ErrorInfo [msg=" + msg + ", code=" + code + ", FRIEND_INFO= " + FRIEND_INFO + ", PROCESS_INFO=" + PROCESS_INFO + "]";
	}
	
	/**
	 * 构建错误信息
	 * @param error
	 * @param code
	 * @param msg
	 * @param returnUrl
	 * @param returnMsg
	 * @return
	 */
	public static ErrorInfo createError(ErrorInfo error, int code, String msg, String returnUrl, String returnMsg){
		error.msg = msg;
		error.code = code;
		error.returnMsg = returnMsg;
		error.returnUrl = returnUrl;
		return error;
		
	}


	/** 
	 * 是否失败. code小于0表示失败。
	 * 
	 * @return 成功返回false， 失败返回true；
	 */
	public boolean isFailed() {
		return code < 0 ? true : false;
	}

	/**
	 * 是否成功。
	 * 
	 * @return
	 * @see #isFailed()
	 */
	public boolean isSuccess() {
		return !isFailed();
	}
}
