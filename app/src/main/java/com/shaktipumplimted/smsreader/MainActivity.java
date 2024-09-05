package com.shaktipumplimted.smsreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESOLVE_HINT = 100;
    private String TAG = "SMS_Retriever";
    AppCompatActivity activity;
    TextView textView,mobileNumber;
    Button sendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.tv_otp);
        mobileNumber = findViewById(R.id.mobileNumber);
        sendOtp = findViewById(R.id.sendOtp);
        requestHintForGoogleAccount();

        mobileNumber.setText(getnum());

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSMSListener();
                 sendOtpMethod();
            }
        });
    }

/*
    private void requestHintForMobileNumber() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
*/



    private void requestHintForGoogleAccount() {


        CredentialsClient mCredentialsClient = Credentials.getClient(this);

        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)// Request only Google accounts
                .build();

        mCredentialsClient.request(mCredentialRequest).addOnCompleteListener(
                new OnCompleteListener<CredentialRequestResponse>() {
                    @Override
                    public void onComplete(Task<CredentialRequestResponse> task) {
                        if (task.isSuccessful()) {
                            // Credential retrieved successfully
                            CredentialRequestResponse credentialRequestResponse = task.getResult();
                            String email = credentialRequestResponse.getCredential().getId();
                            Log.e("email===>",email);
                            // Use the email here
                        } else {
                            // Handle the exception (e.g., user denied the request)
                            Exception e = task.getException();
                            if (e instanceof ResolvableApiException) {
                                // The user must take some action before the request can be fulfilled
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, RESOLVE_HINT);
                                } catch (IntentSender.SendIntentException sie) {
                                    sie.printStackTrace();
                                }
                            } else {
                                // No credentials saved or another error
                            }
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                // The user has selected an account, handle it here
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String email = credential.getId(); // This is the selected email

                Log.e("email====>",email);
                // Use the email here
            } else {
                // The user canceled the dialog
            }
        }
    }

    private void sendOtpMethod() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://control.yourbulksms.com/api/sendhttp.php?authkey=393770756d707334373701&mobiles="+mobileNumber.getText().toString().trim()+"&message=Enter The Following OTP To Verify Your Account 12345 "+Utility.getHashKey(getApplicationContext())+" SHAKTI&sender=SHAKTl&route=2&unicode=0&country=91&DLT_TE_ID=1707161675029844457",

                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                Log.e("responeotp==>", res.toString());
                if (res.toString() != null && !res.toString().isEmpty()) {

                    VerificationCodeModel verificationCodeModel = new Gson().fromJson(res.toString(), VerificationCodeModel.class);
                    if (verificationCodeModel.getStatus().equals("Success")) {
                        Toast.makeText(MainActivity.this, "OTP sent Successfully", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", String.valueOf(error));
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);

    }


    private void startSMSListener() {
        MySMSBroadcastReceiver smsReceiver = new MySMSBroadcastReceiver();
        smsReceiver.setOTPListener(new OTPReceiveListener() {
            @Override
            public void onOTPReceived(String otp) {
                textView.setText(otp);
            }

            @Override
            public void onOTPTimeOut() {
                textView.setText("onOTPTimeOut");
            }
        });

        SmsRetrieverClient client = SmsRetriever.getClient(this);

        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // API successfully started
                //SMSBroadcastReceiver started listenting for sms
                Log.d(TAG, "API successfully started");
                mobileNumber.setText(getnum());
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Fail to start API
                e.printStackTrace();
                mobileNumber.setText("Mobile Number Not Retrived");
            }
        });


    }


    private String getnum() {
        String phoneNumber1 = "";
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            phoneNumber1 = subscriptionManager.getPhoneNumber(SubscriptionManager.getDefaultSubscriptionId());
        }

        return phoneNumber1;

    }


}