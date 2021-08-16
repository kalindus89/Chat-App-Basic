package com.chatapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageView viewuserimageinimageview;
    EditText viewusername;
    TextView movetoupdateprofile;
    Toolbar toolbarofviewprofile;
    ImageButton backbuttonofviewprofile;

    Uri uriTemp;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage; // save images
    FirebaseDatabase firebaseDatabase; // using to save user personal information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewuserimageinimageview = findViewById(R.id.viewuserimageinimageview);
        viewusername = findViewById(R.id.viewusername);
        movetoupdateprofile = findViewById(R.id.movetoupdateprofile);
        backbuttonofviewprofile = findViewById(R.id.backbuttonofviewprofile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        toolbarofviewprofile = findViewById(R.id.toolbarofviewprofile);
        setSupportActionBar(toolbarofviewprofile);
        backbuttonofviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile muserprofile = snapshot.getValue(UserProfile.class);
                viewusername.setText(muserprofile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseStorage.getReference().child("Images").child(firebaseAuth.getUid()).child("Profile Pic")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                uriTemp=uri;
                Picasso.get().load(uriTemp).into(viewuserimageinimageview);

            }
        });

        movetoupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uriTemp!=null && !viewusername.getText().toString().isEmpty()) {
                    Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                    intent.putExtra("nameCurrent", viewusername.getText().toString());
                    intent.putExtra("uriCurrent", uriTemp);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please wait until data sync", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}