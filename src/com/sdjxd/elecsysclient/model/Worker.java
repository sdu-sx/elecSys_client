package com.sdjxd.elecsysclient.model;



/**
 * Classname:Worker
 * Description:员工对象
 * @author 许凌霄
 * @version 1.0
 * */
public class Worker

{
	/**
	 * 员工号
	 * */
	public String wid;
	
	/**
	 * 员工名
	 * */
	public String wname;
	
	/**
	 * 员工密码
	 * */
	public String pwd;
	
	
	
	/**
	 * 默认构造方法
	 * */
	public Worker(){}
	/**
	 * 构造方法
	 * @param 
	 * wid 员工号
	 * pwd 员工密码
	 * */
	public Worker(String wid,String pwd)
	{
		this.wid=wid;
		this.pwd=pwd;
	}
	/**
	 * 构造方法
	 * @param 
	 * wid 员工号
	 * name 员工名
	 * pwd 员工密码
	 * */
	public Worker(String id,String name,String pwd)
	{
		wid=id;
		wname=name;
		this.pwd=pwd;
	}
	public Worker(String id) 
	{
		wid=id;
	}

}
