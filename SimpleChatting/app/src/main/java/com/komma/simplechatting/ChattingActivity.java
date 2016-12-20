package com.komma.simplechatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChattingActivity extends AppCompatActivity {

    String myId = null;
    String friendId = null;

    ScrollView  scrollView_chat;
    Button      btSend;
    TextView    tvMainText;
    TextView    tvId;
    EditText    etSend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        myId = intent.getStringExtra("ID");
        friendId = intent.getStringExtra("FRIENDID");

        scrollView_chat = (ScrollView) findViewById(R.id.ScrollView);
        btSend = (Button) findViewById(R.id.btSend);
        btSend.setText("Send");
        tvMainText = (TextView) findViewById(R.id.maintext);
        etSend = (EditText) findViewById(R.id.txSend);
        tvId = (TextView) findViewById(R.id.txId);
        tvId.setText(myId);

        

        


    }
}
