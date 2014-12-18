package com.fn.reunion.app.utility;

import android.content.Context;


/**
 * Created by Faris on 12/18/14.
 *
 * This class for save appData and Configs into shared preferences
 *
 */

public class SharedPreferences {

    /**
     *  Save custom string to shared preferences
     * @param context
     * @param sharedKey
     * @param value
     */

    private static final String SHARED_NAME ="AppConfig";

    public static void putStringToSharedPref(Context context , String sharedKey , String value){

        android.content.SharedPreferences sharedPref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(sharedKey,value);
        editor.commit();

    }

    /**
     *
     * @param context
     * @param sharedKey
     * @return  String for give sharedKey
     */

    public static String getStringFormSharedPref(Context context , String sharedKey ){
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(sharedKey,"");
    }

    /**
     * Save custom boolean value to shared preferences
     * @param context
     * @param sharedKey
     * @param value
     */

    public static void putBooleanToSharedPref(Context context , String sharedKey , boolean value){

        android.content.SharedPreferences sharedPref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(sharedKey,value);
        editor.commit();

    }

    /**
     *
     * @param context
     * @param sharedKey
     * @return  boolean for give sharedKey
     */

    public static boolean getBooleanFormSharedPref(Context context , String sharedKey ){
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(sharedKey,false);
    }

}
