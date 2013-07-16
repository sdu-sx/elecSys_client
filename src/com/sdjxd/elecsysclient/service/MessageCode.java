package com.sdjxd.elecsysclient.service;
/**
 * Interfacename:MessageCode
 * Description: 定义线程消息传递机制的消息码和参数
 * @author 许凌霄
 * @version 1.0
 * */
public interface MessageCode 
{
	//Message.what:request
	public static final int LOGIN = 0x00000001;
	public static final int AUTO_LOGIN = 0x00000002;
	public static final int GET_TASKLIST = 0x00000003;
	public static final int GET_TASK = 0x00000004;
	public static final int POST_RESULT = 0x00000005;
	public static final int START_EXEC = 0x00000006;
	public static final int GET_DEVICE = 0x00000007;
	public static final int CHECK_QRCODE = 0x00000008;
	public static final int SAVE_DEVICE_RESULT=0x00000009;
	public static final int GET_FAULT_HISTORY=0x0000000A;
	public static final int POST_FAULT=0x0000000B;
	public static final int SET_HOST=0x0000000C;
	public static final int CLEAR_CACHE=0x0000000D;
	
	//Message.arg1:error
	public static final int ERR_NO_HOST = 0x00000001;
	public static final int ERR_NETWORK = 0x00000002;
	public static final int ERR_NO_WID = 0x00000003;
	public static final int ERR_WRONG_PWD = 0x00000004;
	public static final int ERR_NO_TID =0x00000005;
	public static final int ERR_NO_DID =0x00000006;
	public static final int ERR_INVALID_UPLOAD =0x00000007;
	public static final int ERR_TASK_UNDONE =0x00000008;

}
