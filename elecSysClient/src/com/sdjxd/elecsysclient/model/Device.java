package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Vector;

import android.content.ContentValues;

/**
 * Classname:Device
 * Description:设备对象
 * @author 许凌霄
 * @version 1.0
 * */
public class Device implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1474873634457253816L;

	/**
	 * 设备号
	 * */
	public String did;
	
	/**
	 * 设备名称
	 * */
	public String dName;
	
	/**
	 * 设备类型
	 * */
	public String dType;
	
	/**
	 * 设备地址
	 * */
	public String dAddress;
	
	/**
	 * 设备二维码
	 * */
	public String QRCode;
	
	/**
	 * 设备作业卡条目
	 * */
	private Vector<CheckCard> checkCards;
	
	public Device(String did,String dName,String dType,String dAddress,String QRCode)
	{
		this.did=did;
		this.dName=dName;
		this.dType=dType;
		this.dAddress=dAddress;
		this.QRCode=QRCode;
		checkCards=new Vector<CheckCard>();
	}
	public Device() 
	{
		// TODO Auto-generated constructor stub
	}
	public boolean addCheckCard(CheckCard card)
	{
		boolean result=false;
		if(card!=null)
		{
			checkCards.add(card);
			result=true;
		}
		return result;		
	}
	
	public String toString()
	{
		String result="{"+did+","+dName+","+dType+","+dAddress+","+QRCode+","+checkCards+"}";
		return result;
	}
	public void setCheckCards(Vector<CheckCard> newCards) 
	{
		checkCards = newCards;
	}
	public Vector<CheckCard> getCheckCards()
	{
		return checkCards;
	}
}
