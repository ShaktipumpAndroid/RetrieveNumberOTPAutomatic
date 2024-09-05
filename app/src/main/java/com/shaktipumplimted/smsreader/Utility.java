package com.shaktipumplimted.smsreader;

import android.content.Context;

public class Utility {

    public static String getHashKey(Context context) {
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(context);

        // This code requires one time to get Hash keys do comment and share key
        //    Log.d(TAG, "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));
        return appSignatureHashHelper.getAppSignatures().get(0);
    }
}
