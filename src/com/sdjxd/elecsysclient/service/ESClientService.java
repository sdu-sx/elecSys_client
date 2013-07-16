package com.sdjxd.elecsysclient.service;




import java.util.Date;

import com.sdjxd.elecsysclient.model.Device;
import com.sdjxd.elecsysclient.model.Fault;
import com.sdjxd.elecsysclient.model.FaultHistory;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.TaskList;
import com.sdjxd.elecsysclient.model.Worker;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
/**
 * Classname:ESClientService
 * Description: Electric system�ĺ�̨����,����ǰ�����󲢷�����̨�̴߳���
 * @author ������
 * @version 1.0
 * */
public class ESClientService extends Service implements RequestFilter,MessageCode
{
	private static final String TAG="ESClientService";
	
	private HandlerThread thread=null;
	private ESHandler bgHandler=null;
	
	/**
	 * ����ʱ�����̣߳�ESHandler
	 * */
	@Override
	public void onCreate()
	{
		super.onCreate();
		thread=new HandlerThread("Service thread",Process.THREAD_PRIORITY_DEFAULT);
		thread.start();
		bgHandler=new ESHandler(new ServiceHandler(),thread.getLooper(),this);
		
		Log.d(TAG, "service created!");
	}
	
	/**
	 * ����Activity��Intent���󣬲�����Ϣ����
	 * @param
	 * intent ����action����ز������������
	 * */
	@Override
	public int onStartCommand(Intent intent,int flag,int startId)
	{
		String action=intent.getAction();
		Log.d(TAG, "service on start:"+action);
		
		if(action!=null)
		{
			if(action.equals(ACTION_LOGIN))
			{
				sendLogin(intent);
			}
			else if(action.equals(ACTION_AUTO_LOGIN))
			{
				sendAutoLogin(intent);
			}
			else if(action.equals(ACTION_GET_TASKLIST))
			{
				sendGetTaskList(intent);
			}
			else if(action.equals(ACTION_GET_TASK))
			{
				sendGetTask(intent);
			}
			else if(action.equals(ACTION_POST_FAULT))
			{
				sendPostFault(intent);
			}
			else if(action.equals(ACTION_GET_DEVICE))
			{
				sendGetDevice(intent);
			}
			else if(action.equals(ACTION_GET_FAULT_HISTORY))
			{
				sendGetFaultHistory(intent);
			}
			else if(action.equals(ACTION_POST_RESULT))
			{
				sendPostResult(intent);
			}
			else if(action.equals(ACTION_CLEAR_CACHE))
			{
				sendClearCache(intent);
			}
			else if(action.equals(ACTION_SAVE_DEVICE_RESULT))
			{
				sendSaveResult(intent);
			}
			else if(action.equals(ACTION_CHECK_QRCODE))
			{
				sendCheckQR(intent);
			}
			else if(action.equals(ACTION_SET_HOST))
			{
				sendSetHost(intent);
			}
		}
		
		return START_STICKY_COMPATIBILITY; 
	}
	
	private void sendSetHost(Intent intent)
	{
		Message msg=bgHandler.obtainMessage();
		msg.setData(intent.getExtras());
		msg.what=SET_HOST;
		msg.sendToTarget();
	}

	private void sendCheckQR(Intent intent)
	{
		Message msg=bgHandler.obtainMessage();
		msg.setData(intent.getExtras());
		msg.what=CHECK_QRCODE;
		msg.sendToTarget();
	}

	private void sendSaveResult(Intent intent) 
	{
		Message msg=bgHandler.obtainMessage();
		msg.setData(intent.getExtras());
		msg.what=SAVE_DEVICE_RESULT;
		msg.sendToTarget();
	}

	private void sendClearCache(Intent intent) 
	{
		Message msg=bgHandler.obtainMessage();
		msg.what=CLEAR_CACHE;
		msg.sendToTarget();
	}

	/**
	 * �����ύ��¼��Ϣ����Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendPostResult(Intent intent) 
	{
		Task task=(Task) intent.getSerializableExtra(KEY_TASK);
		Message msg=bgHandler.obtainMessage();
		msg.what=POST_RESULT;
		msg.obj=task;
		msg.sendToTarget();
	}

	/**
	 * ���ͻ�ȡ�豸ȱ����ʷ����Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendGetFaultHistory(Intent intent) 
	{
		String did=intent.getStringExtra(KEY_DID);
		Message msg=bgHandler.obtainMessage();
		msg.what=GET_FAULT_HISTORY;
		msg.obj=did;
		msg.sendToTarget();
	}

	/**
	 * ���ͻ�ȡ�豸��ϸ��Ϣ����Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendGetDevice(Intent intent) 
	{
		Message msg=bgHandler.obtainMessage();
		msg.what=GET_DEVICE;
		msg.setData(intent.getExtras());
		msg.sendToTarget();
	}

	/**
	 * �����ύȱ�ݵ���Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendPostFault(Intent intent) 
	{
		String did=intent.getStringExtra(KEY_DID);
		String content=intent.getStringExtra(KEY_FAULT_CONTENT);
		Fault fault=new Fault(did,content);
		Message msg=bgHandler.obtainMessage();
		msg.what=POST_FAULT;
		msg.obj=fault;
		msg.sendToTarget();
	}

	/**
	 * ���ͻ�ȡ������ϸ��Ϣ����Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendGetTask(Intent intent) 
	{
		String tid=intent.getStringExtra(KEY_TID);
		Message msg = bgHandler.obtainMessage();
		msg.what=GET_TASK;
		msg.obj=tid;
		msg.sendToTarget();
	}

	/**
	 * ���͵�¼��Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendLogin(Intent intent)
	{
		Worker worker=new Worker(intent.getStringExtra(KEY_WID),intent.getStringExtra(KEY_PWD));
		Message msg = bgHandler.obtainMessage();
		msg.what=LOGIN;
		msg.obj=worker;
		msg.sendToTarget();
	}
	
	/**
	 * �����Զ���¼��Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendAutoLogin(Intent intent)
	{
		Worker worker=new Worker(intent.getStringExtra(KEY_WID));
		Message msg = bgHandler.obtainMessage();
		msg.what=AUTO_LOGIN;
		msg.obj=worker;
		msg.sendToTarget();
	}
	
	/**
	 * ���ͻ�ȡ�����б���Ϣ����̨�߳�
	 * @param
	 * intent ����action����ز������������
	 * */
	private void sendGetTaskList(Intent intent)
	{
		Message msg = bgHandler.obtainMessage();
			msg.what=GET_TASKLIST;
			msg.setData(intent.getExtras());
			msg.sendToTarget();
	}
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * ����ʱ�˳��߳�
	 * */
	public void onDestroy()
	{
		thread.quit();
	}
	
	/**
	 * Classname:ESClientService.ServiceHandler
	 * Description: ���պ�̨�̵߳Ļ�Ӧ��Ϣ�����ѽ���㲥Intent��ǰ����Ӧ�Ľ���������Intent���޸�UI
	 * @author ������
	 * @version 1.0
	 * */
	private class ServiceHandler extends Handler
	{
		private static final String TAG="ServiceHandler";
		
		/**
		 * ������Ϣ
		 * @param
		 * msg ����MessageCode�ж����what
		 * */
		public void handleMessage(Message msg)
		{
			Log.d(TAG, "receive:"+msg.what);
			if(msg.what==LOGIN)
			{
				receiveLogin(msg);
			}
			else if(msg.what==GET_TASKLIST)
			{
				receiveTaskList(msg);
			}
			else if(msg.what==GET_TASK)
			{
				receiveTask(msg);
			}
			else if(msg.what==POST_FAULT)
			{
				receivePostFault(msg);
			}
			else if(msg.what==GET_DEVICE)
			{
				receiveDevice(msg);
			}
			else if(msg.what==GET_FAULT_HISTORY)
			{
				receiveFaultHistory(msg);
			}
			else if(msg.what==POST_RESULT)
			{
				receivePostResult(msg);
			}
			else if(msg.what==CLEAR_CACHE)
			{
				receiveClearCache(msg);
			}
			else if(msg.what==SAVE_DEVICE_RESULT)
			{
				receiveSaveResult(msg);
			}
			else if(msg.what==CHECK_QRCODE)
			{
				receiveCheckQR(msg);
			}
			else if(msg.what==SET_HOST)
			{
				receiveSetHost(msg);
			}
		}
		
		private void receiveSetHost(Message msg) 
		{
			String result=(String) msg.obj;
			Intent intent = new Intent(ACTION_SET_HOST);
			if(result==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "����ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_REPLY, result);
				Log.d(TAG, result);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveCheckQR(Message msg) 
		{
			String result=(String) msg.obj;
			String did=msg.getData().getString(KEY_DID);
			String text=null;
			if(msg.arg1==ERR_NO_HOST)
			{
				text = "δ���÷�����IP��˿�";
				Toast.makeText(ESClientService.this, text, Toast.LENGTH_LONG).show();
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				text = "���紫���쳣";
				Toast.makeText(ESClientService.this, text, Toast.LENGTH_LONG).show();
			}
			else if(msg.arg1==ERR_NO_DID)
			{
				text = "�Ҳ������豸";
				Toast.makeText(ESClientService.this, text, Toast.LENGTH_LONG).show();
			}
			else if(result==null)
			{
				text = "��ά����֤ʧ��";
				Toast.makeText(ESClientService.this, text, Toast.LENGTH_LONG).show();
			}
			else
			{
				Log.d(TAG, "check success!");
				text = result;
				Toast.makeText(ESClientService.this, text, Toast.LENGTH_SHORT).show();
				Intent intent =new Intent();
				intent.putExtras(msg.getData());
				sendGetDevice(intent);
			}
		}

		private void receiveSaveResult(Message msg) 
		{
			String result = (String) msg.obj;
			Intent intent = new Intent(ACTION_SAVE_DEVICE_RESULT);
			if(result==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�����豸��¼���ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_REPLY, result);
				Log.d(TAG, result);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveClearCache(Message msg) 
		{
			String result=(String) msg.obj;
			Intent intent = new Intent(ACTION_CLEAR_CACHE);
			if(result==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�������ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_REPLY, result);
				Log.d(TAG, result);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receivePostResult(Message msg) 
		{
			Date result=(Date) msg.obj;
			Intent intent=new Intent(ACTION_POST_RESULT);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_TID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "����Ų�����");
			}
			else if(msg.arg1==ERR_NO_DID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�������޴��豸");
			}
			else if(msg.arg1==ERR_TASK_UNDONE)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "����δ���");
			}
			else if(result==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�ύ��¼��Ϣʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_FINISH_TIME, result);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveFaultHistory(Message msg)
		{
			FaultHistory faults=(FaultHistory) msg.obj;
			Intent intent=new Intent(ACTION_GET_FAULT_HISTORY);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_DID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�豸�Ų�����");
			}
			else if(faults==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "��ȡ�豸ȱ����ʷʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_FAULT_HISTORY, faults);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveDevice(Message msg) 
		{
			Device device = (Device) msg.obj;
			Intent intent=new Intent(ACTION_GET_DEVICE);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_DID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�豸�Ų�����");
			}
			else if(device==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR,"��ȡ�豸��ϸ��Ϣʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_DEVICE,device);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receivePostFault(Message msg)
		{
			Fault fault=(Fault) msg.obj;
			Intent intent=new Intent(ACTION_POST_FAULT);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_DID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�Ҳ������豸");
			}
			else if(msg.arg1==ERR_INVALID_UPLOAD)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "����ȱ�ݲ���ȷ");
			}
			else if(fault==null)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�ϴ�ȱ��ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_REPLY, "�ϴ�ȱ�ݳɹ���");
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveTask(Message msg) 
		{
			Task task=(Task) msg.obj;
			Intent intent=new Intent(ACTION_GET_TASK);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_TID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�޷��ҵ�������");
			}
			else if(task==null)
			{
				Log.d(TAG, "task is null");
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "��ȡ����ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_TASK,task);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		private void receiveTaskList(Message msg) 
		{
			TaskList tasklist=(TaskList) msg.obj;
			Intent intent=new Intent(ACTION_GET_TASKLIST);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
				Log.d(TAG, "put network error to intent!");
			}
			else if(msg.arg1==ERR_NO_WID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���Ų�����");
			}
			else if(tasklist==null)
			{
				Log.d(TAG, "tasklist is null");
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "��ȡ�����б�ʧ��");
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_TASKLIST,tasklist);
			}
			ESClientService.this.sendBroadcast(intent);
		}

		/**
		 * ���յ�¼����Ľ�������㲥�����Activity
		 * */
		private void receiveLogin(Message msg)
		{
			Worker worker=(Worker) msg.obj;
			Intent intent=new Intent(ACTION_LOGIN);
			if(msg.arg1==ERR_NO_HOST)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "δ���÷�����IP��˿�");
			}
			else if(msg.arg1==ERR_NETWORK)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���紫���쳣");
			}
			else if(msg.arg1==ERR_NO_WID)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "���Ų�����");
			}
			else if(msg.arg1==ERR_WRONG_PWD)
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "�������");
			}
			else if(worker!=null)
			{
				intent.putExtra(KEY_RESPONSE, true);
				intent.putExtra(KEY_WID, worker.wid);
				intent.putExtra(KEY_WNAME, worker.wname);
				intent.putExtra(KEY_PWD, worker.pwd);
			}
			else
			{
				intent.putExtra(KEY_RESPONSE, false);
				intent.putExtra(KEY_ERROR, "��¼ʧ��");
			}
			
			ESClientService.this.sendBroadcast(intent);
		}
	}
}
