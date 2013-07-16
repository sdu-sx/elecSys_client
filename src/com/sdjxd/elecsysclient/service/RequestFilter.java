package com.sdjxd.elecsysclient.service;
/**
 * Interfacename:RequestFiter
 * Description: 定义Intent传播的请求回应协议
 * @author 许凌霄
 * @version 1.0
 * */
public interface RequestFilter 
{
	public static final String PACK="com.sdjxd.elecSysClient.service";
	
//	public static final String CATEGORY_REQUEST=PACK+".REQUEST";
//	public static final String CATEGORY_REPLY=PACK+".REPLY";
	
	public static final String ACTION_START = PACK+".START";
	public static final String ACTION_LOGIN = PACK+".LOGIN";
	public static final String ACTION_AUTO_LOGIN = PACK+".AUTO_LOGIN";
	public static final String ACTION_GET_TASKLIST = PACK+".GET_TASKLIST";
	public static final String ACTION_GET_TASK = PACK+".GET_TASK";
	public static final String ACTION_POST_RESULT = PACK+".POST_RESULT";
	public static final String ACTION_START_EXEC = PACK+".START_EXE";
	public static final String ACTION_GET_DEVICE = PACK+".GET_DEVICE";
	public static final String ACTION_CHECK_QRCODE = PACK+".CHECK_QRCODE";
	public static final String ACTION_SAVE_DEVICE_RESULT = PACK+".SAVE_DEVICE_RESULT";
	public static final String ACTION_GET_FAULT_HISTORY = PACK+".GET_FAULT_HISTORY";
	public static final String ACTION_POST_FAULT = PACK+".POST_FAULT";
	public static final String ACTION_SET_HOST = PACK+".SET_HOST";
	public static final String ACTION_CLEAR_CACHE=PACK+".CLEAR_CACHE";

	public static final String KEY_RESPONSE="response";
	public static final String KEY_ERROR="error";
	public static final String KEY_REPLY="reply";
	
	public static final String KEY_WID="wid";
	public static final String KEY_PWD="pwd";
	public static final String KEY_WNAME="wname";
	public static final String KEY_TASKSTATE = "taskstate";
	public static final String KEY_TASKLIST = "tasklist";
	public static final String KEY_TID="tid";
	public static final String KEY_TASK="task";
	public static final String KEY_DID="did";
	public static final String KEY_FAULT_CONTENT="content";
	public static final String KEY_DEVICE="device";
	public static final String KEY_DNAME="dname";
	public static final String KEY_DTYPE="dtype";
	public static final String KEY_DADDRESS="dAddress";
	public static final String KEY_FAULT_HISTORY="faulthistory";
	public static final String KEY_FINISH_TIME="finishTime";
	public static final String KEY_UNDOLIST="undo";
	public static final String KEY_DONELIST="done";
	public static final String KEY_OVERTIMELIST="overtime";
	public static final String KEY_TNAME="tname";
	public static final String KEY_DEADLINE="deadline";
	public static final String KEY_DEVICE_NUM="deviceNum";
	public static final String KEY_BLANK="blank";
	public static final String KEY_FAULT_TIME="faultTime";
	public static final String KEY_QRCODE="qr";
	public static final String KEY_TIME="time";
	public static final String KEY_IP="ip";
	public static final String KEY_PORT="port";
}
