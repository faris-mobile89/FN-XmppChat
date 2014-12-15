package com.fn.reunion.app.ui.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fn.reunion.app.R;
import com.fn.reunion.app.ui.base.AppBaseActivity;
import com.fn.reunion.app.ui.registeration.RegisterationActivity;
import com.fn.reunion.app.utility.SessionManager;
import com.fn.reunion.app.utility.XmppStatus;
import com.fn.reunion.app.xmpp.ServiceManager;

public class SplashScreenActivity extends Activity {
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SessionManager session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		 session = new SessionManager(getBaseContext());
		 
		 if (session.isLoggedIn()) {
			  ServiceManager serviceManager = new ServiceManager(SplashScreenActivity.this);
		      serviceManager.setNotificationIcon(R.drawable.app_launcher);
		      serviceManager.startService();
		      
		      /**
		       *  Save ServiceManager state
		       */
		      XmppStatus.saveXmppStatus(getBaseContext());
		}
		 
		 new Handler().postDelayed(new Runnable() {
			 
            @Override
            public void run() {
            	Intent i =null;
            	if(session.isLoggedIn()){
            		i = new Intent(SplashScreenActivity.this, AppBaseActivity.class);
            	}else {
                   i = new Intent(SplashScreenActivity.this, RegisterationActivity.class);
            	}
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
