package com.chatapp.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    String currentName;
    Uri currentProfilePicUri, newProfilePicUri;
    ImageView getnewuserimageinimageview;
    EditText getnewusername;
    Button updateprofilebutton;
    ProgressBar progressbarofupdateprofile;
    CardView getnewuserimage;
    private static int PICK_IMAGE = 123;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getnewusername = findViewById(R.id.getnewusername);
        getnewuserimageinimageview = findViewById(R.id.getnewuserimageinimageview);
        updateprofilebutton = findViewById(R.id.updateprofilebutton);
        progressbarofupdateprofile = findViewById(R.id.progressbarofupdateprofile);
        getnewuserimage = findViewById(R.id.getnewuserimage);

        currentName = getIntent().getStringExtra("nameCurrent");
        currentProfilePicUri = getIntent().getParcelableExtra("uriCurrent");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        getnewusername.setText(currentName);
        Picasso.get().load(currentProfilePicUri).into(getnewuserimageinimageview);
        newProfilePicUri = currentProfilePicUri;

        getnewuserimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        updateprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((currentName.equals(getnewusername.getText().toString()) && currentProfilePicUri.equals(newProfilePicUri))) {

                    Toast.makeText(getApplicationContext(), "No changed", Toast.LENGTH_SHORT).show();
                } else if ((!currentName.equals(getnewusername.getText().toString()) && currentProfilePicUri.equals(newProfilePicUri))) {

                    progressbarofupdateprofile.setVisibility(View.VISIBLE);
                    updateprofilebutton.setEnabled(false);
                    hideKeyboard(UpdateProfile.this);
                    updateNameToFirestore("no image");
                } else if ((currentName.equals(getnewusername.getText().toString()) && !currentProfilePicUri.equals(newProfilePicUri))) {

                    progressbarofupdateprofile.setVisibility(View.VISIBLE);
                    updateprofilebutton.setEnabled(false);
                    hideKeyboard(UpdateProfile.this);
                    updateImageOrBoth(false);
                } else if ((!currentName.equals(getnewusername.getText().toString()) && !currentProfilePicUri.equals(newProfilePicUri))) {

                    progressbarofupdateprofile.setVisibility(View.VISIBLE);
                    updateprofilebutton.setEnabled(false);
                    hideKeyboard(UpdateProfile.this);
                    updateImageOrBoth(true);
                }

            }
        });


    }

    private void updateImageOrBoth(boolean both) {

        StorageReference storageReference = firebaseStorage.getReference().child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //Image compresesion
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newProfilePicUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        currentProfilePicUri = newProfilePicUri;
                        if(both==true) {
                            updateNameToFirestore(uri.toString());
                        }else{
                            Toast.makeText(getApplicationContext(), "User Profile pic updated Successful", Toast.LENGTH_SHORT).show();

                            progressbarofupdateprofile.setVisibility(View.INVISIBLE);
                            updateprofilebutton.setEnabled(true);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressbarofupdateprofile.setVisibility(View.INVISIBLE);
                        updateprofilebutton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "URI get Failed", Toast.LENGTH_SHORT).show();
                    }
                });

               // Toast.makeText(getApplicationContext(), "Image is uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressbarofupdateprofile.setVisibility(View.INVISIBLE);
                updateprofilebutton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Image Not UpLoaded", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateNameToFirestore(String imageUrl) {

        if (getnewusername.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name Can't Be Blank", Toast.LENGTH_SHORT).show();

        } else {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            Map<String, Object> note = new HashMap<>();
            note.put("name", getnewusername.getText().toString());
            if(!imageUrl.equals("no image")) {
                note.put("image", imageUrl);
            }

            documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    updateDataToRealTimeDatabase();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Failed To Update Name", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void updateDataToRealTimeDatabase() {

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()); // pass any key
        UserProfile userProfile = new UserProfile(getnewusername.getText().toString(), firebaseAuth.getUid());
        databaseReference.setValue(userProfile);

        Toast.makeText(getApplicationContext(), "User Profile updated Successful", Toast.LENGTH_SHORT).show();

        currentName = getnewusername.getText().toString();
        progressbarofupdateprofile.setVisibility(View.INVISIBLE);
        updateprofilebutton.setEnabled(true);
        finish();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) // results of user selection image
        {
            newProfilePicUri = data.getData();
            getnewuserimageinimageview.setImageURI(newProfilePicUri);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}