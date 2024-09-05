package com.shaktipumplimted.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadcastReceiver extends BroadcastReceiver {
    private static OTPReceiveListener otpListener;

    public static void setOTPListener(OTPReceiveListener otpReceiveListener) {
        otpListener = otpReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();

            assert extras != null;
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
           Log.e("status=====>", String.valueOf(status));
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String full_message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    /*
                    <#> 123456 is your secret one time password (OTP)
                     to login to your Account. 7bhnjAR5gBE
                     */
                    Log.d("Sms_receiver", "message " + full_message);
                    //Extract the OTP code and send to the listener
                    full_message = full_message.replace(Utility.getHashKey(context),"");
                    String otp = extractNumberFromAnyAlphaNumeric(full_message);

                    Log.d("Sms_receiver", "otp " + otp);

                    if (otpListener != null) {
                        otpListener.onOTPReceived(otp);
                    } else {
                        Log.d("Sms_receiver", "otpListener is null");
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    if (otpListener != null) {
                        otpListener.onOTPTimeOut();
                    }
                    break;
            }
        }
    }
    public static String extractNumberFromAnyAlphaNumeric(String alphaNumeric) {
//        alphaNumeric = alphaNumeric.length() > 0 ? alphaNumeric.replaceAll("\\D+", "") : "";
//        int num = alphaNumeric.length() > 0 ? Integer.parseInt(alphaNumeric) : 0; // or -1
//        return String.valueOf(num);

        Pattern otpPattern = Pattern.compile("\\b\\d{4,6}\\b");
        Matcher matcher = otpPattern.matcher(alphaNumeric);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
