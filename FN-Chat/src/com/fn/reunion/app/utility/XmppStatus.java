package com.fn.reunion.app.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.fn.reunion.app.R;

public class XmppStatus {
	
	public static void saveXmppStatus(Context c){
		  Context context = c;
	      SharedPreferences sharedPref = context.getSharedPreferences(
	    		  context.getString(R.string.preference_XMPP_key), Context.MODE_PRIVATE);
	      SharedPreferences.Editor editor = sharedPref.edit();
	      editor.putBoolean(context.getString(R.string.saved_xmpp_service), true);
	      editor.commit();
	}
	
	public static boolean isServiceManager(Context c){
		Context context = c;
		SharedPreferences sharedPref = context.getSharedPreferences(
	    		  context.getString(R.string.preference_XMPP_key), Context.MODE_PRIVATE);
    	String savedKey = context.getResources().getString(R.string.saved_xmpp_service);
		return sharedPref.getBoolean(savedKey, false);
	}

}
