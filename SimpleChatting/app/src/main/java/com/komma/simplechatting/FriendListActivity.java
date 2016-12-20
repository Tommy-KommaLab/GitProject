package com.komma.simplechatting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendListActivity extends AppCompatActivity implements View.OnClickListener{

    //AWS Tommy Server Public DNS
    private static final String SERVERDNS = "http://ec2-52-78-81-140.ap-northeast-2.compute.amazonaws.com";

    private static final int RECIVE_FRIENDNICK_SUCCESS = 3;     //DB로부터 친구 정보 잘 가져온 경우
    private static final int RECIVE_FRIENDNICK_FAIL = 4;        //DB로부터 친구 정보 수신 못한 경우

    ListView listView;
    EditText editTextfriendId;
    Button   btSearchFriendId;

    ArrayList<String> listId = null;
    String myId = null;
    String myNickName = null;
    String friendId = null;

    SendMessageHandler handler;
    CheckFriendThread checkFriend;

    static class ViewHolder {
        TextView    tvFriendId;
        Button      btFriendId;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        editTextfriendId = (EditText)findViewById(R.id.txFriend);
        listId = new ArrayList<>();
        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new MyAdapter());

        btSearchFriendId = (Button)findViewById(R.id.btSearch);

        btSearchFriendId.setOnClickListener(this);

        handler = new SendMessageHandler();

        //현재 로그인한 사용자 정보
        Intent intent = getIntent();
        myId = intent.getExtras().getString("ID");
        myNickName = intent.getExtras().getString("NICKNAME");

        //서버에 접속해서 사용자의 친구 정보 리스트를 받아온다.
        checkFriend = new CheckFriendThread();
        checkFriend.start();

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.btSearch:
                intent = new Intent(FriendListActivity.this, ChattingActivity.class);
                intent.putExtra("ID", myId);
                startActivity(intent);
                finish();
                break;
            case R.id.btFriendid:
                intent = new Intent(FriendListActivity.this, ChattingActivity.class);
                intent.putExtra("FRIENDID", friendId);
                startActivity(intent);
                finish();
                break;
        }
    }

    //Server 에서 Json 으로 수신한 친구 id 를 list 로 전환
    private ArrayList<String> getFriendIdList(String data) {

        JSONArray jarr = null;

        ArrayList<String> list = new ArrayList<>();

        try {
            jarr = new JSONArray(data);

            if (jarr.length() > 0) {

                for (int k = 0; k < jarr.length(); k++) {

                    JSONObject json = jarr.getJSONObject(k);
                    list.add(json.getString("idFRIEND"));
                }
            }

        }catch (JSONException e) {

            e.printStackTrace();
        }

        return list;
    }

    class SendMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case RECIVE_FRIENDNICK_SUCCESS :

                    //사용자의 친구 정보를 제대로 수신 했다면,
                    //List 를 갱신한다.
                    listId = getFriendIdList(msg.obj.toString());

                    break;
                case RECIVE_FRIENDNICK_FAIL :
                    Toast.makeText(FriendListActivity.this, "No corresponding nickname",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    class CheckFriendThread extends Thread {
        @Override
        public void run() {

            StringBuilder html = new StringBuilder();
            try{

                HttpURLConnection conn = (HttpURLConnection) new URL(
                        SERVERDNS + "/chatting/allFriendSelect.php?id=" + myId).openConnection();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String line = br.readLine();
                        if(line == null)
                            return;


                        html.append(line + '\n');

                        while(true) {
                            line = br.readLine();
                            if(line == null) {
                                break;
                            }
                            html.append(line + '\n');
                        }
                        br.close();

                    }
                    conn.disconnect();

                    String friendInfo = html.toString();
                    Message msg = handler.obtainMessage();

                    if(friendInfo.length() > 0){
                        msg.what = RECIVE_FRIENDNICK_SUCCESS;
                        msg.obj = friendInfo;       //친구 리스트를 JSON 으로 전달
                    } else {
                        msg.what = RECIVE_FRIENDNICK_FAIL;
                    }

                    handler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return  listId.size();
        }

        @Override
        public Object getItem(int i) {
            return listId.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_friendid, parent, false);
                holder = new ViewHolder();

                holder.tvFriendId = (TextView)convertView.findViewById(R.id.txFriendid);
                holder.btFriendId = (Button)convertView.findViewById(R.id.btFriendid);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvFriendId.setText(listId.get(position));
            holder.btFriendId.setText(listId.get(position));
            friendId = listId.get(position);

            return convertView;
        }


    }
}
