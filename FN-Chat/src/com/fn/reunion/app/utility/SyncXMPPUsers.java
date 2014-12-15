package com.fn.reunion.app.utility;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.fn.reunion.app.model.Contact;
import com.fn.reunion.app.ui.pages.BuddiesListPage;
import com.fn.reunion.app.xmpp.NotificationService;
import com.fn.reunion.app.xmpp.XmppManager;

public class SyncXMPPUsers {

	private List<Contact> contacts;
	private List<Contact> registeredContacts;
	private Handler handler;
	private XmppManager manager;
	private String tag = SyncXMPPUsers.class.getSimpleName();
	private static SyncXMPPUsers instance = null;
	private static Context context;

	private SyncXMPPUsers() {
		handler = new Handler(context.getMainLooper());
		contacts = ContactsManager.getAllContacts(context, false);
		manager = NotificationService .getInstance().getXmppManager();
		registeredContacts = new ArrayList<Contact>();
		notifyRosterUI();
	}
	
	public static SyncXMPPUsers initiate(Context mContext){
		
		if (instance == null) {
			 context = mContext;
			instance = new SyncXMPPUsers();
		}
		return instance;
	}

	public void notifyRosterUI() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//1 -> do loop on contacts list
				//2 -> for each user do 
				//3 -> check if user exists depends on phone number
				//4 -> exists ? then save to RosterList
				
				if (!manager.getConnection().isConnected()) {
					Log.e(tag, "XMPP disconected!");
					return;
				}
				
				for (Contact user : contacts) {
					//Log.i(tag, user.toString());
					try {
						if (! manager.checkIfUserExists(user.getPhoneNumber()) ){
							//Log.i(tag, phoneNumber+" not exists");
						}else{
							registeredContacts.add(user);
							//Log.i(tag, phoneNumber+" exists");
						}
					 }catch (XMPPException e) {
						e.printStackTrace();
					}
				}
				// notify RosterListView
				for (Contact user : registeredContacts){
					manager.addFriend(user.getPhoneNumber()+"@"+manager.getConnection().getHost(),
							user.getName());
					Log.i(tag,"Roster: "+ user.toString());
				}
				BuddiesListPage.updateRoster(registeredContacts);
				
			}//end run()
		});
	}

	private void runOnUiThread(Runnable r) {
		handler.post(r);
	}
}
