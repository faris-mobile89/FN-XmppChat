package com.fn.reunion.app;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Constants {
	
	public static final String KEY_TAG = "tag";
	public static final String KEY_ID = "id";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_FIRST_NAME = "firstName";
	public static final String KEY_LAST_NAME = "familyName";
	public static final String KEY_FULL_NAME = "fullName";
	public static final String KEY_PHONE = "phoneNumber";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_CONFIRMED = "confirmed";
	public static final String KEY_VCDOE = "vCode";
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_ERROR = "error";
	public static final String KEY_TAG_CONFIRM = "confirmUser";
	public static final String KEY_TAG_SEARCH_USERS = "searchUsers";
	public static final String KEY_TAG_SEARCH_USERS_ADVANCED = "advancedSearchUsers";
	public static final String KEY_TAG_USERS = "users";
	public static final String KEY_JABBER_ID = "jabberID";
	
	public static final String KEY_SUGG_FRIENDS = "suggestedFriend";
	public static final String KEYWORD = "keyword";
	public static final String KEY_GENDER = "gender";
	
	
	public static final String KEY_GET = "GET";
	public static final String KEY_POST = "POST";
	
	public static final int CONFIRMED_FLAG = 1;
    public static final int ERROR_CODE = 1;
	
	public static final String HOST = "http://192.168.11.21";
	public static final String API_URL = "/mena_chat/api.php";


    /*
     * Shared Preferences kes
     */

    public static final String KEY_SHOW_MY_LOCATION = "locationValue";
	
	public static final DisplayImageOptions options =  new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.ic_empty)
	.showImageForEmptyUri(R.drawable.ic_empty)
	.showImageOnFail(R.drawable.ic_error)
	.cacheInMemory(true)
	.cacheOnDisk(true)
	.considerExifParams(true)
	.bitmapConfig(Bitmap.Config.RGB_565)
	.build();
}
