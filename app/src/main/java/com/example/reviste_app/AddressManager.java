package com.example.reviste_app;

import android.content.Context;
import android.content.SharedPreferences;

public class AddressManager {
    private static final String SHARED_PREFS_NAME = "AddressPrefs";
    private static final String ADDRESS_NAME_KEY = "addressName";
    private static final String ADDRESS_DEPT_KEY = "addressDept";
    private SharedPreferences prefs;

    public AddressManager(Context context) {
        prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAddress(String name, String department) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ADDRESS_NAME_KEY, name);
        editor.putString(ADDRESS_DEPT_KEY, department);
        editor.apply();
    }

    public String getAddressName() {
        return prefs.getString(ADDRESS_NAME_KEY, "");
    }

    public String getAddressDepartment() {
        return prefs.getString(ADDRESS_DEPT_KEY, "");
    }
}