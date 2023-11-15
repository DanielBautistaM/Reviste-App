package com.example.reviste_app;

import android.content.Context;
import android.content.SharedPreferences;

public class PaymentInfoManager {
    private static final String SHARED_PREFS_NAME = "PaymentPrefs";
    private static final String PAYMENT_DOC_ID_KEY = "paymentDocId";
    private SharedPreferences prefs;

    public PaymentInfoManager(Context context) {
        prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void savePaymentDocumentId(String documentId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PAYMENT_DOC_ID_KEY, documentId);
        editor.apply();
    }

    public String getPaymentDocumentId() {
        return prefs.getString(PAYMENT_DOC_ID_KEY, "");
    }
}