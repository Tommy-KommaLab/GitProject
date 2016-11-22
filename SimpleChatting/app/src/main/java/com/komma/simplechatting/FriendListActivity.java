package com.komma.simplechatting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendListActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String SERVERDNS = "http://ec2-52-78-81-140.ap-northeast-2.compute.amazonaws.com";

    private static final int RECIVE_FRIENDNICK_SUCCESS = 3;
    private static final int RECIVE_FRIENDNICK_FAIL = 4;

    ListView listView;
    EditText friendId;
    TextView tvFriendId;
    Button   btSearchFriendId;

    ArrayList<String> listId = null;
    String id = null;
    String friendNick = null;

    SendMessageHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friendId = (EditText)findViewById(R.id.txFriend);
        listId = new ArrayList<>();
        listView = (ListView)findViewById(R.id.list);
        btSearchFriendId = (Button)findViewById(R.id.btSearch);

        btSearchFriendId.setOnClickListener(this);

        handler = new SendMessageHandler();


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btSearch:

                break;
        }
    }

    class SendMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case RECIVE_FRIENDNICK_SUCCESS :


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
                        SERVERDNS + "/chatting/allFriendSelect.php?id=" + id).openConnection();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String line = br.readLine();
                        if(line == null)
                            return;


                        

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
