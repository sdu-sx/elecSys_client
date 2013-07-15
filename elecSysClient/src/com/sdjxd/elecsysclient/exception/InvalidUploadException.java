package com.sdjxd.elecsysclient.exception;

public class InvalidUploadException extends Exception
{
	public InvalidUploadException()
	{
		super("your upload is refuse by server!");
	}
}
