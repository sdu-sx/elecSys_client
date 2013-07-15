package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Vector;

/**
 * Classname:FaultHistory
 * Description: 设备缺陷历史
 * @author 许凌霄
 * @version 1.0
 * */
public class FaultHistory implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6806714949401337990L;

	/**
	 * 设备id
	 * */
	public String did;
	
	/**
	 * 缺陷列表
	 * */
	private Vector<Fault> faults;
	
	public FaultHistory(String did)
	{
		this.did=did;
		setFaults(new Vector<Fault>());
	}
	public boolean addFault(Fault fault)
	{
		boolean result=false;
		if(fault!=null)
		{
			getFaults().add(fault);
			result=true;
		}
		return result;
	}
	public String toString()
	{
		String result="{"+did+","+getFaults()+"}";
		return result;
	}
	/**
	 * @return the faults
	 */
	public Vector<Fault> getFaults() {
		return faults;
	}
	/**
	 * @param faults the faults to set
	 */
	public void setFaults(Vector<Fault> faults) {
		this.faults = faults;
	}
}
