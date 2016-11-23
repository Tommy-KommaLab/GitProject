package com.komma.simplechatting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SERVERDNS = "http://ec2-52-78-81-140.ap-northeast-2.compute.amazonaws.com";

    private static final int LOGIN_SUCCESS = 1;
    private static final int LOGIN_FAIL = 2;

    EditText et_ID;
    EditText et_PW;

    SendMessageHandler handler;

    private String  nickName = null;
    private String  id = null;
    private String  pw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new SendMessageHandler();

        et_ID = (EditText)findViewById(R.id.txId);
        et_PW = (EditText)findViewById(R.id.txPass);

        Button btLogIn = (Button)findViewById(R.id.btLogin);
        Button btSignup = (Button)findViewById(R.id.btGoRegist);

        btLogIn.setOnClickListener(this);
        btSignup.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btLogin:
                id = et_ID.getText().toString();
                pw = et_PW.getText().toString();
                CheckLoginThread thread = new CheckLoginThread();
                thread.start();
                break;
            case R.id.btGoRegist:
                break;
        }

    }

    public String getNickName(String data){

        String nick = "";

        try {

            JSONArray ja = new JSONArray(data);
            if(ja.length()>0) {
                for(int i=0; i<ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    if(order.getString("idMEMB").equals(id) && order.getString("pwMEMB").equals(pw)) {
                        nick = order.getString("nickMEMB");
                        break;
                    }
                }
            }

            if(nick.equals("")){
                Toast.makeText(MainActivity.this, "Wrong ID or PassWord",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nick;
    }
    private void goFriendActivity() {
        Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
        intent.putExtra("NICKNAME", nickName);
        intent.putExtra("ID", id);
        startActivity(intent);
        finish();
    }

    class SendMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case LOGIN_SUCCESS :
                    nickName = getNickName(msg.obj.toString());
                    if(!nickName.equals(""))
                        goFriendActivity();

                    break;
                case LOGIN_FAIL :
                    Toast.makeText(MainActivity.this, "Wrong ID or PassWord",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    class CheckLoginThread extends Thread {
        @Override
        public void run() {
            super.run();

            StringBuilder   html = new StringBuilder();
            String          data = "";

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(
                        SERVERDNS + "/chatting/allMemberSelect.php?"+"id="+id +"&pass="+pw).openConnection();
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
                    data = html.toString();

                    Message msg = handler.obtainMessage();

                    if(data.length() > 0) {
                        msg.what = LOGIN_SUCCESS;
                        msg.obj = data;


                    } else {
                        msg.what = LOGIN_FAIL;
                    }
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
