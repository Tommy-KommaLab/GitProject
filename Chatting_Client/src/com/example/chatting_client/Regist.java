package com.example.chatting_client;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * クラス名：Regist
 *　関数名：onCreate()
 *　パラメタ：activityのhandler関連変数。
 *　パラメタ：登録前同じid,nicknameがあるか検索関連変数
 *　パラメタ：登録関連変数
 *　パラメタ：他のActivityに値の伝送関連変数
 *　パラメタ：判断関連変数
 *　activityにあるwidgetなどをMainActivityから操作ができるように関連変数と連結する。 
 */
public class Regist extends Activity {
	AllMemberSelect m_thread;		   //登録前同じid,nicknameがあるか検索関連変数
	RegistMember r_thread;			   //登録関連変数
	String allMember;
	Intent intent;					//他のActivityに値の伝送関連変数
	Boolean registFlag = false ;
	Boolean test = false;			   //判断関連変数
	
	EditText id, pass, rePass, nickname;
	Button btRegist;					//activityのhandler関連変数。	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memregist);

		id = (EditText) findViewById(R.id.txRegistId);
		pass = (EditText) findViewById(R.id.txRegistPass);
		rePass = (EditText) findViewById(R.id.txReRegistPass);
		nickname = (EditText) findViewById(R.id.txNickName);
		btRegist = (Button) findViewById(R.id.btRegit);
		ClickListener cl = new ClickListener();
		btRegist.setOnClickListener(cl);
										//activityのhandlerを設定。											
	}
	
	/*
	 * クラス名：ClickListener
	 *　関数名：onClick()
	 *　パラメタ：例外処理関連変数
	 *　例外処理関連 
	 */

	class ClickListener implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				String a = "";
												//例外処理
				if (v.getId() == R.id.btRegit) {
					if (id.getText().toString() == ""
							|| nickname.getText().toString().equals("")
							|| pass.getText().toString().equals("")
							|| rePass.getText().toString().equals("")) {
						Toast.makeText(Regist.this, "Check your units",
								Toast.LENGTH_LONG).show();
					} else if (!(pass.getText().toString().equals(rePass
							.getText().toString()))) {
						Toast.makeText(Regist.this,
								"Check your pass , rePass ", Toast.LENGTH_LONG)
								.show();
					} else {
						m_thread = new AllMemberSelect();
						m_thread.start();

						if (registFlag) {
							Toast.makeText(Regist.this, "Success Regist",
									Toast.LENGTH_LONG).show();
							r_thread = new RegistMember();
							r_thread.start();
							startActivity(new Intent(Regist.this, Login.class));
							finish();
						}						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * クラス名：RegistMember
	 *　関数名：run()
	 *　パラメタ：例外処理関連変数
	 *　サーバに接続し、ユーザが記入したid,passWord,NickNameをデータベースに入れる。
	 */

	class RegistMember extends Thread{
		String success;
		public void run()
		{
			try {				
				String url = "http://192.168.11.26/chatting/registMember.php?" ;
				url += "id=" + id.getText() + "&";
				url += "pass=" + pass.getText() + "&";
				url += "nickname=" + nickname.getText();
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
				if(conn !=null){
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){						
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String line = br.readLine();
						StringBuilder html = new StringBuilder();						
						while (true) {
							line = br.readLine();
							if (line == null) {
								break;
							}
							html.append(line + '\n');
						}
						br.close();					
					    conn.disconnect();
					    
						html.append(line);		
						success = html.toString();
						test = true;		
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	/*
	 * クラス名：AllMemberSelect
	 *　関数名：run() , handleMessage()
	 *　パラメタ：例外処理関連変数,
	 * パラメタ：サーバ接続関連変数
	 *　登録前サーバに接続し、ユーザが記入したid,NickNameと同じちがデータベースにあるか検索する。
	 */
	
	class AllMemberSelect extends Thread {
		public void run() {
			StringBuilder html = new StringBuilder();
			try {
				String url = "http://192.168.11.26/chatting/allMemberSelect.php";
				HttpURLConnection conn = (HttpURLConnection) new URL(url)
						.openConnection();
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(conn.getInputStream()));
						String line = br.readLine();
						if (line == null) {
							return;
						}
						html.append(line + '\n');
						while (true) {
							line = br.readLine();
							if (line == null) {
								break;
							}
							html.append(line + '\n');
						}
						br.close();
					}
					conn.disconnect();
					allMember = html.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			handler.sendMessage(msg);
		}
	}
	public Handler handler = new Handler()
	{	
		@Override
		public void handleMessage(Message msg)
		{
			try {
				JSONArray ja = new JSONArray(allMember);
				for (int i = 0; i < ja.length(); i++) {
					JSONObject order = ja.getJSONObject(i);
					if (order.getString("id").equals(id.getText().toString())) {
						Toast.makeText(Regist.this, "reWrite id",
								Toast.LENGTH_SHORT).show();
					} else if (order.getString("nickname").equals(
							nickname.getText().toString())) {
						Toast.makeText(Regist.this, "reWrite nickName",
								Toast.LENGTH_SHORT).show();
					} else {
						registFlag =true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
}

			
		
		
