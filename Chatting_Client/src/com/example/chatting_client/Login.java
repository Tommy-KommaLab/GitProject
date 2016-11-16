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
 * �N���X���FLogin
 *�@�֐����FonCreate(), checkId()
 *�@�p�����^�Factivity��handler�֘A�ϐ��B
 *�@�p�����^�F��ʂ����������߂̕ϐ��B
 *�@�p�����^�FId�ƃp�X���[�h�̔��f���֘A�ϐ�
 *�@ ���[�U���L������id,passWord�ƃf�[�^�x�[�X�ɓ��ꂽ���Ɣ�r���A���O�C������B
 */

public class Login extends Activity {
	CheckId ckId;
	Intent intent; // ��ʂ����������߂̕ϐ��B
	Boolean flag=false; // Id�ƃp�X���[�h�̔��f���֘A�ϐ��B�@false : wrong password or id
					// true : Correct
	EditText txId;
	EditText txPass; // activity��handler�֘A�ϐ��B
	String checked;
	String nickName;
	String id,pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// editText��Button���̐ݒ�B
		txId = (EditText) findViewById(R.id.txId);
		txPass = (EditText) findViewById(R.id.txPass);	
	}
	public void OnClickMethod(View v)
	{
		switch(v.getId())
		{
		// Login�{�^���������ƋL������id��password���T�[�o�ɂ�����Ɣ�r���Č��ʂ����炤�B		
		case R.id.btLogin: 
			id = txId.getText().toString();
			pass = txPass.getText().toString();
			
			ckId = new CheckId();
			ckId.start();	
			checkId();
			// ���ʂ�true��������login��ʂ�chatting��ʂɕς��܂��B		
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
		// ���ʂ�false�������獡�̉�ʂɃ��b�Z�[�W��\������B
		else  {
			Toast.makeText(Login.this, "Wrong ID or PassWord",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * �N���X���FCheckId�@�֐����Frun()�@�p�����^�FHttpURLConnection conn �T�[�o�ɐڑ��֘A�ϐ��B
	 * �@�p�����^�FBufferedReader br�@�T�[�o�̏��̓ǂݍ��ފ֘A�ϐ��B�@�p�����^�FStringBuilder
	 * html�@�T�[�o�̏��̋L���֘A�ϐ��B�@�T�[�o�ɂ���id,password�̏����L�����邽�߃T�[�o�ɐڑ�����B �@
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
	 * �@�֐����FcheckId()�@�p�����^�FString id �T�[�o�ɂ���id���̋L���֘A�ϐ��B�@�p�����^�FString pass
	 * �T�[�o�ɂ���password���̋L���֘A�ϐ��B�@�@�T�[�o�ɂ���id,password�̏��Ɓ@�L�����������r���ALogin�𔻒f����B �@
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