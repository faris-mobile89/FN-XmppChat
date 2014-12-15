package com.fn.reunion.app.chat.content.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class ContactsSyncAdapter extends AbstractThreadedSyncAdapter {
	
	private final AccountManager mAccountManager;
	
	public ContactsSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	/**
	 * called by the sync manager when it’s sync time
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
	}

}
