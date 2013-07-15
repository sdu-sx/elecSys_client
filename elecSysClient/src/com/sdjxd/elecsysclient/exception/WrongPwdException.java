package com.sdjxd.elecsysclient.exception;

public class WrongPwdException extends Exception
{
	public WrongPwdException()
	{
		super("Wrong password!");
	}
}
