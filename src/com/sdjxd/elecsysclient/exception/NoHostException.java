package com.sdjxd.elecsysclient.exception;
/**
 * Classname:NoHostException
 * Description:�������쳣
 * @author ������
 * @version 0.1
 * */
public class NoHostException extends Exception
{
	public NoHostException()
	{
		super("set url without hostIp or hostPort!");
	}
}
