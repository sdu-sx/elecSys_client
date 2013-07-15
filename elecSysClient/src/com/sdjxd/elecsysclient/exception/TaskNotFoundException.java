package com.sdjxd.elecsysclient.exception;

public class TaskNotFoundException extends Exception
{
	public TaskNotFoundException()
	{
		super("Could not find this task in the server!");
	}
}
