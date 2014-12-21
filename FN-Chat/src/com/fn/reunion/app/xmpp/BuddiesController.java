package com.fn.reunion.app.xmpp;

import org.jivesoftware.smack.packet.Presence.Mode;

import android.content.Context;
import android.os.Handler;

import com.fn.reunion.app.ui.pages.BuddiesListPage;

public class BuddiesController {

	
    private Handler handler;
    private Context context;
    
    public BuddiesController(Context mContext){
		this.context = mContext;
		initHandler();
	}
    
    private void initHandler(){
    	 handler = new Handler(context.getMainLooper());
    }

    public void notifyIncomingMessage(final String fromUserID, final String toUserID, final String message) {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   BuddiesListPage.incomingMessage(fromUserID,toUserID,message);
           }
       });
    }
    
    public void notifyPresenceChanged(final String from , final Mode mode , final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
         	   if (mode != null) {
         		   BuddiesListPage.presenceChanged(from,mode,status);
				   }
            }
        });
     }

    private void runOnUiThread(Runnable r) {
       handler.post(r);
    } 
}
