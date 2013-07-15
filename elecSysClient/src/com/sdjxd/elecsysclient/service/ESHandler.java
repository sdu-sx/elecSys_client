package com.sdjxd.elecsysclient.service;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import com.sdjxd.elecsysclient.DAO.SqlSupport;
import com.sdjxd.elecsysclient.exception.DeviceNotFoundException;
import com.sdjxd.elecsysclient.exception.InvalidUploadException;
import com.sdjxd.elecsysclient.exception.NoHostException;
import com.sdjxd.elecsysclient.exception.TaskNotFoundException;
import com.sdjxd.elecsysclient.exception.TaskUndoneException;
import com.sdjxd.elecsysclient.exception.WorkerNotFoundException;
import com.sdjxd.elecsysclient.exception.WrongPwdException;
import com.sdjxd.elecsysclient.model.CheckCard;
import com.sdjxd.elecsysclient.model.Device;
import com.sdjxd.elecsysclient.model.Fault;
import com.sdjxd.elecsysclient.model.FaultHistory;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.model.TaskList;
import com.sdjxd.elecsysclient.model.Worker;
import com.sdjxd.elecsysclient.net.ElecSysClient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/**
 * Classname:ESHandler
 * Description: ��̨�̵߳�handler�����ղ�������service���͵���Ϣ������Ȼ����serviceHandler���ͽ����Ϣ
 * @author ������
 * @version 1.0
 * */
public class ESHandler extends Handler implements MessageCode,RequestFilter
{
	private static final String TAG="ESHandler";
	private Handler targetHandler;
	private ElecSysClient client;
	private SqlSupport database;
	private Context context;
	
	public ESHandler(Handler handler,Looper looper,Context context)
	{
		super(looper);
		targetHandler=handler;
		client=new ElecSysClient();
		database= new SqlSupport();
		this.context=context;
	}
	
	/**
	 * ��дHandler�е�handlerMessage(),���ڴ�����Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	@Override
	public void handleMessage(Message msg)
	{
		Log.d(TAG,"message:"+msg.what);
		if(msg.what==LOGIN)
		{
			login(msg);
		}
		else if(msg.what==GET_TASKLIST)
		{
			getTaskList(msg);
		}
		else if(msg.what==GET_TASK)
		{
			getTask(msg);
		}
		else if(msg.what==POST_FAULT)
		{
			postFault(msg);
		}
		else if(msg.what==GET_DEVICE)
		{
			getDevice(msg);
		}
		else if(msg.what==GET_FAULT_HISTORY)
		{
			getFaultHistory(msg);
		}
		else if(msg.what==POST_RESULT)
		{
			postResult(msg);
		}
		else if(msg.what==CLEAR_CACHE)
		{
			clearCache(msg);
		}
		else if(msg.what==SAVE_DEVICE_RESULT)
		{
			saveDeviceResult(msg);
		}
	}
	
	private void saveDeviceResult(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=msg.what;
		Bundle data = msg.getData();
		Device device = (Device) data.getSerializable(KEY_DEVICE);
		String tid=data.getString(KEY_TID);
		database.setDevice(context, device, tid);
		reply.obj="����ɹ�";
		Log.d(TAG, "save device result successfully!");
		reply.sendToTarget();
	}

	private void clearCache(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=msg.what;
		database.Emptydata(context);
		reply.obj="�������ɹ�";
		reply.sendToTarget();
		Log.d(TAG, "clear reply sended");
	}

	/**
	 * ��̨���ʷ����������ύ������������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void postResult(Message msg)
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=POST_RESULT;
		Task task=(Task) msg.obj;
		String did="005";//���ݿ�ȡ��
		Device[] devices = new Device[task.deviceNum];
		for(int i=0;i<task.deviceNum;i++)
		{
			devices[i]=database.getDevice(context, task.getDevices().get(i).did, task.tid);
		}
		try
		{
			Date ft=null;
			boolean isDevicePush=true;
			for(int i=0;i<task.deviceNum;i++)
			{
				isDevicePush=client.postDeviceResult(task.tid, devices[i].did, devices[i].getCheckCards());
			}
			if(isDevicePush)
			{
				ft=client.finishTask(task.tid);
				if(ft!=null)
				{
					database.Setendtime(context, task.tid, ft);
					database.setTaskstate(context, TaskState.DONE, task.tid);
				}
			}
			reply.obj=ft;
		}
		catch (NoHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
		} 
		catch (TaskNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_TID;
		} 
		catch (DeviceNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_DID;
		} 
		catch (TaskUndoneException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_TASK_UNDONE;
		}
		finally
		{
			reply.sendToTarget();
		}
		
		
	}

	/**
	 * ��̨���ʷ��������л�ȡ�豸ȱ����ʷ���������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void getFaultHistory(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=GET_FAULT_HISTORY;
		FaultHistory fh=null;
		String did=(String)msg.obj;
		Date time=null;
		try
		{
			fh=client.getFaultHistory(did,time);
			reply.obj=fh;
		}
		catch (NoHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
		} 
		catch (DeviceNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_DID;
		}
		finally
		{
			reply.sendToTarget();
		}
	}

	/**
	 * ��̨���ʷ��������л�ȡ�豸��ϸ��Ϣ���������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	
	private void getDevice(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=GET_DEVICE;
		Device device =null;
		Bundle bundle=msg.getData();
		String tid=bundle.getString(KEY_TID);
		String did=bundle.getString(KEY_DID);
		device=database.getDevice(context, did, tid);
		try 
		{
			if(device==null)
			{
				device=client.getDevice(did);
				database.setDevice(context, device, tid);
			}
			reply.obj=device;
		} 
		catch (NoHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
		} 
		catch (DeviceNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_DID;
		}
		finally
		{
			reply.sendToTarget();
		}
	}

	/**
	 * ��̨���ʷ����������ύȱ�ݲ��������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void postFault(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=POST_FAULT;
		Fault fault=(Fault) msg.obj;
		try 
		{
			if(fault!=null)
			{
				fault = client.postFault(fault.did,fault.content);
				
				//save to database
				
				reply.obj=fault;
			}
			
		}
		catch (NoHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
		} 
		catch (DeviceNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_DID;
		} 
		catch (InvalidUploadException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_INVALID_UPLOAD;
		}
		finally
		{
			reply.sendToTarget();
		}
		
		
	}

	/**
	 * ��̨���ʷ��������л�ȡ������������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void getTask(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=GET_TASK;
		String tid=(String) msg.obj;
//		Task task=database.getTask(context, tid);
//		Log.d(TAG, "count:"+task.getDevices().size());
		Task task=null;
		//�ڱ������ݿ��в�ѯtask
		try 
		{
			if(task==null)
			{
				task = client.getTask(tid);
				Log.d(TAG, "count:"+task.getDevices().size());
				database.setTask(context, task);
			}
			reply.obj=task;
		} 
		catch (NoHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
		} 
		catch (TaskNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_TID;
		}
		finally
		{
			reply.sendToTarget();
		}	
	}

	/**
	 * ��̨���ʷ��������л�ȡ�����б���������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void getTaskList(Message msg) 
	{
		Message reply=targetHandler.obtainMessage();
		reply.what=GET_TASKLIST;
		Bundle bundle=msg.getData();
		String wid=bundle.getString(KEY_WID);
		String state=bundle.getString(KEY_TASKSTATE);
		//�ڱ������ݿ��в�ѯ
		TaskList tasklist=new TaskList(wid,TaskState.valueOf(state));
		Date date= database.getTasklist(context, tasklist, TaskState.valueOf(state), wid);
		try 
		{
			TaskList remotelist=client.getTasklist(wid,state,date);
			tasklist.append(remotelist);
			//tasklist sorting!!
//			tasklist.sortlist();
			database.SetTasklist(context, tasklist);
			reply.obj=tasklist;
		} 
		catch (NoHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_HOST;
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NETWORK;
			Log.d(TAG, "put network error to msg!");
		} catch (WorkerNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply.arg1=ERR_NO_WID;
		}
		finally
		{
			reply.sendToTarget();
			Log.d(TAG, "send to service");
		}
	}

	/**
	 * ��̨���ʷ��������е�¼���������ش������Ϣ
	 * @param
	 * msg ����MessageCode�ж����what
	 * */
	private void login(Message msg)
	{
		Worker worker=(Worker) msg.obj;
		Message send=targetHandler.obtainMessage();
		send.what=LOGIN;
		try
		{
			worker=client.login(worker.wid, worker.pwd);
			if(worker!=null)
			{
				database.SetWorker(context,worker);
			}
			send.obj=worker;
		}
		catch (NoHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			send.arg1=ERR_NO_HOST;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			send.arg1=ERR_NETWORK;
		} 
		catch (WorkerNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			send.arg1=ERR_NO_WID;
		} 
		catch (WrongPwdException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			send.arg1=ERR_WRONG_PWD;
			Log.d(TAG, "error has been set");
		}		
		finally
		{
			send.sendToTarget();
			Log.d(TAG, "reply message!");
		}
	}
}
