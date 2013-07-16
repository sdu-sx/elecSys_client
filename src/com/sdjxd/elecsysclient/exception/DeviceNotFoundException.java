package com.sdjxd.elecsysclient.exception;

public class DeviceNotFoundException extends Exception
{
	public DeviceNotFoundException()
	{
		super("Could not find this device on the server!");
	}

	public DeviceNotFoundException(String string)
	{
		super(string);
	}
}
