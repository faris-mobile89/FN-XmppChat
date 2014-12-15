package com.fn.reunion.app.ui.pages;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


import com.fn.reunion.app.R;
import com.fn.reunion.app.controller.BadConnectionException;
import com.fn.reunion.app.controller.UserStateType;
import com.fn.reunion.app.model.Contact;
import com.fn.reunion.app.model.FriendTempData;
import com.fn.reunion.app.model.FriendsRoster;
import com.fn.reunion.app.ui.privateChat.MessageActivity;
import com.fn.reunion.app.ui.searchfriends.NewChatActivity;
import com.fn.reunion.app.xmpp.NotificationService;
import com.fn.reunion.app.xmpp.XmppManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.poppyview.PoppyViewHelper;

public class BuddiesListPage extends Fragment {

    ListView list;
	Connection connection;
	private static XmppManager xmppManager;
	private static ArrayList<FriendTempData> friends;
	private static BuddyAdapter adapter;
	public  List<FriendTempData> roster;
	private Handler buddiesHandler;

    private PoppyViewHelper mPoppyViewHelper;
	private String TAG = "BuddiesList";
	
	public static void updateRoster(List<Contact> registeredContacts){
		
	   friends  = xmppManager.retrieveFriendList();
	   
	   for (FriendTempData friend : friends) {
		   
			String userId  = StringUtils.parseName(friend.getUserID());
			
			for (Contact contact : registeredContacts) {
			   if (userId.equals(contact.getPhoneNumber())) {
				friend.setNickname(contact.getName());
			   }
			}
		}
	   
        // presence friends using singleTone
		FriendsRoster.setFriendsRoster(friends);
		
	/*
		 * print RosterList after change
		 */
		/*for (FriendTempData friend : friends) {
			//Log.d("BuddiesList", "userName:"+friend.getNickname());
		   }*/
	   
		adapter.updateAdapter(friends);
	}
    
	public static void presenceChanged(String fromUserId,Mode mode){
		
		Log.i("BuddiesList", "presenceChanged");

		
		if (xmppManager == null) {
			return;
		}
		
    	if (xmppManager.isConnected() && friends != null) {
    		Log.i("BuddiesList", friends.size()+" user");
    		fromUserId = StringUtils.parseName(fromUserId);
    	 	// search on user
    		for (FriendTempData friend : friends) {
    		   String userId  = StringUtils.parseName(friend.getUserID());
				if (userId.equals(fromUserId) ) {
					Log.d("BuddiesList", "presenceChanged() to user: "+fromUserId +"  Mode : " +mode+
							"to string :"+mode.toString()+" length :"+mode.toString().length());
					if (mode.equals(Mode.available)||mode.equals(Mode.away)||mode.equals(Mode.chat)){
					    friend.setState(UserStateType.ONLINE);
					    break;
					}else
					   if (mode.equals(Mode.xa)|| mode.equals(Mode.dnd)){
						   friend.setState(UserStateType.OFFLINE);
						   break;
						}
				}
			 }
    		//Log.i("BuddiesList", FriendsRoster.getInstance().getFriendsRoster().size()+" user getInstance()");
        	adapter.updateAdapter(friends);
		}
    }
	
	public static void incomingMessage(String fromUserID, String toUserID, String message){
		Log.i("BuddiesList", "incomingMessage() fromUserID "+ fromUserID + 
				" toUserID "+ toUserID +" with message : "+ message);
		
		for (FriendTempData friend : friends) {
			//Log.d("BuddiesList",friend.toString());
 		   String userId  = StringUtils.parseName(friend.getUserID());
				if (userId.equals(fromUserID) ) {
					friend.setUnreadMesssageCount(friend.getUnreadMesssageCount()+1);
					adapter.updateAdapter(friends);
					break;
				  }
				}
	}
   
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    buddiesHandler = new Handler();
            mPoppyViewHelper = new PoppyViewHelper(getActivity());
        View rootView = inflater.inflate(R.layout.buddies, container, false);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		list = (ListView) getActivity().findViewById(R.id.buddiesList);

        createFloatButton();
		
        registerForContextMenu(list);
        try {
        	xmppManager = NotificationService .getInstance().getXmppManager();		  
            if (!xmppManager.isConnected()) {
    			   buddiesHandler.postDelayed(new loadBuddiesList(),2000);
    		   }else{
    			   friends  = xmppManager.retrieveFriendList();
    			   viewRosters();
    		   }
		} catch (Exception e) {
		}
		
        
		// SyncXMPPUsers.initiate(getBaseContext());
	}



    @Override
	public void onPause() {
		super.onPause();
	  try {
			if (friends != null  ) {
				//friends  = xmppManager.retrieveFriendList();
			//adapter.updateAdapter(friends);
			}
		} catch (NullPointerException e) {
		}
	}

    private void createFloatButton() {

        FloatingActionButton buttonAction = (FloatingActionButton)getActivity().findViewById(R.id.floatButton);
        buttonAction.setSize(FloatingActionButton.SIZE_MINI);
        //buttonAction.setColorNormalResId(R.color.pink);
        // buttonAction.setColorPressedResId(R.color.pink_pressed);
        //buttonAction.setIcon(R.drawable.ic_fab_star);

        buttonAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),NewChatActivity.class));
            }
        });
    }


    private void viewRosters() throws XMPPException {

		adapter = new BuddyAdapter(getActivity(), friends);
		
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 Intent intent = new Intent(getActivity(),
				 MessageActivity.class);
				  roster.get(position).setUnreadMesssageCount(0);
				  adapter.updateAdapter(friends);
				 intent.putExtra("username", adapter.getItem(position).getUserID());
				 intent.putExtra("nickName", adapter.getItem(position).getNickname());
				 startActivity(intent);
			}
		});

        createPoppyViewOnListView();

    }

    private void setAvatar(){

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.user_chat2);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();


        try {
            xmppManager.setAvatarPicture(bitMapData);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        byte[] byteArray = new byte[0];
        try {
            byteArray = xmppManager.getAvatarPicture("6896636666");
            Bitmap my = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //holder.thumb.setImageResource(R.drawable.icon_no_profile_pic);
            //holder.thumb.setImageBitmap(bitmap);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        catch (Exception m){

        }

    }

    private void createPoppyViewOnListView(){


        View poppyView = mPoppyViewHelper.createPoppyViewOnListView(R.id.buddiesList, R.layout.poppyview, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "onScroll");
            }
        });


        poppyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Toast.makeText(getActivity(), "Click me!", Toast.LENGTH_SHORT).show();
            }
        });


    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    if (v.getId()==R.id.buddiesList) {
    	    menu.setHeaderTitle("Actions");
    		String[] menuItems = getResources().getStringArray(R.array.menu_roster); 
    		for (int i = 0; i<menuItems.length; i++) {
    			menu.add(Menu.NONE, i, i, menuItems[i]);
			}
    	}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo
        = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
       int adapterPosition = menuInfo.position;
	    Log.i("BuddiesList", "adapterPosition "+adapterPosition);

	    int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.menu_roster);
		String menuItemName = menuItems[menuItemIndex];
	    //Log.i("BuddiesList", menuItemIndex+""+menuItemName);
		
		switch (menuItemIndex) {
		case 0:
			deleteUser(adapterPosition);
			break;
		case 1:
    		Log.i("BuddiesList", FriendsRoster.getInstance().getFriendsRoster().size()+" user getInstance()");
		default:
			break;
		}
	    return true;
  }
	
	private void deleteUser(final int adapterPosition){
		final String userId = friends.get(adapterPosition).getUserID();
		
		  new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
          .setTitleText("Are you sure?")
          .setContentText("Won't be able to chat with this user!")
          .setConfirmText("Yes,delete it!")
          .setCancelText("No,cancel!")
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
          @Override
          public void onClick(SweetAlertDialog sDialog) {
        	try {
                  if (xmppManager.removeFriend(userId)){
        				adapter.removeObject(adapterPosition);
        			       sDialog.setTitleText("Deleted!")
                           .setContentText("User has been deleted!")
                           .setConfirmText("OK")
                           .setConfirmClickListener(null)
                           .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        			}else{
        			       sDialog.setTitleText("Deleted!")
                           .setContentText("can't remove user")
                           .setConfirmText("OK")
                           .setConfirmClickListener(null)
                           .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        				//Toast.makeText(getActivity(), "can't remove user", Toast.LENGTH_SHORT).show();
        			}
        			
        		} catch (BadConnectionException e) {
        			e.printStackTrace();
        		}
          }
          })
          .show();
	} 
    
	private class loadBuddiesList implements Runnable{

		@Override
		public void run() {
			Log.d(TAG, "loadBuddiesList.run()");
			try {
				 xmppManager = NotificationService .getInstance().getXmppManager();
				 if (xmppManager.isConnected()) {
					   friends  = xmppManager.retrieveFriendList();
					   viewRosters();
					   Log.i(TAG, "loadBuddiesList XMPP Connected");
					   Log.i(TAG, "loadBuddiesList friends size:"+friends.size());
                       Crouton.makeText(getActivity(), "Connected to server", Style.INFO).setConfiguration(Configuration.DEFAULT).show();
                 }else{
					   Log.d(TAG, "loadBuddiesList not connected ...");
                       Crouton.makeText(getActivity(), "Trying to Connect to server", Style.CONFIRM).setConfiguration(Configuration.DEFAULT).show();
                       buddiesHandler.postDelayed(this, 3000);
                 }
				 
			 } catch (Exception e) {
                Log.e(TAG, "loadBuddiesList Exception");
                buddiesHandler.postDelayed(this, 3000);
                Crouton.makeText(getActivity(), "Trying to Connect to server", Style.CONFIRM).setConfiguration(Configuration.DEFAULT).show();
            }
		}
    }

    class BuddyAdapter extends BaseAdapter {
	    
		public BuddyAdapter(Context context, List<FriendTempData> items) {
			roster = items;
		}

		@Override
		public int getCount() {
			return roster.size();
		}
		
		public void removeObject(int index){
			roster.remove(index);
			this.notifyDataSetChanged();
		}//

		@Override
		public FriendTempData getItem(int index) {
			return roster.get(index);
		}

		@Override
		public long getItemId(int index) {
			return 0;
		}
		
	    public void updateAdapter(List<FriendTempData> newRoster) {
		        roster= newRoster;
		        notifyDataSetChanged();
		    }

	    public void refresh(List<FriendTempData> newRoster) {
			    roster.clear();
			    roster = newRoster;
		        this.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			
			FriendTempData friend = roster.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.buddy_row, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.buddyName);
				holder.status = (TextView) convertView.findViewById(R.id.status);
				holder.thumb = (ImageView) convertView.findViewById(R.id.buddyThumb);
				holder.badge = (TextView) convertView.findViewById(R.id.badge_unread_message);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.name.setText(friend.getNickname());
			
			if (friend.getUnreadMesssageCount() > 0 ) {
				holder.badge.setBackgroundDrawable(getResources().getDrawable(R.drawable.badge_unread_messages_counter));
				holder.badge.setText(friend.getUnreadMesssageCount()+"");
			}else{
				holder.badge.setText("");
				holder.badge.setBackgroundDrawable(null);
			}
			
			if (friend.getState() == UserStateType.ONLINE){
			    holder.status.setBackgroundDrawable(getResources().getDrawable(R.drawable.badge_online));
			}else{
			    holder.status.setBackgroundDrawable(getResources().getDrawable(R.drawable.badge_offline));
			}


			
			return convertView;
		}
	}
	
	class ViewHolder {
		ImageView thumb;
		TextView name,status,badge;
	}
}