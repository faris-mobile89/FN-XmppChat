package com.fn.reunion.app.xmpp;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.fn.reunion.app.model.FriendsRoster;
import com.fn.reunion.app.ui.pages.BuddiesListPage;

/**
 * Changes to the roster, that is, changes to friends' statuses or
 * availability, are handled by this class.
 */

public class BuddyListener implements RosterListener {
	
	private String tag = "BuddyListener";
	private Context context;
	
	public BuddyListener(Context mContext){
		this.context = mContext;
	}
	
    /**
     * Displays which user is added to the entry.
     *
     * @param addresses
     */
	
    public void entriesAdded(Collection<String> addresses) {
        Log.i(tag, "entriesAdded");
        return;
    }

    /**
     * Displays which user is updated in the entry.
     *
     * @param addresses
     */
    public void entriesUpdated(Collection<String> addresses) {
    	Log.i(tag, "entriesUpdated");
        return;
    }

    /**
     * Displays which user is deleted in the entry.
     *
     * @param addresses
     */
    public void entriesDeleted(Collection<String> addresses) {
        // Fix me!
        return;
    }

    /**
     * Updates user's changed presence
     *
     * @param presence
     */
    public void presenceChanged(Presence presence) {
    	
    	   String from  = presence.getFrom();
    	   //Log.i(tag, presence.getMode().toString());
    	   new BuddiesController(context).notifyPresenceChanged(from ,presence.getMode());
        return;
    }
}