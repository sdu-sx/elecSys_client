package com.sdjxd.elecsysclient.adapter;

import java.util.Vector;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.model.CheckCard;
import com.sdjxd.elecsysclient.model.Task.TaskState;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter
{
	private static final String TAG="DeviceAdapter";
	private Context context;
	private Vector<CheckCard> cards;
	private TaskState state;
	private int layout;
	private int cName;
	private int cValue;
	private int checkBox;
	
	public DeviceAdapter(Context context,Vector<CheckCard> data,TaskState state,int resource,int cName,int cValue,int checkbox)
	{
		super();
		this.context=context;
		this.cards=data;
		this.state=state;
		this.layout=resource;
		this.cName=cName;
		this.cValue=cValue;
		this.checkBox=checkbox;
	}
	@Override
	public int getCount() 
	{
		return cards.size();
	}

	@Override
	public CheckCard getItem(int pos)
	{
		return cards.get(pos);
	}

	@Override
	public long getItemId(int pos)
	{
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		if(convertView==null)
		{
			convertView=inflater.inflate(layout, null);
			ViewBinder views=new ViewBinder();
			views.cnameView=(TextView) convertView.findViewById(cName);
			views.cvalueView=(EditText) convertView.findViewById(cValue);
			views.checkBox = (CheckBox) convertView.findViewById(checkBox);
			if(state.equals(TaskState.UNDO))
			{
				views.cvalueView.setOnFocusChangeListener(views);
				views.checkBox.setOnCheckedChangeListener(views);
			}
			else
			{
				views.cvalueView.setEnabled(false);
				views.checkBox.setEnabled(false);
			}
			convertView.setTag(views);
		}
		ViewBinder views=(ViewBinder) convertView.getTag();
		CheckCard card=cards.get(pos);
		views.cnameView.setText(card.cName);
		views.cvalueView.setText(card.cValue);
		views.checkBox.setChecked(card.isCheck);
		views.position=pos;
		return convertView;
	}
	private class ViewBinder implements OnFocusChangeListener,OnCheckedChangeListener
	{
		public TextView cnameView;
		public EditText cvalueView;
		public CheckBox checkBox;
		public int position;
		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
		{
			String text=cvalueView.getText().toString();
			Log.d(TAG, "EditText:"+text);
			Log.d(TAG, "cvalue:"+getItem(position).cValue);
			if(isChecked)
			{
				if(!text.equals(""))
				{
					getItem(position).isCheck=isChecked;
				}
				else
				{
					Toast.makeText(context, "请先填写本条目抄录值", Toast.LENGTH_SHORT).show();
					buttonView.setChecked(false);
				}
			}
			else
			{
				getItem(position).isCheck=isChecked;
			}
		}
		@Override
		public void onFocusChange(View view, boolean hasFocus)
		{
			
			CheckCard card=getItem(position);
			if(view instanceof EditText)
			{
				EditText edit=(EditText)view;
				if(!hasFocus)
				{
					card.cValue=edit.getText().toString();
				}
			}
	
		}
	}

}
