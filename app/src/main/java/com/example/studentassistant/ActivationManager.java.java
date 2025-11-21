package com.sudanese.studentassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Base64;
import java.security.MessageDigest;
import java.util.*;

public class ActivationManager {
    private static final String PREF_NAME = "ActivationPrefs";
    private static final String KEY_IS_ACTIVATED = "is_activated";
    private static final String KEY_ACTIVATION_CODE = "activation_code";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_ADMIN_PHONE = "admin_phone";
    private static final String KEY_ACTIVATION_DATE = "activation_date";
    private static final String KEY_APP_VERSION = "app_version";
    
    private SharedPreferences preferences;
    private Context context;
    private String secretKey = "SudaneseStudentApp2024";
    
    public ActivationManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        if (preferences.getBoolean(KEY_FIRST_RUN, true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_FIRST_RUN, false);
            editor.putString(KEY_ADMIN_PHONE, "249123456789");
            editor.putString(KEY_APP_VERSION, getAppVersion());
            editor.apply();
        }
    }
    
    public String getDeviceId() {
        String savedDeviceId = preferences.getString(KEY_DEVICE_ID, null);
        if (savedDeviceId != null) {
            return savedDeviceId;
        }
        
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceInfo = android.os.Build.MANUFACTURER + android.os.Build.MODEL + android.os.Build.SERIAL;
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((androidId + deviceInfo).getBytes());
            String deviceId = Base64.encodeToString(hash, Base64.NO_WRAP).substring(0, 16).toUpperCase();
            
            preferences.edit().putString(KEY_DEVICE_ID, deviceId).apply();
            return deviceId;
        } catch (Exception e) {
            return "DEV" + System.currentTimeMillis();
        }
    }
    
    public String generateActivationCode(String deviceId) {
        try {
            String rawCode = deviceId + secretKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawCode.getBytes());
            String code = Base64.encodeToString(hash, Base64.NO_WRAP).substring(0, 8).toUpperCase();
            return formatActivationCode(code);
        } catch (Exception e) {
            return "ACT" + System.currentTimeMillis();
        }
    }
    
    private String formatActivationCode(String code) {
        if (code.length() >= 8) {
            return code.substring(0, 4) + "-" + code.substring(4, 8);
        }
        return code;
    }
    
    public boolean activateApp(String inputCode) {
        try {
            String cleanCode = inputCode.replace("-", "").toUpperCase();
            String deviceId = getDeviceId();
            String expectedCode = generateActivationCode(deviceId).replace("-", "").toUpperCase();
            
            if (cleanCode.equals(expectedCode)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_IS_ACTIVATED, true);
                editor.putString(KEY_ACTIVATION_CODE, cleanCode);
                editor.putLong(KEY_ACTIVATION_DATE, System.currentTimeMillis());
                editor.apply();
                return true;
            }
            
            if (isPreGeneratedCode(cleanCode)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_IS_ACTIVATED, true);
                editor.putString(KEY_ACTIVATION_CODE, cleanCode);
                editor.putLong(KEY_ACTIVATION_DATE, System.currentTimeMillis());
                editor.apply();
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean isPreGeneratedCode(String code) {
        Set<String> validCodes = preferences.getStringSet("pre_generated_codes", new HashSet<>());
        return validCodes.contains(code);
    }
    
    public void addPreGeneratedCode(String code) {
        Set<String> validCodes = preferences.getStringSet("pre_generated_codes", new HashSet<>());
        validCodes.add(code.replace("-", "").toUpperCase());
        preferences.edit().putStringSet("pre_generated_codes", validCodes).apply();
    }
    
    public List<String> generateActivationCodes(int count) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String fakeDeviceId = "DEV" + System.currentTimeMillis() + i;
            String code = generateActivationCode(fakeDeviceId);
            codes.add(code);
            addPreGeneratedCode(code);
        }
        return codes;
    }
    
    public boolean isAppActivated() {
        return preferences.getBoolean(KEY_IS_ACTIVATED, false);
    }
    
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    
    public String getAdminPhoneNumber() {
        return preferences.getString(KEY_ADMIN_PHONE, "249123456789");
    }
    
    public void setAdminPhoneNumber(String phoneNumber) {
        preferences.edit().putString(KEY_ADMIN_PHONE, phoneNumber).apply();
    }
    
    public String getAppVersion() {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }
    
    public Map<String, String> getActivationInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("device_id", getDeviceId());
        info.put("is_activated", String.valueOf(isAppActivated()));
        info.put("activation_date", String.valueOf(preferences.getLong(KEY_ACTIVATION_DATE, 0)));
        info.put("app_version", getAppVersion());
        return info;
    }
    
    public boolean hasPendingActivation() {
        return false;
    }
    
    public String getPendingActivationCode() {
        return "";
    }
}