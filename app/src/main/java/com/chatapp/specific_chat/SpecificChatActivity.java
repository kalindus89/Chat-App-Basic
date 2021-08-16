package com.chatapp.specific_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SpecificChatActivity extends AppCompatActivity {

    EditText getmessage;
    ImageButton sendmessage;
    CardView sendmessagecardview;
    TextView nameofspecificuser;
    ImageView specificuserimageinimageview;
    ImageButton backbuttonofspecificchat;
    RecyclerView recyclerviewofspecific;

    private String enteredmessage;
    Intent intent;

    String mrecievername, sendername, mrecieveruid, msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom, recieverroom;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getmessage = findViewById(R.id.getmessage);
        sendmessagecardview = findViewById(R.id.carviewofsendmessage);
        sendmessage = findViewById(R.id.imageviewsendmessage);
        nameofspecificuser = findViewById(R.id.nameofspecificuser);
        specificuserimageinimageview = findViewById(R.id.specificuserimageinimageview);
        backbuttonofspecificchat = findViewById(R.id.backbuttonofspecificchat);
        recyclerviewofspecific = findViewById(R.id.recyclerviewofspecific);

        intent = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mrecieveruid = intent.getStringExtra("receiveruid");
        mrecievername = intent.getStringExtra("name");

        //to identify who send and received the chat
        senderroom = msenderuid + mrecieveruid;
        recieverroom = mrecieveruid + msenderuid;

        backbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        nameofspecificuser.setText(mrecievername);
        String uri = intent.getStringExtra("imageuri");
        if (uri.isEmpty()) {
            Toast.makeText(getApplicationContext(), "null is recieved", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(uri).into(specificuserimageinimageview);
        }


        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage = getmessage.getText().toString();
                if (enteredmessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter message first", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());

                    SpecificChatModel messages = new SpecificChatModel(enteredmessage, firebaseAuth.getUid(), date.getTime(), currenttime);

                    firebaseDatabase = FirebaseDatabase.getInstance();

                    firebaseDatabase.getReference().child("chats").child(senderroom).child("messages").push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference().child("chats").child(recieverroom).child("messages").push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });

                    getmessage.setText(null);
                }

            }
        });

    }


}