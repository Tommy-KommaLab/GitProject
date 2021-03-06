package com.example.chatting_client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
* クラス名：Friend
*　関数名：onCreate(), OnClickMethod()
*　パラメタ：activityのhandler関連変数。
*　パラメタ：自分の仲間を探すためのクラスhandler
*　パラメタ：仲間関連変数。
*　activityにあるwidgetなどをMainActivityから操作ができるように関連変数と連結する。 
*/
public class Friend extends Activity {
	CheckFriend checkFriend; //自分の仲間を探すためのクラスhandler
	ArrayList<String> list;
	Intent intent;	
	String id;
	String getFriends;
							//仲間関連変数。
	EditText friend;	
	ListView listView;
	TextView tv;
	Button bt;				//activityのhandler関連変数。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		// editTextとButton等の設定。
		list = new ArrayList<String>();
		friend = (EditText) findViewById(R.id.txFriend);
		intent = getIntent();		
		id = intent.getStringExtra("id");
		//　自分の仲間を探すためのクラスのthread.start()
		checkFriend = new CheckFriend();
		checkFriend.start();
		//　探した仲間を流動的表示。
		try {
			JSONArray ja = new JSONArray(getFriends);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject order = ja.getJSONObject(i);
				list.add(order.getString("friendid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(new MyAdapter());
	}
	/*
	* クラス名：MyAdapter
	*　関数名：getCount(), getItem(), getItemId(), getView()
	*　パラメタ：仲間を流動的表示するためView, TextView等を生成関連変数。
	*　activityにあるwidgetなどをMainActivityから操作ができるように関連変数と連結する。 
	*/


	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {		return list.size();		}
		@Override
		public Object getItem(int position) {	return list.get(position);	}
		@Override
		public long getItemId(int position) {	return position;	}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//	仲間を流動的表示するためView, TextView等を生成
			LayoutInflater inf = getLayoutInflater();
			View v1 = inf.inflate(R.layout.list_item, null);
			tv = (TextView) v1.findViewById(R.id.txFriendid);
			bt = (Button) v1.findViewById(R.id.btFriendid);

			tv.setText(list.get(position));
			bt.setText(list.get(position));			
			return v1;
		}
	}
	/*
	*　関数名：OnClickMethod()
	*　パラメタ：ボタンclick関連変数。
	*　仲間を検索するボタン、仲間とchattingをするボタン 
	*/

	
	public void OnClickMethod(View v) {
		if (v.getId() == R.id.btSearch) {			
			intent = new Intent(Friend.this, Chatting.class);
			intent.putExtra("id", id);
			startActivity(intent);
			finish();
		} else {
			intent = new Intent(Friend.this, Chatting.class);
			intent.putExtra("friend", tv.getText());
			startActivity(intent);
			finish();
		}
	}
	
	/*
	* クラス名：CheckFriend
	*　関数名：run(),
	*　パラメタ：サーバの接続関連弁数
	*　サーバにある自分の仲間を検索し、結果を getFriendsに入れる。
	*/

	class CheckFriend extends Thread {
		public void run() {
			StringBuilder html = new StringBuilder();
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(
						"http://192.168.11.26/chatting/allFriendSelect.php?id="
								+ id).openConnection();
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
					getFriends = html.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}