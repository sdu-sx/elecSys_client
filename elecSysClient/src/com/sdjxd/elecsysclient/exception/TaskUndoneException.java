package com.sdjxd.elecsysclient.exception;

public class TaskUndoneException extends Exception
{
	public TaskUndoneException()
	{
		super("The task has't finished yet!");
	}
}
