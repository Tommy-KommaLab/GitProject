package com.example.chatting_client;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
 
/*
* クラス名：Chatting
*　関数名：onCreate(), connect(), showDialog()
*　パラメタ：activityのhandler関連変数。　
*　パラメタ：socket関連変数。
*　パラメタ：画面にchatting内容の表示関連変数。
*　activityにあるwidgetなどをMainActivityから操作ができるように関連変数と連結する。 
*/
public class Chatting extends Activity{
	private final static String BR = // 改行
			System.getProperty("line.separator");
	
	private final static String IP = "192.168.11.26";//"192.168.1.113";
	
	private final static int PORT = 6001;				//IPとPORTを指定。	
	Chatting mainac;
	ScrollView sView;
	Button btSend;
	TextView mainText;
	TextView txId;
	EditText txSend;			//activityのhandler関連変数。
	
	Socket socket;
	InputStream in;
	OutputStream out; 			//socket関連変数。
	String id;	
	String friend;
	Boolean flagMyText;	
	
	private final Handler handler = new Handler();
	private String txtReceive;	//画面にchatting内容の表示関連変数。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		id= intent.getStringExtra("id");		
		friend = intent.getStringExtra("friend");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		mainac=this;
		//editTextとButton等の設定。		
		sView = (ScrollView) findViewById(R.id.ScrollView);
		btSend = (Button)findViewById(R.id.btSend);
		btSend.setText("送信");
		mainText = (TextView)findViewById(R.id.maintext);
		txSend = (EditText)findViewById(R.id.txSend);
		txId = (TextView)findViewById(R.id.txId);
		txId.setText(id);
		ClickListener cl = new ClickListener();
		btSend.setOnClickListener(cl);
		Thread clientThread = new Thread(){
			public void run(){
				try {
					connect(IP,PORT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		clientThread.start();
	}
	/*
	*　関数名：connect()
	*　パラメタ：activityのhandler関連変数。
	*　パラメタ:Socket socket : サーバにsocketで接続し、socketを使うよう関連変数。
	*　パラメタ:InputStream	in   : サーバにinputStreamをもらって　inputStream使うよう関連する。　
	*　パラメタ:OutputStream out : サーバにOutputStreamをもらって　OutputStream使うよう関連する。
	*/
	// サーバに接続し、もらったメッサージを画面に表示する。
	private void connect(String ip, int port)	{
		int size;
		byte[] w = new byte[10240];
		txtReceive = "";
		String a ="";
		try {
			socket = new Socket(ip,port);			
			in = socket.getInputStream();
			out = socket.getOutputStream();			
			while(socket != null && socket.isConnected())
			{				
				size = in.read(w);
				if(size <= 0)		
					continue;					
					txtReceive = new String(w, 0, size, "UTF-8");					
					handler.post(new Runnable(){
						public void run(){							 
							String text =  mainText.getText().toString() ;
							mainText.setText(text);
						
							if(flagMyText){
								mainText.setGravity(Gravity.RIGHT);
								flagMyText = false;
								mainText.setText(text + txtReceive + "\n");
							}
							
							mainText.setGravity(Gravity.LEFT);
							mainText.setText(text + txtReceive + "\n");							
							sView.fullScroll(View.FOCUS_DOWN);							     
							}
					});
				}
						
		} catch (Exception e) {
			handler.post(new Runnable(){
				public void run(){
					Chatting.showDialog(mainac, "", "通信エラーです1");
				}
			});
		}
	}
	
	/*
	* クラス名：ClickListener
	*　関数名：onClick()
	*　パラメタ：activityのhandler関連変数。　
	*　パラメタ：socket関連変数。
	*　パラメタ：画面にchatting内容の表示関連変数。
	*　 chatting機能が画面に表示する。
	*/
	class ClickListener implements Button.OnClickListener{

		@Override
		public void onClick(View v) {
			try {				
			if(v == btSend)
			{
				if(socket != null && socket.isConnected())
				{					
					PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),true);
					
					long time = System.currentTimeMillis();
					Date date = new Date(time);
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					String thisTime = sdf.format(date);
					
					out.println(id +"  "+ txSend.getText().toString() +"   "+ thisTime);
					out.flush();
					flagMyText = true;
					txSend.setText("", TextView.BufferType.NORMAL);
				}
			}
			} catch (Exception e) {
				handler.post(new Runnable(){
					public void run(){
						Chatting.showDialog(mainac,"","通信エラーです2");
					}
				});
			}
		}				
	}
	/*
	*　関数名：showDialog()
	*　Dialogの背細注設定する。
	*/
	public static void showDialog(final Activity activity, String title, String text)
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(activity);
		ad.setTitle(title);
		ad.setMessage(text);
		ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.setResult(Activity.RESULT_OK);				
			}
		});
		ad.create();
		ad.show();
	}	
}
