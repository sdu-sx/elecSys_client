package com.sdjxd.elecsysclient.exception;
/**
 * Classname:NoHostException
 * Description:无主机异常
 * @author 许凌霄
 * @version 0.1
 * */
public class NoHostException extends Exception
{
	public NoHostException()
	{
		super("set url without hostIp or hostPort!");
	}
}
