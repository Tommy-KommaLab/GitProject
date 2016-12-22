package com.komma.simplechatting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String IP = "http://ec2-52-78-81-140.ap-northeast-2.compute.amazonaws.com";
    private final static int    PORT = 10001;

    ChattingActivity thisActivity;
    String myId = null;
    String friendId = null;

    ScrollView  scrollView_chat;
    Button      btSend;
    TextView    tvMainText;
    TextView    tvId;
    EditText    etSend;

    Socket      socket;
    InputStream in;
    OutputStream out;

    String  recive = "";

    private Handler handler = new Handler();

    boolean flagMyText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        myId = intent.getStringExtra("ID");
        friendId = intent.getStringExtra("FRIENDID");

        thisActivity = this;

        scrollView_chat = (ScrollView) findViewById(R.id.ScrollView);
        btSend = (Button) findViewById(R.id.btSend);
        btSend.setText("Send");
        tvMainText = (TextView) findViewById(R.id.maintext);
        etSend = (EditText) findViewById(R.id.txSend);
        tvId = (TextView) findViewById(R.id.txId);
        tvId.setText(myId);

        btSend.setOnClickListener(this);

        Thread clientThread = new Thread() {
            public void run() {
                connect(IP, PORT);
            }
        };

        clientThread.start();


    }

    @Override
    public void onClick(View view) {

        if(view == btSend) {
            if(socket != null && socket.isConnected()) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String stTime = sdf.format(date);

                    out.println(myId + " " + etSend.getText().toString() + " " + stTime);
                    out.flush();
                    flagMyText = true;
                    etSend.setText("", TextView.BufferType.NORMAL);


                } catch (IOException e) {
                    //e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChattingActivity.showDialog(thisActivity, "오류", "통신이 원할하지 않습니다.");
                        }
                    });

                }
            } else {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ChattingActivity.showDialog(thisActivity, "오류", "통신이 원할하지 않습니다.");
                    }
                });
            }
        }
    }

    public void connect(String ip, int port) {

        int     size;
        byte[]  buffer = new byte[10240];



        try {
            socket = new Socket(ip, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            while (socket != null && socket.isConnected()) {
                size = in.read(buffer);
                if(size <= 0)
                    continue;

                recive = new String(buffer, 0, size, "UTF-8");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       String  chatTxt = tvMainText.getText().toString();
                        tvMainText.setText(chatTxt);

                        if(flagMyText) {
                            tvMainText.setGravity(Gravity.RIGHT);
                            flagMyText = false;
                            tvMainText.setText(chatTxt + recive + "\n");
                        }

                        tvMainText.setGravity(Gravity.LEFT);
                        tvMainText.setText(chatTxt + recive + "\n");
                        scrollView_chat.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        } catch (IOException e) {
            //e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ChattingActivity.showDialog(thisActivity, "오류", "통신이 원할하지 않습니다.");
                }
            });
        }


    }

    public static void showDialog(final Activity activity, String title, String text) {

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(text);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.setResult(Activity.RESULT_OK);
            }
        });

        ad.create();
        ad.show();
    }
}
