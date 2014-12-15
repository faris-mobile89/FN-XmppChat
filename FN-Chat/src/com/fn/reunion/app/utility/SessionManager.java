package com.fn.reunion.app.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fn.reunion.app.model.UserInfo;

@SuppressLint("CommitPrefEdits")
public class SessionManager {

	SharedPreferences pref;
	// Editor for Shared preferences
	Editor editor;
	// Context
	Context _context;
	// Shared pref mode
	int PRIVATE_MODE = 0;
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences("USER", PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create User session
	 * */
	public void createLoginSession(UserInfo user) {
		editor.putBoolean(IS_LOGIN, true);
		editor.putString("fname", user.getFname());
		editor.putString("lname", user.getLname());
		editor.putString("fname", user.getFname());
		editor.putString("phone", user.getPhone());
		editor.putString("email", user.getEmail());

		editor.commit();
	}

	/**
	 * Get stored session data
	 * */
	public UserInfo getUserDetails() {
		UserInfo user = new UserInfo();
		user.setPhone(pref.getString("phone", null));
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
