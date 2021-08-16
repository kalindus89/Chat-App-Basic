package com.chatapp.specific_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    SpecificMessageAdapter messagesAdapter;
    ArrayList<SpecificChatModel> messagesArrayList;

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


        List<String> commonChatRoomId = new ArrayList();
        commonChatRoomId.add(msenderuid.toLowerCase(Locale.ROOT));
        commonChatRoomId.add(mrecieveruid.toLowerCase(Locale.ROOT));
        Collections.sort(commonChatRoomId);

        senderroom=commonChatRoomId.get(0)+commonChatRoomId.get(1);

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

                            // Call smooth scroll to bottom
                            recyclerviewofspecific.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerviewofspecific.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
                                }
                            });

                        }
                    });

                    getmessage.setText(null);
                }

            }
        });

        messagesArrayList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // start list from end
        recyclerviewofspecific.setLayoutManager(linearLayoutManager);
        messagesAdapter=new SpecificMessageAdapter(SpecificChatActivity.this,messagesArrayList);
        recyclerviewofspecific.setAdapter(messagesAdapter);


        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    SpecificChatModel messages=snapshot1.getValue(SpecificChatModel.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
        recyclerviewofspecific.setAdapter(messagesAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }

}