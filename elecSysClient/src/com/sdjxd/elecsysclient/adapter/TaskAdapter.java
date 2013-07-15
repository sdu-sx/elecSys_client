package com.sdjxd.elecsysclient.adapter;

import java.util.Vector;

import com.sdjxd.elecsysclient.model.DeviceLite;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter
{
	private static final String TAG="TaskAdapter"; 
	private static int DONE_COLOR=0x00ff00;
	private Context context;
	private Vector<DeviceLite> devices;
	private int layout;
	private int dname;
	private int dtype;
	private int dAddress;
	public TaskAdapter(Context context,Vector<DeviceLite> data,int resource,int dNameView,int dTypeView, int dAddressView)
	{
		super();
		this.context=context;
		this.devices=data;
		this.layout=resource;
		this.dname=dNameView;
		this.dtype=dTypeView;
		this.dAddress=dAddressView;
	}
	@Override
	public int getCount() 
	{
		return devices.size();
	}

	@Override
	public DeviceLite getItem(int pos) 
	{
		return devices.get(pos);
	}

	@Override
	public long getItemId(int pos) 
	{
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater=LayoutInflater.from(context);
		if(convertView==null)
		{
			convertView=inflater.inflate(layout, null);
			ViewBinder views = new ViewBinder();
			views.dnameView= (TextView) convertView.findViewById(dname);
			views.dtypeView= (TextView) convertView.findViewById(dtype);
			views.dAddressView= (TextView) convertView.findViewById(dAddress);
			convertView.setTag(views);
		}
		ViewBinder views=(ViewBinder) convertView.getTag();
		DeviceLite device = devices.get(pos);
		if(device!=null)
		{
			views.dnameView.setText(device.dName);
			views.dtypeView.setText(device.dType);
			views.dAddressView.setText(device.dAddress);
		}
		
		if(getItem(pos).isCheck)
		{
			convertView.setBackgroundColor(DONE_COLOR);
		}
		return convertView;
	}
	
	private class ViewBinder
	{
		public TextView dnameView;
		public TextView dtypeView;
		public TextView dAddressView;
	}
}
