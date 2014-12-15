package com.fn.reunion.app.utility;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.fn.reunion.app.model.Contact;;

public class ContactsManager {
 
	private static String tag = ContactsManager.class.getSimpleName();
	
	public Account[] getAccounts(Context context){
		
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccounts();
		for (Account ac : accounts) {
		    String acname = ac.name;
		    String actype = ac.type;
		    // Take your time to look at all available accounts
		    Log.i(tag,"Accounts : " + acname + ", " + actype);
		}
		return accounts;
	}

	public static String getMyPhoneNumber(Context context){
		TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		return mPhoneNumber;
	}
	
	public static List<Contact> getAllContacts(Context context ,boolean isLogContacts){
		
		List<Contact> contactsList = new ArrayList<Contact>();
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
		  String name= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		  phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
		  contactsList.add(new Contact(name, phoneNumber));
		  
		  if (isLogContacts) {
    		  Log.i(tag , "name :"+name +" phone: "+phoneNumber);
		   } 
		}
		phones.close();
		
		return contactsList;
	}
}
