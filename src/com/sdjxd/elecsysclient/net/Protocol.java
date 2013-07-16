package com.sdjxd.elecsysclient.net;

import com.sdjxd.elecsysclient.exception.NoHostException;
/**
 * Interfacename:Protocol
 * Description: 网络协议借口
 * @author 许凌霄
 * @version 1.0
 * */
public interface Protocol 
{
	public static final String HTTP="http://";
	public static final String DIR="/elecSys/mobile/";
	public static final int CONNECTION_TIMEOUT=3000;
	public static final String CONTENT_LENGTH="Content-Length";
	
	public static final String REQUEST_LOGIN="Login?";
	public static final String REQUEST_GET_TASKLIST="Acquire?";
	public static final String REQUEST_GET_TASK = "getTask?";
	public static final String REQUEST_POST_FAULT="writeFault";
	public static final String REQUEST_GET_DEVICE="getDevice?";
	public static final String REQUEST_GET_FAULT_HISTORY="getFault?";
	public static final String REQUEST_POST_RESULT="writeResult";
	public static final String REQUEST_FINISH_TASK="checkTask?";
	
	public static final String PARAM_WID="wid=";
	public static final String PARAM_PWD="pwd=";
	public static final String PARAM_STATE="state=";
	public static final String PARAM_DATE="time=";
	public static final String PARAM_TID="tid=";
	public static final String PARAM_DID="did=";
	public static final String PARAM_CONTENT="content=";
	
	public static final String ACK="success";
	public static final String MSG_NO_WID = "no such worker";
	public static final String MSG_WRONG_PWD="wrong password";
	public static final String MSG_NO_TID="no such task";
	public static final String MSG_NO_DID="no such device";
	public static final String MSG_NULL_FAULT="no fault log";
	public static final String MSG_TASK_UNDONE="not finished";
	
	
	public static final String KEY_WNAME="wname";
	public static final String KEY_RESPONSE="message";
	public static final String KEY_TASKLIST="tasklist";
	public static final String KEY_TID="tid";
	public static final String KEY_TNAME="tname";
	public static final String KEY_DEVICE_COUNT="count";
	public static final String KEY_RELEASE_TIME="stime";
	public static final String KEY_FINISH_TIME="etime";
	public static final String KEY_DEADLINE="deadline";
	public static final String KEY_DEVICELITES="devicelites";
	public static final String KEY_STATE="state";
	public static final String KEY_DEVICE_ADDRESS="address";
	public static final String KEY_DID="did";
	public static final String KEY_DNAME="dname";
	public static final String KEY_DTYPE="type";
	public static final String KEY_FID="fid";
	public static final String KEY_FAULT_TIME="time";
	public static final String KEY_DEVICE_QRCODE="qr";
	public static final String KEY_CHECKCARD_LIST="clauselist";
	public static final String KEY_CID="cid";
	public static final String KEY_CNAME="cname";	
	public static final String KEY_FAULT_CONTENT="content";
	public static final String KEY_FAULT_LIST="faultlist";
	public static final String KEY_RESULT="parameter";
	public static final String KEY_CVALUE="value";
	
	
	public String generateURL(String ip,String port) throws NoHostException;
}
