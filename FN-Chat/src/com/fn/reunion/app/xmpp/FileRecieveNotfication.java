package com.fn.reunion.app.xmpp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.fn.reunion.app.ui.privateChat.MessageActivity;

public class FileRecieveNotfication{
    private Handler handler;

    public void checkdata(Context context){
       handler = new Handler(context.getMainLooper());
    } 

    public void notifyMessageUI(final String userID , final String FilePath) {
       // Do work
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   Log.d("FileRecieveNotfication", "show file on UI");
               // Code to run on UI thread
				MessageActivity.isComplete = true;
				MessageActivity.notifyFileRecieved(userID,FilePath);
           }
       });
    }

    private void runOnUiThread(Runnable r) {
       handler.post(r);
    }  
}