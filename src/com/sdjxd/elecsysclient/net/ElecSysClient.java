package com.sdjxd.elecsysclient.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sdjxd.elecsysclient.exception.DeviceNotFoundException;

import com.sdjxd.elecsysclient.exception.InvalidUploadException;
import com.sdjxd.elecsysclient.exception.NoHostException;
import com.sdjxd.elecsysclient.exception.TaskNotFoundException;
import com.sdjxd.elecsysclient.exception.TaskUndoneException;
import com.sdjxd.elecsysclient.exception.WorkerNotFoundException;
import com.sdjxd.elecsysclient.exception.WrongPwdException;
import com.sdjxd.elecsysclient.model.CheckCard;
import com.sdjxd.elecsysclient.model.Device;
import com.sdjxd.elecsysclient.model.DeviceLite;
import com.sdjxd.elecsysclient.model.Fault;
import com.sdjxd.elecsysclient.model.FaultHistory;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.model.TaskList;
import com.sdjxd.elecsysclient.model.TaskLite;
import com.sdjxd.elecsysclient.model.Worker;

/**
 * Classname:ElecSysClient
 * Description: Http客户端
 * @author 许凌霄
 * @version 1.0
 * */
public class ElecSysClient implements Protocol
{
	
	private static final String TAG="ElecSysClient";
	
	
	private HttpURLConnection conn=null;
	private URL url=null;
	private SimpleDateFormat dateFormat;
	
	/**
	 * 主机ip和端口
	 * */
	private String hostIp=null;
	private String hostPort=null;
	
	/**
	 * 默认构造函数
	 * */
	public ElecSysClient()
	{
		hostIp="121.250.216.127";
		hostPort="8080";
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		
	}
	
	/**
	 * 设置ip
	 * @param 
	 * ip 主机ip
	 * */
	public void setIP(String ip)
	{
		hostIp=ip;
	}
	
	/**
	 * 设置端口
	 * @param 
	 * port 主机端口
	 * */
	public void setPort(String port)
	{
		hostPort=port;
	}
	
	/**
	 * 获取主机ip
	 * @return 主机ip
	 * */
	public String getIP()
	{
		return hostIp;
	}
	
	/**
	 * 获取主机端口
	 * @return 主机端口
	 * */
	public String getPort()
	{
		return hostPort;
	}
	
	/**
	 * 根据主机IP和端口生成URL字符串,主机ip或端口为空时
	 * @param
	 * ip 主机ip
	 * port 主机端口
	 * @return URL字符串
	 * @throws 
	 * NoHostException
	 * */
	@Override
	public String generateURL(String ip,String port) throws NoHostException 
	{
		String sUrl;
		if(ip!=null&&port!=null)
		{
			sUrl=HTTP+hostIp+":"+hostPort+DIR;
		}
		else
		{
			throw new NoHostException();
		}
		return sUrl;
	}
	
	/**
	 * 获取缺陷历史
	 * @param
	 * did 设备号
	 * time 数据库中最晚缺陷时间
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws DeviceNotFoundException 
	 * */
	public FaultHistory getFaultHistory(String did,Date time) throws NoHostException, IOException, DeviceNotFoundException
	{
		FaultHistory faults=null;
		InputStream in=null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_GET_FAULT_HISTORY+PARAM_DID+did;
		Log.d(TAG, surl);
		try 
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			JSONObject json=parseToJSON(in);
			
			String message=json.get(KEY_RESPONSE).toString();
			if(message.equals(ACK))
			{
				faults=new FaultHistory(did);
				JSONArray array = json.getJSONArray(KEY_FAULT_LIST);
				JSONObject obj;
				Fault fault;
				for(int i=0;i<array.length();i++)
				{
					obj=array.getJSONObject(i);
					String fid=obj.getString(KEY_FID);
					String content=obj.getString(KEY_FAULT_CONTENT);
					String faultTime=obj.getString(KEY_FAULT_TIME);
					Date date=dateFormat.parse(faultTime);
					fault=new Fault(fid,did,content,date);
					faults.addFault(fault);
				}
			}
			else if(message.equals(MSG_NO_DID))
			{
				throw new DeviceNotFoundException();
			}
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
			Log.d(TAG, "connection disconnect!");
		}
		return faults;
	}
	
	/**
	 * 获取设备详细信息
	 * @param 
	 * did 设备号
	 * @return 设备对象
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws DeviceNotFoundException 
	 * */
	public Device getDevice(String did) throws NoHostException, IOException, DeviceNotFoundException
	{
		Device device =null;
		InputStream in=null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_GET_DEVICE+PARAM_DID+did;
		Log.d(TAG, surl);
		
		try
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			JSONObject json=parseToJSON(in);
			
			String message=json.get(KEY_RESPONSE).toString();
			if(message.equals(ACK))
			{
				String dname=json.getString(KEY_DNAME);
				String type=json.getString(KEY_DTYPE);
				String address=json.getString(KEY_DEVICE_ADDRESS);
				String qr=json.getString(KEY_DEVICE_QRCODE);
				
				device=new Device(did,dname,type,address,qr);
				
				JSONArray array=json.getJSONArray(KEY_CHECKCARD_LIST);
				CheckCard card;
				JSONObject obj;
				for(int i=0;i<array.length();i++)
				{
					obj=array.getJSONObject(i);
					card=new CheckCard();
					card.cid=obj.getString(KEY_CID);
					card.cName=obj.getString(KEY_CNAME);
					device.addCheckCard(card);
				}
			}
			else if(message.equals(MSG_NO_DID))
			{
				throw new DeviceNotFoundException();
			}
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
			Log.d(TAG, "connection disconnect!");
		}
		return device;
	}
	
	/**
	 * 确认完成任务
	 * @param
	 * tid 任务id
	 * @return 确认结果
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws TaskNotFoundException 
	 * @throws TaskUndoneException 
	 * */
	public Date finishTask(String tid) throws NoHostException, IOException, TaskNotFoundException, TaskUndoneException
	{
		Date finishTime = null;
		InputStream in=null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_FINISH_TASK+PARAM_TID+tid;
		Log.d(TAG, surl);
		
		try 
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			JSONObject json =parseToJSON(in);
			String message=json.getString(KEY_RESPONSE);
			if(message.equals(ACK))
			{
				finishTime=dateFormat.parse(json.getString(KEY_FINISH_TIME));
			}
			else if(message.equals(MSG_NO_TID))
			{
				throw new TaskNotFoundException();
			}
			else if(message.equals(MSG_TASK_UNDONE))
			{
				throw new TaskUndoneException();
			}
		}
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			in.close();
			conn.disconnect();
		}
		
		return finishTime;
	}
	
	/**
	 * 提交设备抄录信息
	 * @param
	 * tid 任务id
	 * did 设备id
	 * checkCards 设备条目列表（含抄录结果）
	 * @return 上传响应
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws TaskNotFoundException 
	 * @throws DeviceNotFoundException 
	 * */
	public boolean postDeviceResult(String tid,String did,Vector<CheckCard> checkCards) throws NoHostException, IOException, TaskNotFoundException, DeviceNotFoundException
	{
		boolean result = false;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_POST_RESULT;
		Log.d(TAG, surl);
		HttpPost httpRequest = new HttpPost(surl);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		
		try
		{
			JSONArray values=new JSONArray();
			JSONObject obj;
			for(CheckCard card:checkCards)
			{
				obj=new JSONObject();
				obj.put(KEY_CID, card.cid);
				obj.put(KEY_CVALUE, card.cValue);
				values.put(obj);
			}
			JSONObject json=new JSONObject();
			json.put(KEY_TID, tid);
			json.put(KEY_DID, did);
			json.put(KEY_CHECKCARD_LIST, values);
			
			params.add(new BasicNameValuePair(KEY_RESULT,json.toString()));
			
			httpRequest.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			int response=httpResponse.getStatusLine().getStatusCode();
			if(response==200)
			{
				String message = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				if(message.equals(ACK))
				{
					result=true;
				}
				else if(message.equals(MSG_NO_TID))
				{
					throw new TaskNotFoundException();
				}
				else if(message.equals(MSG_NO_DID))
				{
					throw new DeviceNotFoundException("Could not find a device in this task!");
				}
			}
			else
			{
				Log.e(TAG,""+response);
			}
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
		return result;
	}
	
	/**
	 * 提交缺陷
	 * @param
	 * did 设备id
	 * content 缺陷内容
	 * @return 含完整信息的Fault对象
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws DeviceNotFoundException 
	 * @throws InvalidUploadException 
	 * */
	public Fault postFault(String did,String content) throws NoHostException, IOException, DeviceNotFoundException, InvalidUploadException
	{
		Fault fault=null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_POST_FAULT;
		Log.d(TAG, surl);
		
		HttpPost httpRequest = new HttpPost(surl);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(KEY_DID, did));
		params.add(new BasicNameValuePair(KEY_FAULT_CONTENT, content));
		try 
		{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				String strResult = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				JSONObject json=new JSONObject(strResult);
				String message=json.getString(KEY_RESPONSE);
				if(message.equals(ACK))
				{
					fault=new Fault(did,content);
					fault.fid=json.getString(KEY_FID);
					fault.date=dateFormat.parse(json.getString(KEY_FAULT_TIME));
				}
				else if(message.equals(MSG_NO_DID))
				{
					throw new DeviceNotFoundException();
				}
				else if(message.equals(MSG_NULL_FAULT))
				{
					throw new InvalidUploadException();
				}
			}
			else
			{
				Log.e(TAG,""+httpResponse.getStatusLine().getStatusCode());
			}
		}
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fault;
	}
	/**
	 * 提交二维码信息供服务器检查
	 * @param
	 * did 设备id
	 * qrcode 解码后的二维码字符串
	 * return 匹配结果
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws DeviceNotFoundException 
	 * */
	public String checkQRCode(String did, String qrcode) throws NoHostException, IOException, DeviceNotFoundException 
	{
		String result=null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_CHECK_QRCODE;
		Log.d(TAG, surl);
		HttpPost httpRequest = new HttpPost(surl);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(KEY_DID, did));
		params.add(new BasicNameValuePair(KEY_DEVICE_QRCODE, qrcode));
		try
		{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			int response=httpResponse.getStatusLine().getStatusCode();
			if(response==200)
			{
				String message = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				if(message.equals(ACK))
				{
					result="二维码认证成功！";
				}
				else if(message.equals(MSG_NO_DID))
				{
					throw new DeviceNotFoundException();
				}
			}
		} 
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获取任务请求
	 * @param
	 * tid 任务id
	 * @return 任务对象
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws TaskNotFoundException 
	 * */
	public Task getTask(String tid) throws NoHostException, IOException, TaskNotFoundException
	{
		Task task=null;
		InputStream in = null;
		String surl=generateURL(hostIp,hostPort);
		surl+=REQUEST_GET_TASK+PARAM_TID+tid;
		Log.d(TAG, surl);
		
		try 
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			JSONObject json=parseToJSON(in);
			
			String message=json.get(KEY_RESPONSE).toString();
			if(message.equals(ACK))
			{
				String tname=json.getString(KEY_TNAME);
				TaskState state = TaskState.valueOf(json.getString(KEY_STATE));
				int deviceNumber=Integer.parseInt(json.getString(KEY_DEVICE_COUNT));
				Date rt = dateFormat.parse(json.getString(KEY_RELEASE_TIME));
				Date ft = dateFormat.parse(json.getString(KEY_FINISH_TIME));
				Date dl = dateFormat.parse(json.getString(KEY_DEADLINE));
				
				task=new Task(tid,tname,deviceNumber,state,rt,ft,dl);
				
				JSONArray devices=json.getJSONArray(KEY_DEVICELITES);
				DeviceLite device;
				JSONObject obj;
				for(int i=0;i<devices.length();i++)
				{
					obj=devices.getJSONObject(i);
					device=new DeviceLite();
					device.did=obj.get(KEY_DID).toString();
					device.dName=obj.getString(KEY_DNAME);
					device.dAddress=obj.getString(KEY_DEVICE_ADDRESS);
					device.dType=obj.getString(KEY_DTYPE);
					
					task.addDevice(device);
				}
			}
			else if(message.equals(MSG_NO_TID))
			{
				throw new TaskNotFoundException();
			}
			
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
			Log.d(TAG, "connection disconnect!");
		}
		return task;
	}
	/**
	 * 登录请求
	 * @param
	 * wid 工人号
	 * pwd 工人密码
	 * @return 工人对象
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws WorkerNotFoundException 
	 * @throws WrongPwdException 
	 * */
	public Worker login(String wid,String pwd) throws NoHostException, IOException, WorkerNotFoundException, WrongPwdException
	{
		Worker worker=null;
		InputStream in = null;
		String surl=generateURL(hostIp,hostPort);
		surl=surl+REQUEST_LOGIN+PARAM_WID+wid+"&"+PARAM_PWD+pwd;
		Log.d(TAG, surl);
		try 
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			JSONObject json=parseToJSON(in);
			
			String message=json.get(KEY_RESPONSE).toString();
			if(message.equals(ACK))
			{
				String wname=json.getString(KEY_WNAME);
				Log.d(TAG, "wname:"+wname);
				worker=new Worker(wid,wname,pwd);
			}
			else if(message.equals(MSG_NO_WID))
			{
				throw new WorkerNotFoundException();
			}
			else if(message.equals(MSG_WRONG_PWD))
			{
				throw new WrongPwdException();
			}
			

		} 
		catch (MalformedURLException e)
		{
			
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
			Log.d(TAG, "connection disconnect!");
		}
		return worker;
	}
	
	/**
	 * 获取工人任务列表请求
	 * @param
	 * wid 工人号
	 * state 任务状态
	 * date	如果状态为Task.TaskState.UNDONE date为发布时间 releaseTime;
	 * 		如果状态为Task.TaskState.DONE date为完成时间 finishTime;
	 * 		如果状态为Task.TaskState.OUTDATED date为期限时间 deadline;
	 * @return 任务列表对象
	 * @throws NoHostException 
	 * @throws IOException 
	 * @throws WorkerNotFoundException 
	 * */
	public TaskList getTasklist(String wid,String state,Date date) throws NoHostException, IOException, WorkerNotFoundException 
	{
		TaskList tasklist=null;
		InputStream in = null;
		
		String surl=generateURL(hostIp,hostPort);
		surl=surl+REQUEST_GET_TASKLIST+PARAM_WID+wid+"&"+PARAM_STATE+state;
		if(date!=null)
		{
			String time=dateFormat.format(date);
			surl=surl+"&"+PARAM_DATE+time;
		}
		Log.d(TAG, surl);
		
		try 
		{
			url=new URL(surl);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Accept", "text/json");
			
			conn.connect();
			int response=conn.getResponseCode();
			Log.d(TAG, "response:"+response);
			
			in=conn.getInputStream();
			Log.d(TAG, "TaskState:"+state);
			JSONObject json=parseToJSON(in);
			//JSONArray json=parseToJSONArray(in);
			
			String message=json.getString(KEY_RESPONSE).toString();
			if(message.equals(ACK))
			{
				Log.d(TAG, ACK);
				JSONArray array=json.getJSONArray(KEY_TASKLIST);
				Log.d(TAG, "json array:"+array);
				
				TaskState taskState=TaskState.valueOf(state);
				tasklist=new TaskList(wid,taskState);
				int len=array.length();
				TaskLite task;
				JSONObject obj;
				for(int i=0;i<len;i++)
				{
					obj=array.getJSONObject(i);
					String tid=obj.getString(KEY_TID);
					String tname=obj.getString(KEY_TNAME);
					String releaseTime=obj.getString(KEY_RELEASE_TIME);
					String finishTime=obj.getString(KEY_FINISH_TIME);
					String deadLine=obj.getString(KEY_DEADLINE);
					int deviceNumber=Integer.parseInt(obj.getString(KEY_DEVICE_COUNT));
					Date rt= (Date) dateFormat.parse(releaseTime);
					Date ft= (Date) dateFormat.parse(finishTime);
					Date dl= (Date) dateFormat.parse(deadLine);
					
					task=new TaskLite(tid,tname,rt,ft,dl,deviceNumber,taskState);
					tasklist.addTask(task);
				}
			}
			else if(message.equals(MSG_NO_WID))
			{
				throw new WorkerNotFoundException();
			}
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
			Log.d(TAG, "connection disconnect!");
		}
		return tasklist;
	}
	
	
	/**
	 * 把输入流转化成JSON对象
	 * @param 
	 * in 输入流
	 * @return JSON对象
	 * @throws IOException
	 * */
	private JSONObject parseToJSON(InputStream in) throws IOException
	{
		JSONObject jsonObject=null;
		int available=in.available();
		byte[] buffer= new byte[available];
		in.read(buffer);
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		stream.write(buffer, 0, available);
		String json=stream.toString("utf-8");
		Log.d(TAG, "json:"+json);
		if(json!=null)
		{
			try
			{
				jsonObject=new JSONObject(json);
			} 
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			finally
			{
				in.close();
				stream.close();
			}
		}
		in.close();
		stream.close();
		return jsonObject;
	}
	
}
