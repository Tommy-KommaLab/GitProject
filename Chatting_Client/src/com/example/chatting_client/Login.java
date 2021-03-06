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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
 * クラス名：Login
 *　関数名：onCreate(), checkId()
 *　パラメタ：activityのhandler関連変数。
 *　パラメタ：画面を換えすための変数。
 *　パラメタ：Idとパスワードの判断情報関連変数
 *　 ユーザが記入したid,passWordとデータベースに入れた情報と比較し、ログインする。
 */

public class Login extends Activity {
	CheckId ckId;
	Intent intent; // 画面を換えすための変数。
	Boolean flag=false; // Idとパスワードの判断情報関連変数。　false : wrong password or id
					// true : Correct
	EditText txId;
	EditText txPass; // activityのhandler関連変数。
	String checked;
	String nickName;
	String id,pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// editTextとButton等の設定。
		txId = (EditText) findViewById(R.id.txId);
		txPass = (EditText) findViewById(R.id.txPass);	
	}
	public void OnClickMethod(View v)
	{
		switch(v.getId())
		{
		// Loginボタンを押すと記入したidとpasswordをサーバにある情報と比較して結果をもらう。		
		case R.id.btLogin: 
			id = txId.getText().toString();
			pass = txPass.getText().toString();
			
			ckId = new CheckId();
			ckId.start();	
			checkId();
			// 結果がtrueだったらlogin画面がchatting画面に変えます。		
			break;		
			
		case R.id.btGoRegist :
			intent = new Intent(Login.this,Regist.class);					
			startActivity(intent);
			finish();
			break;		
		}
	}
	public void checkLogin()
	{
		if (flag) {
			intent = new Intent(Login.this, Friend.class);
			intent.putExtra("id", id);
			startActivity(intent);
			finish();
		}
		// 結果がfalseだったら今の画面にメッセージを表示する。
		else  {
			Toast.makeText(Login.this, "Wrong ID or PassWord",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * クラス名：CheckId　関数名：run()　パラメタ：HttpURLConnection conn サーバに接続関連変数。
	 * 　パラメタ：BufferedReader br　サーバの情報の読み込む関連変数。　パラメタ：StringBuilder
	 * html　サーバの情報の記憶関連変数。　サーバにあるid,passwordの情報を記憶するためサーバに接続する。 　
	 */

	class CheckId extends Thread {
		public void run() {
			StringBuilder html = new StringBuilder();
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(
						"http://192.168.11.26/chatting/allMemberSelect.php?"+"id="+id +"&pass="+pass).openConnection();
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
					checked = html.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * 　関数名：checkId()　パラメタ：String id サーバにあるid情報の記憶関連変数。　パラメタ：String pass
	 * サーバにあるpassword情報の記憶関連変数。　　サーバにあるid,passwordの情報と　記入した情報を比較し、Loginを判断する。 　
	 */
	public void checkId() {
		try {
			JSONArray ja = new JSONArray(checked);
			JSONObject order = ja.getJSONObject(0);
			if(order.getString("flag").equals("1"))
			{
				intent = new Intent(Login.this, Friend.class);
				intent.putExtra("id", nickName);
				startActivity(intent);
				finish();		
			}
			else
			{
				Toast.makeText(Login.this, "Wrong ID or PassWord",
						Toast.LENGTH_SHORT).show();
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}