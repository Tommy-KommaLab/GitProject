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
* �N���X���FChatting
*�@�֐����FonCreate(), connect(), showDialog()
*�@�p�����^�Factivity��handler�֘A�ϐ��B�@
*�@�p�����^�Fsocket�֘A�ϐ��B
*�@�p�����^�F��ʂ�chatting���e�̕\���֘A�ϐ��B
*�@activity�ɂ���widget�Ȃǂ�MainActivity���瑀�삪�ł���悤�Ɋ֘A�ϐ��ƘA������B 
*/
public class Chatting extends Activity{
	private final static String BR = // ���s
			System.getProperty("line.separator");
	
	private final static String IP = "192.168.11.26";//"192.168.1.113";
	
	private final static int PORT = 6001;				//IP��PORT���w��B	
	Chatting mainac;
	ScrollView sView;
	Button btSend;
	TextView mainText;
	TextView txId;
	EditText txSend;			//activity��handler�֘A�ϐ��B
	
	Socket socket;
	InputStream in;
	OutputStream out; 			//socket�֘A�ϐ��B
	String id;	
	String friend;
	Boolean flagMyText;	
	
	private final Handler handler = new Handler();
	private String txtReceive;	//��ʂ�chatting���e�̕\���֘A�ϐ��B

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		id= intent.getStringExtra("id");		
		friend = intent.getStringExtra("friend");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		mainac=this;
		//editText��Button���̐ݒ�B		
		sView = (ScrollView) findViewById(R.id.ScrollView);
		btSend = (Button)findViewById(R.id.btSend);
		btSend.setText("���M");
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
	*�@�֐����Fconnect()
	*�@�p�����^�Factivity��handler�֘A�ϐ��B
	*�@�p�����^:Socket socket : �T�[�o��socket�Őڑ����Asocket���g���悤�֘A�ϐ��B
	*�@�p�����^:InputStream	in   : �T�[�o��inputStream��������ā@inputStream�g���悤�֘A����B�@
	*�@�p�����^:OutputStream out : �T�[�o��OutputStream��������ā@OutputStream�g���悤�֘A����B
	*/
	// �T�[�o�ɐڑ����A����������b�T�[�W����ʂɕ\������B
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
					Chatting.showDialog(mainac, "", "�ʐM�G���[�ł�1");
				}
			});
		}
	}
	
	/*
	* �N���X���FClickListener
	*�@�֐����FonClick()
	*�@�p�����^�Factivity��handler�֘A�ϐ��B�@
	*�@�p�����^�Fsocket�֘A�ϐ��B
	*�@�p�����^�F��ʂ�chatting���e�̕\���֘A�ϐ��B
	*�@ chatting�@�\����ʂɕ\������B
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
						Chatting.showDialog(mainac,"","�ʐM�G���[�ł�2");
					}
				});
			}
		}				
	}
	/*
	*�@�֐����FshowDialog()
	*�@Dialog�̔w�ג��ݒ肷��B
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
