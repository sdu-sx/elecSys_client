package com.sdjxd.elecsysclient.model;



/**
 * Classname:Worker
 * Description:Ա������
 * @author ������
 * @version 1.0
 * */
public class Worker

{
	/**
	 * Ա����
	 * */
	public String wid;
	
	/**
	 * Ա����
	 * */
	public String wname;
	
	/**
	 * Ա������
	 * */
	public String pwd;
	
	
	
	/**
	 * Ĭ�Ϲ��췽��
	 * */
	public Worker(){}
	/**
	 * ���췽��
	 * @param 
	 * wid Ա����
	 * pwd Ա������
	 * */
	public Worker(String wid,String pwd)
	{
		this.wid=wid;
		this.pwd=pwd;
	}
	/**
	 * ���췽��
	 * @param 
	 * wid Ա����
	 * name Ա����
	 * pwd Ա������
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
