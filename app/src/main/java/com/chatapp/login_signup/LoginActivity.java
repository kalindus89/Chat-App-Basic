package com.chatapp.login_signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chatapp.ChatActivity;
import com.chatapp.R;
import com.chatapp.SplashActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText getphonenumber;
    Button sendotpbutton;
    CountryCodePicker countrycodepicker;
    String countryCode, phoneNumber;
    ProgressBar progressbarofmain;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        countrycodepicker = findViewById(R.id.countrycodepicker);
        getphonenumber = findViewById(R.id.getphonenumber);
        sendotpbutton = findViewById(R.id.sendotpbutton);
        progressbarofmain = findViewById(R.id.progressbarofmain);

        firebaseAuth = FirebaseAuth.getInstance();

        countryCode = countrycodepicker.getSelectedCountryCodeWithPlus();

        countrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = countrycodepicker.getSelectedCountryCodeWithPlus();
            }
        });

        sendotpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = getphonenumber.getText().toString().trim();

                if (number.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your number", Toast.LENGTH_SHORT).show();
                } else if (number.length() < 9) {
                    Toast.makeText(getApplicationContext(), "Please enter correct number", Toast.LENGTH_SHORT).show();
                }else{
                    progressbarofmain.setVisibility(View.VISIBLE);
                    sendotpbutton.setEnabled(false);

                    phoneNumber=countryCode+number;// +,country code and number enter

                    PhoneAuthOptions options= PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // how to automatically fetch code here budihail
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(code, forceResendingToken);

                Toast.makeText(getApplicationContext(), "OTP is sent", Toast.LENGTH_SHORT).show();
                progressbarofmain.setVisibility(View.INVISIBLE);
                sendotpbutton.setEnabled(true);
                codeSent=code;

                Intent intent = new Intent(LoginActivity.this, OtpAuthentication.class);
                intent.putExtra("otp",codeSent);
                startActivity(intent);
                finish();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
       // FirebaseUser asd=FirebaseAuth.getInstance().getCurrentUser()
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }
}