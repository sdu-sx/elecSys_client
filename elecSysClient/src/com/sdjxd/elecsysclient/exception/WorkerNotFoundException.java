package com.sdjxd.elecsysclient.exception;


public class WorkerNotFoundException extends Exception
{
	public WorkerNotFoundException()
	{
		super("No wid in the server!");
	}
}
