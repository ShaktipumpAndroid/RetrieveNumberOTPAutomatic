package com.shaktipumplimted.smsreader;

public interface OTPReceiveListener {
    void onOTPReceived(String otp);

    void onOTPTimeOut();
}
