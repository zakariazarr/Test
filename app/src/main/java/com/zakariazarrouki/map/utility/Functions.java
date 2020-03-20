package com.zakariazarrouki.map.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class Functions {

    private static String USER_TAG = "user_session";
    private static String CONFIG_REF = "config_ref";

    public static void showInfoToast(Context context, String msg){
        Toasty.info(context,msg, Toast.LENGTH_LONG).show();
    }

    public static boolean saveUserObject(Context context, String obj){
        SharedPreferences pref = context.getSharedPreferences(CONFIG_REF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_TAG,obj);
        return editor.commit();
    }

    public static String getUserObject(Context context){
        SharedPreferences pref = context.getSharedPreferences(CONFIG_REF, MODE_PRIVATE);
        return pref.getString(USER_TAG, "");
    }
}
