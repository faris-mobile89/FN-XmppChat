/*
 * Copyright (C) 2014 Faris Abu Saleem.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fn.reunion.app.ui.base;

import static com.devspark.appmsg.AppMsg.LENGTH_STICKY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.devspark.appmsg.AppMsg;
import com.fn.reunion.app.R;
import com.fn.reunion.app.Application;
import com.fn.reunion.app.model.FriendsRoster;
import com.fn.reunion.app.model.SuggestedFriend;
import com.fn.reunion.app.ui.searchfriends.NewChatActivity;
import com.fn.reunion.app.utility.XmppStatus;
import com.fn.reunion.app.xmpp.NotificationService;
import com.fn.reunion.app.xmpp.ServiceManager;
import com.fn.reunion.app.xmpp.XMPPLogic;
import com.fn.reunion.app.xmpp.XmppManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.keyboardsurfer.android.widget.crouton.Configuration;

/**
 * This is main view.
 * 
 * @author Faris Abu Saleem (faris.it.cs@gmail.com)
 */

 @SuppressWarnings("deprecation")
 public class AppBaseActivity extends SlidingFragmentActivity implements ActionBar.TabListener{
   
	private XMPPConnection connection;
    //private XmppManager xmppManager;
	Handler mHandler = new Handler();
	public SlidingMenu menu;
	public static AppMsg XMPP_CONNECTION_MESSAGE;
	public ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { " News ", " Chats " };
	protected ListFragment mFrag;
    private static Application application;
	
	private String TAG = AppBaseActivity.class.getSimpleName();
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
      setContentView(R.layout.app_main_activity);
      setBehindContentView(R.layout.menu_frame);

        application = ((Application)getApplicationContext());

	  AppMsg.Style style = new AppMsg.Style(LENGTH_STICKY, R.color.red);
	  XMPP_CONNECTION_MESSAGE = AppMsg.makeText(this, "Error connecting server", style)
	  .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out);
    
      initSlideMenu(savedInstanceState);
      initXMPP();
      initPager();
      
      //Execute at first time when initialize the application.
      Bundle b = getIntent().getExtras();
        if (b != null) {
			try {
				if (b.getBoolean("isAddFriend")) {
			        mHandler.post(new AddFriends(getBaseContext()));
				}
			} catch (Exception e) {
			}
		}
    }

    public void xmppDisconnected(){

        Log.d("tag", "xmppDisconnected by faris");


    }
    private void initXMPP() {
 	      
        //Start the service
    	if (!XmppStatus.isServiceManager(getBaseContext())) {
    		  ServiceManager serviceManager = new ServiceManager(AppBaseActivity.this);
    	      serviceManager.setNotificationIcon(R.drawable.app_launcher);
    	      serviceManager.startService();
		}

        Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE)
                .build();

        try {
        	connection = XMPPLogic.getInstance().getConnection();
            connection.addConnectionListener(new MConnectionListener(getBaseContext()));
            if (connection.isConnected()) {
         	   FriendsRoster.getInstance();
     	   }else{
     		 // XMPP_CONNECTION_MESSAGE.show();
                Crouton.makeText(this, "Not Connected to the server", Style.ALERT).setConfiguration(Configuration.DEFAULT).show();
            }
    	} catch (NullPointerException e) {
    		Log.e(TAG, "initXMPP faild");
            Crouton.makeText(this, "Not Connected to the server", Style.ALERT).setConfiguration(Configuration.DEFAULT).show();
        }catch (Exception e){
          //TODO set error message
        }
	}

	private void initSlideMenu(Bundle savedInstanceState) {
    	 
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new MenuListFragment();
            t.replace(R.id.menu_frame, mFrag);
            t.commit();
        } else {
            mFrag = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }
        
        /*
        SlidingMenu menu;
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setBehindWidth(200);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        */
        SlidingMenu menu = getSlidingMenu();
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffset(300);
        menu.setMode(SlidingMenu.LEFT);
        menu.setFadeDegree(0.35f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidth(25);
        menu.setShadowDrawable(R.drawable.shadow);
	}

	private void initPager(){
    	
		// Initialization
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(true);
		
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		}
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));	
		}
		actionBar.getTabAt(0).setIcon(R.drawable.rss_32);
		actionBar.getTabAt(1).setIcon(R.drawable.chat_32);
       // actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_background_textured_theme_fn_chat));

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    
    	new MenuInflater(this).inflate(R.menu.app_main_menu, menu);
       return (super.onCreateOptionsMenu(menu));
 }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.action_new_chat) {
    	 startActivity(new Intent(this,NewChatActivity.class));
    }
    return (super.onOptionsItemSelected(item));
} 
    
    public ArrayList<HashMap<String, String>> getUserList() throws Exception {

         ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
         if (connection != null) {
             Roster roster = connection.getRoster();
             Collection<RosterEntry> entries = roster.getEntries();
             for (RosterEntry entry : entries) {
                 String avatarPath = "";
                 ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                 VCard vCard = new VCard();
                 try {
                     vCard.load(connection, entry.getUser());
                     vCard.getExtensions();
                     byte[] b = vCard.getAvatar();
                     Bitmap avatar = BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, b.length);
                   //  avatarPath = savaAvatar(avatar, entry.getUser());
                 } catch (Exception e) {
                 }

                 HashMap<String, String> user = new HashMap<String, String>();
                 user.put("name", entry.getName());
                 user.put("userid", entry.getUser());
                 user.put("statusMsg", "" + entry.getStatus());
                 user.put("user_avatar", avatarPath);
                 user.put("status", "" + connection.getRoster().getPresence(entry.getUser()).isAvailable());
                 userList.add(user);
             }
         }
         return userList;
     }
   
    @Override
    protected void onDestroy() {
    	super.onDestroy();
   }

    public class AddFriends implements Runnable
    {
    	
        private Context mContext = null;    
        private Thread addThread = null;
        
        private XmppManager xmppManager;

        public AddFriends(Context context)
        {
             mContext = context;
        }
        
        public void start()
        {
            if(addThread == null) addThread = new Thread(this);
            addThread.start();
        }

        public void run()
        {
        	   try{
        		   xmppManager = NotificationService.getInstance().getXmppManager();
        		   if (xmppManager.isConnected()) {
        		    Bundle bundleObject = getIntent().getExtras();
        		    // Get ArrayList Bundle & building friends list
        		    @SuppressWarnings("unchecked")
        			ArrayList<SuggestedFriend> classObject = (ArrayList<SuggestedFriend>) 
        			bundleObject.getSerializable(com.fn.reunion.app.Constants.KEY_SUGG_FRIENDS);
        		    
        		       for(int index = 0; index < classObject.size(); index++){
        		    	 SuggestedFriend object = classObject.get(index);
        		    	  Log.i("Found", object.toString());
        		    	  Log.d("getFNFN", "addFriend " +object.getJabberId() );
        		  		  xmppManager.addFriend(object.getJabberId()+"@"+xmppManager.getConnection().getHost(),
        		  		   			  object.getName());
        		     }
        		       return;
        		   }
        		} catch(NullPointerException e){

        		}catch (Exception e){

               }
        	   
        	   try{
        			  mHandler.postDelayed(this, 5000);
            	      Log.d("getFNFN", "postDelayed 5 seconds");
        	   }catch(NullPointerException ne){
        		   
        	   }
        }
    }
    
	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction frTransaction) {
	}
	
	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction frTransaction) {
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction frTransaction) {	
	}

    private class MConnectionListener implements ConnectionListener{

        public MConnectionListener(Context mContext){

        }
        @Override
        public void connectionClosed() {
          Log.d(TAG,"connectionClosed");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.d(TAG,"connectionClosedOnError");
        }

        @Override
        public void reconnectingIn(int i) {
            Log.d(TAG,"reconnectingIn");
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d(TAG,"reconnectionSuccessful");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.d(TAG,"reconnectionFailed");

        }
    }
}