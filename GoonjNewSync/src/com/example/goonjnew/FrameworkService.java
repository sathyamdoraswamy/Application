package com.example.goonjnew;

import android.app.Service;
import android.net.*;
import android.content.Intent; //import android.os.Bundle;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;
import android.content.Context; //import android.view.View;
import android.content.SharedPreferences.Editor;
//import android.widget.EditText;
//import android.widget.Button;
//import android.widget.TextView;

import java.util.*;
import java.net.*;
import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

// WE'LL HAVE A SEPARATE SERVICE FOR PULLING UPDATES.. WE'LL SEND A MESSAGE IN A CERTAIN FORMAT AND
// READING THE MESSAGE, WE'LL BE ABLE TO INTERPRET WHAT IT SAYS, AND DO WHATEVER IS NEEDED..


/**  1. When we switch off and on, we should be able to start communicating from the exact point where it was left off. Atleast, abort what was happening and restart it.
 *  2. Every 20 seconds, check if GPRS available.. Only then, you should transmit.
 *  3. Let us try putting a signal strength bar (cant transmit if sig str <val)
 *  4. if server not there, keep waiting and keep trying till server becomes available..
 *  5. Maybe we'll fix the user id..*/

public class FrameworkService extends Service {
	private static final String TAG = "FrameworkService";
	private FrameworkThread thread;
	Helper dh;
	String user;
	String deviceId;
	static String storagePath;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub,deviceId

		return null;
	}

	@Override
	public void onCreate() {
		// Intent it=this.getIntent();
		// long
		// id=Long.parseLong(callingIntent.getStringExtra("com.androidbook.imgtwit.ID"));
		Log.d(TAG, "Service Created");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
		// Intent callingIntent=getIntent();
		int id = Integer.parseInt(intent.getStringExtra("com.androidbook.FrameworkService.UID"));
		dh = new Helper((Context) this, id);
		ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		user = "SATHYAM";//getUser();
		deviceId = Secure.getString(((Context)this).getContentResolver(), Secure.ANDROID_ID);
		storagePath = Environment.getExternalStorageDirectory().toString() + "/framework";		
		thread = new FrameworkThread(dh, 13241, connMgr, user, deviceId);
		thread.start();
	}
	
	private String getUser() 
    {
        SharedPreferences prefs = getSharedPreferences("USER", 0);
        String value = prefs.getString("user", null);                 
    	return value;        
    }
}

class FrameworkThread extends Thread {
	String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	DatagramSocket dg;
	DatagramPacket sendPacket;
	Socket connection;	
	InetAddress server;
	Helper dh;
	int client_userid;
	ConnectivityManager cm;
	String deviceId;
	String user;
	public FrameworkThread(Helper h, int id, ConnectivityManager cm, String user, String deviceId) {
		try {
			server = InetAddress.getByName(GlobalData.IPAdd);
			client_userid = id;
			dh = h;
			this.cm = cm;
			this.deviceId = deviceId;
			this.user = user;//"SATHYAM";
			// byte[] data = "MSG SENT BY SERVICE".getBytes();
			// sendPacket = new DatagramPacket(data, data.length,server,8011);
			//
			// dg=new DatagramSocket();		
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public boolean checkNetworkStatus() {

		final android.net.NetworkInfo wifi = cm
		.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = cm
		.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


		if (wifi.isAvailable()) { return true;

		} else

			if (mobile.isAvailable()) {
				return true;

			} else {
				return false;
			}

	}

//	public static final byte[] intToByteArray(int value) {
//		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
//				(byte) (value >>> 8), (byte) value };
//	}
//
//	public static final byte[] shortToByteArray(short value) {
//		return new byte[] { (byte) (value >>> 8), (byte) value };
//	}
//
//	public static final int byteArrayToInt(byte[] b) {
//		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
//		+ (b[3] & 0xFF);
//	}

	public boolean haveInternet() {
		try {
			HttpGet request = new HttpGet();
			HttpParams httpParameters = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters, 500);
			HttpConnectionParams.setSoTimeout(httpParameters, 500);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			request.setURI(new URI("http://www.cse.iitd.ernet.in/~aseth/"));
			HttpResponse response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void commit(Transaction t) {
		try {
			for (int i = 0; i < t.noOfBundles; i++) {
				String record = "";
				String query = "";            
				Bundle b = t.bundles[i];
				byte[] recd = b.data;	
				record = new String(recd);
				System.out.println("Record :\n" + record);
				String rec[] = record.split("\n");    
				long recordid = Long.parseLong(rec[0]);
				String groupid = rec[1];
				String key = rec[2];            
				String value = rec[3];            
				String user = rec[4];            
				String datatype = rec[5];            
				long timestamp = Long.parseLong(rec[6]);                        
				if (datatype.equals("file")) {
					int l = Integer.parseInt(value.substring(0,value.indexOf(' ')));
					String fileName = value.substring(value.indexOf(' ')+1);
					fileName = deviceId+"_"+t.transactionId+"_"+i+"_"+fileName;
					String path = FrameworkService.storagePath + "/" + fileName;
					File f = new File(path);
					FileOutputStream fos = new FileOutputStream(f);               
					for (int j = 1; j <= l; j++) {
						b = t.bundles[i+j];
						fos.write(b.data);
					}
					fos.close();
					i += l;
					value = path;
					query = "insert into serverdb values("+recordid+","+groupid+",'"+key+"','"+value+"','"+user+"','"+datatype+"',"+timestamp+",'Y')";
					System.out.println("Query :"+query);
					dh.putToRepository(recordid,groupid,key,value,user,datatype,timestamp,"Y");
					//Framework.executeQuery(query);
				} else {
					query = "insert into serverdb values("+recordid+","+groupid+",'"+key+"','"+value+"','"+user+"','"+datatype+"',"+timestamp+",'Y')";
					System.out.println("Query :"+query);
					dh.putToRepository(recordid,groupid,key,value,user,datatype,timestamp,"Y");
					//Framework.executeQuery(query);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public boolean update(Socket connection, ObjectInputStream ois, ObjectOutputStream oos)
	{
		try {		
			byte[] buffer = "UPDATE".getBytes();
			oos.writeObject(buffer);
			buffer = (deviceId + " " + user).getBytes();
			oos.writeObject(buffer);
			buffer = (byte[]) ois.readObject();
			String reply = (new String(buffer));
			if(reply.equals("NULL"))
				return true;
			String[] namespaces = reply.split("\n");
			Long[] rowids = dh.getRowIds(namespaces);
			int l = rowids.length;
			String ids;
			if(l==1)
				ids = Long.toString(rowids[0]);
			else
			{
				ids = Long.toString(rowids[0]);
				int i;
				for(i=1;i<l;i++)
				{
					ids.concat("\n"+Long.toString(rowids[i]));
				}
			}			
			System.out.println("IDs : "+ids);
			buffer = ids.getBytes();
			oos.writeObject(buffer);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean pull(Socket connection, ObjectInputStream ois, ObjectOutputStream oos)
	{			
		try { 											
			Transaction t = null;			
			String fileName = extStorageDirectory + "/framework/pull.txt";			
			File f = new File(fileName);		
			try {
				boolean flag = true;
				if (f.exists()) {
					byte[] buffer = "PULL".getBytes();
					oos.writeObject(buffer);
					System.out.println("PULL packet sent!!!");						
					ObjectInputStream fin = new ObjectInputStream(new FileInputStream(f));
					t = (Transaction) fin.readObject();
					fin.close();					
					boolean result = f.delete();
					System.out.println("Delete event : "+result);
                    Bundle b = new Bundle();
                    b.userId = 1;
                    b.transactionId = -1;
                    b.bundleType = 5;// RETRANS
                    b.noOfBundles = 1;
                    b.bundleNumber = -1;      
                    String s = Integer.toString(t.transactionId)+"\n"+Integer.toString((t.bundleNo+1));
                    byte[] bb = s.getBytes();
                    int size = bb.length;
                    b.bundleSize = bb.length;
                    b.data = new byte[size];
                    for(int j=0;j<size;j++)
                    {
                        b.data[j] = bb[j];
                    }
                    System.out.println("Sent retrans : " + b.transactionId + " " + b.noOfBundles + " " + b.bundleNumber + " " + b.bundleType + " " + new String(b.data));
                    oos.writeObject(b.getBytes());
                    oos.flush();	
                    System.out.println("RETRANS bundle sent");
					flag = true;
					//depending upon response receive broken  transaction or start receiving fresh transactions
					while(flag)
					{
						System.out.print("Going to read");
						Thread.sleep(1000);
						buffer = (byte[]) ois.readObject();
						System.out.println("Read");
						b = new Bundle();
						b.parse(buffer);
						System.out.println("Parsed");
						if(b.bundleType==3)
							break;
						t.addBundle(b);                                                
						if(b.bundleNumber==(b.noOfBundles-1))
						{                                                      
							commit(t);
							//t = new Transaction();
							flag = false;
						}
						Bundle ackb = new Bundle();
						ackb.createACK(b);
						System.out.println(ackb.transactionId + " " + ackb.noOfBundles + " " + ackb.bundleNumber + " " + ackb.bundleType);
						oos.writeObject(ackb.getBytes());
						oos.flush();
						System.out.println("Written!!!");
					}
				} 
				//update(connection, ois, oos);
				byte[] buffer = "PULL".getBytes();
				oos.writeObject(buffer);	
				System.out.println("PULL packet sent!!!");		
				Bundle b = new Bundle();
                b.userId = 1;
                b.transactionId = -1;
                b.bundleType = 4;//START
                b.noOfBundles = 1;
                b.bundleNumber = -1;
                b.bundleSize = 0;
                b.data = null;
                oos.writeObject(b.getBytes());
                oos.flush();
            	System.out.println("START bundle sent");
					//t = new Transaction();						
//					String b = Integer.toString(-1)+"\n"+Integer.toString(-1);
//					oos.writeObject(b.getBytes());
//					System.out.println("Sent starting bundle no. : -1 -1 ");				
				flag = true;
				System.out.println("Waiting to receive");                
				do {
					System.out.print("Going to read");
					Thread.sleep(1000);
					buffer = (byte[]) ois.readObject();
					System.out.println("Read");
					b = new Bundle();
					b.parse(buffer);
					System.out.println("Parsed");
					if(b.bundleType==3)
						break;
					if(b.bundleNumber==0)
					{
						t = new Transaction(b.transactionId,b.noOfBundles);
					}
					t.addBundle(b);                                              
					if(b.bundleNumber==(b.noOfBundles-1))
					{                                                      
						commit(t);                                                        
					}        
					Bundle ackb = new Bundle(); 
					ackb.createACK(b);
					System.out.println("Sent ack : " + ackb.transactionId + " " + ackb.noOfBundles + " " + ackb.bundleNumber + " " + ackb.bundleType);
					oos.writeObject(ackb.getBytes());
					oos.flush();
				} while(flag);                                                            
	
				//                    buffer = (byte[]) ois.readObject();
				//                    msg = new String(buffer);
				//                    System.out.println("Message is :" + msg);
			} catch (Exception e) {
				e.printStackTrace();
				//                    if(t!=null)
				//                    System.out.println(t.transactionId+" "+t.bundleNo+" "+t.noOfBundles);
				if(t!=null && t.bundleNo != (t.noOfBundles-1))
				{
					System.out.println("Writing broken transaction!!!");
					f.createNewFile();
					ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(f));
					fout.writeObject(t);
					fout.close();
				}
				return false;
			} 
		} catch(Exception e) {
			e.printStackTrace();
			try {
				sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}				
			return true;		
	}
	class Temp {
	int a;
	double b;
	long t;
	char c;
	String d;
	}
	public boolean push(Socket connection, ObjectInputStream ois, ObjectOutputStream oos)
	{		
		Transaction[] transactions = null;
		ACKThread ack = null;
		//File f = new File(extStorageDirectory + "/framework/status.txt");
//		Temp obj = new Temp();
//		obj.a = 1;
//		obj.b = 3.14;
//		obj.t = System.currentTimeMillis();
//		obj.c = 'S';
//		obj.d = "Hi";		
		try {				
			byte[] buffer = "PUSH".getBytes();
			oos.writeObject(buffer);
			System.out.println("PUSH packet sent!!!");
			buffer = (byte[]) ois.readObject();
            Bundle b = new Bundle();
            b.parse(buffer);
            if (b.bundleType == 5)//RETRANS
            {
				File f = new File(extStorageDirectory + "/framework/push.txt");
				if (!f.exists()) {
                    b = new Bundle();
                    b.userId = 1;
                    b.transactionId = -1;
                    b.bundleType = 3;//STOP
                    b.noOfBundles = 1;
                    b.bundleNumber = -1;
                    b.bundleSize = 0;
                    b.data = null;
                    oos.writeObject(b.getBytes());
                    oos.flush();                    
                }
				else
				{
					String[] info = (new String(b.data)).split("\n");
	                int tid = Integer.parseInt(info[0]);
	                int bno = Integer.parseInt(info[1]);
					ObjectInputStream fin = new ObjectInputStream(new FileInputStream(f));
					//Transaction[] t = new Transaction[1];
					Transaction[] tt = (Transaction[])fin.readObject();		
					fin.close();
					Transaction[] t = new Transaction[1];
					t[0] = tt[tid];
					if(t[0].bundles==null)
						t[0].organizeBundles();
					boolean result = f.delete();
					System.out.println("Delete event : "+result);								
					ack = new ACKThread(dh, t, ois, bno);
					//ACKThread ack = new ACKThread(tid, t.noOfBundles, ois, bno);
					ack.start();
					for (int bi = bno; bi < t[0].noOfBundles; bi++) {
						byte[] bundle = t[0].bundles[bi].getBytes();
						oos.writeObject(bundle);
						oos.flush();
						System.out.println("Sent bundle -> Transaction id : " + tid + " Bundle No. : " + bi);
						Thread.sleep(1000);
					}
					ack.join();
					//dh.setSynched(t[0].records);
				}
			}
            if (b.bundleType == 4)//START
            {
                //Transaction[] transactions = null;
				List<String[]> list = dh.selectNewRecords();			
				if (list!=null) {
					int l = list.size();
					Record[] records = new Record[l];
					int n = 0;
					String gid = "";
					for (int i = 0; i < l; i++) {
						records[i] = new Record();
						records[i].groupid = list.get(i)[0];
						records[i].key = list.get(i)[1];
						records[i].value = list.get(i)[2];
						records[i].user = list.get(i)[3];
						records[i].datatype = list.get(i)[4];
						records[i].timestamp = Long.parseLong(list.get(i)[5]);
						records[i].synched = "N";
						if (!(gid.equals(records[i].groupid))) {
							n++;
							gid = records[i].groupid;
						}
					}
					System.out.println("NO OF TRANSACTIONS:"+n);
					transactions = new Transaction[n];
					int t = 0, r = 0;
					transactions[t] = new Transaction();
					transactions[t].transactionId = t;//records[r].groupid;
					transactions[t].addRecord(records[r]);
					r++;
					while (r < records.length) {
						while (r < records.length && records[r].groupid.equals(records[r - 1].groupid)) {
							transactions[t].addRecord(records[r]);
							System.out.println(records[r].key);
							r++;
						}
						System.out.println("NO OF RECORDS :"+records.length);
						System.out.println("R VALUE :"+r);
						if (r < records.length) {
							t++;
							transactions[t] = new Transaction();
							transactions[t].transactionId = t;//records[r].groupid;
							transactions[t].addRecord(records[r]);
							r++;
						}
					}
					// Fetch all distinct group ids that are not synched
					// Get list of all records for each group id and form
					// transactions with increasing tid from 0 onwards
					// }
					ack = new ACKThread(dh, transactions, ois, 0);
					ack.start();
					for (t = 0; t < transactions.length; t++) {
						transactions[t].organizeBundles();
	//						ACKThread ack = new ACKThread(
	//								transactions[t].transactionId,
	//								transactions[t].noOfBundles, ois, 0);
	//						ack.start();
						for (int bi = 0; bi < transactions[t].noOfBundles; bi++) {
							byte[] bundle = transactions[t].bundles[bi].getBytes();
							oos.writeObject(bundle);
							oos.flush();
							System.out.println("Sent bundle -> Transaction id : " + t + " Bundle No. : " + bi);
							Thread.sleep(1000);
						}
	//						ack.join();
	//						dh.setSynched(transactions[t].records);
					}
					ack.join();
				}
				//Sending STOP message
	            b = new Bundle();
	            b.userId = 1;
	            b.transactionId = -1;
	            b.bundleType = 3;//STOP
	            b.noOfBundles = 1;
	            b.bundleNumber = -1;
	            b.bundleSize = 0;
	            b.data = null;
	            oos.writeObject(b.getBytes());
				// Fetch all distinct group ids that are not synched
				// Get list of all records for each group id and form
				// transactions with increasing tid from 0 onwards
				// Start transmission and ackthread like above
				// In case of disconnection save transaction objects to a file
				// Transaction t = new Transaction(tid,list);
				// String msg = t.transactionId+"\n"+t.noOfBundles;
				// oos.writeObject(msg.getBytes());
				// byte[] buffer = (byte[]) ois.readObject();
				// int b = Integer.parseInt(new String(buffer));
				// t.organizeBundles();
				// ACKThread ack = new ACKThread(tid,t.noOfBundles,ois,b+1);
				// ack.start();
				// t.send(oos, b+1);
				// ack.join();				
				transactions = null;
				ack = null;
	//				if (f.exists())
	//					f.delete();
	//			}
				sleep(10000);
            }
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Caught Outside");
			if(ack!=null)
			{
				try {
					ack.join();
					sleep(10000);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return false;
			}
//				 try
//				 {
//				 ObjectOutputStream fout = new ObjectOutputStream(new
//				 FileOutputStream(new File(extStorageDirectory + "/framework/push.txt")));
//				 fout.writeObject(transactions);
//				 /*fout.writeInt(transactions.length);
//				 System.out.println("Writing : "+transactions.length);
//				 for(int i=0;i<transactions.length;i++)
//				 fout.writeObject(transactions[i]);*/
//				 fout.close();
//				 }
//				 catch(Exception e1){}
		}			
		return true;		
	}
	
	public void run() {
		ObjectOutputStream oos;
		ObjectInputStream ois;
		Temp obj = new Temp();
		obj.a = 123;
		obj.b = 3.14;
		obj.t = 0;//System.currentTimeMillis();
		obj.c = 'A';
		obj.d = "GOONJ";	
		//dh.updateObject("f3f529cf-5c6f-40cf-97d9-13cc859b6223", obj, "Goonj");
		//dh.updateObject("f3f529cf-5c6f-40cf-97d9-13cc859b6223", obj, "Goonj");
		//dh.putObject(obj,"Goonj");
		while(true)  
		{
			try {
				connection = new Socket(server, 8011);
				connection.setSoTimeout(20000);
				ois = new ObjectInputStream(connection.getInputStream());
				oos = new ObjectOutputStream(connection.getOutputStream());
				boolean result = update(connection, ois, oos);
				result = pull(connection, ois, oos);
				System.out.println("PULL OVER!!!");				
				if(result)		
				{		  	
					obj.d = "GRAMVAANI";
					//dh.putObject(obj,"Goonj");
					push(connection, ois, oos);
					System.out.println("PUSH OVER!!!");
				}
				connection.close();									
//				String query = "UPDATE phone SET RECORDID=0 WHERE GROUPID='f3f529cf-5c6f-40cf-97d9-13cc859b6223'";
//				dh.runQuery(query);
//				break;				
			} catch(Exception e) {
				e.printStackTrace();
			}  
			try {  
				sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//break;
		}
	}	
}
	class ACKThread extends Thread {
		Transaction[] t;
		int tid;
		int noOfBundles;
		ObjectInputStream ois;
		int seq;
		Helper dh;	
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		public ACKThread(Helper dh, Transaction[] t, ObjectInputStream ois, int seq)
		{
			this.dh = dh;
			this.t = t;
			this.ois = ois;
			this.seq = seq;
		}
		public ACKThread(int tid, int noOfBundles, ObjectInputStream ois,
				int seq) {
			this.tid = tid;
			this.noOfBundles = noOfBundles;
			this.ois = ois;
			this.seq = seq;		
		}

		public void run() {
			System.out.println("ACK STARTED");
			Bundle b = null;
			int i =0, j;
			try
			{
			for(i=0;i<t.length;i++)
			{
				String timestamp = "";
				for(j=seq;j<t[i].noOfBundles;j++)	
				{
					do {
						System.out.println("SEQ ACK : " + t[i].noOfBundles);
						byte[] buffer = (byte[]) ois.readObject();
						b = new Bundle();
						b.parse(buffer);
						if(b.bundleNumber==b.noOfBundles-1)
							timestamp = new String(b.data);
						System.out.println("ACK WAITING : " + j);
						System.out.println("ACK RECEIVED : " + b.bundleNumber);
						System.out.println(t[i].transactionId + " " + t[i].noOfBundles + " " + j);
					} while (!b.isAcknowledgement(t[i].transactionId, t[i].noOfBundles, j));
				}
				System.out.println("TIMESTAMP RECEIVED IS :" + timestamp);
				long ts = Long.parseLong(timestamp);
				dh.setSynched(ts, t[i].records);
				seq = 0;
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ObjectOutputStream fout;
				try {
					fout = new ObjectOutputStream(new FileOutputStream(new File(extStorageDirectory + "/framework/push.txt")));
					fout.writeObject(t);
					System.out.println("-----------------------------------------------------");
					System.out.println("Written broken transaction!!!");
					System.out.println("-----------------------------------------------------");
					fout.close();
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			 
				 /*fout.writeInt(transactions.length);
				 System.out.println("Writing : "+transactions.length);
				 for(int i=0;i<transactions.length;i++)
				 fout.writeObject(transactions[i]);*/
				 
			}
//			int j = seq;
//			
//			try {
//				for (j = seq; j < noOfBundles; j++) {
//					do {
//						System.out.println("SEQ ACK : " + noOfBundles);
//						byte[] buffer = (byte[]) ois.readObject();
//						b = new Bundle();
//						b.parse(buffer);
//						System.out.println("ACK WAITING : " + j);
//						System.out.println("ACK RECEIVED : " + b.bundleNumber);
//						System.out.println(tid + " " + noOfBundles + " " + j);
//					} while (!b.isAcknowledgement(tid, noOfBundles, j));			
//				}
//			} catch (Exception e) {
//				e.printStackTrace();						
//			}
			System.out.println("ACK ENDED");
		}
	}