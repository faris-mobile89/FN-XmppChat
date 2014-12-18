package com.fn.reunion.app.ui.base;
 
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.fn.reunion.app.R;
import com.fn.reunion.app.model.FriendTempData;
import com.fn.reunion.app.ui.privateChat.MessageActivity;
import com.fn.reunion.app.ui.profile.ProfileActivity;
import com.fn.reunion.app.utility.CircleBitmapDisplayer;
import com.fn.reunion.app.xmpp.NotificationService;
import com.fn.reunion.app.xmpp.XmppManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class MenuListFragment extends ListFragment {
 
    private List<SampleItem> mMenuItems = new ArrayList<SampleItem>();
    private Context mContext;
    private DisplayImageOptions optionsC;
    private static XmppManager xmppManager;
    private Handler buddiesHandler;
    private static ArrayList<FriendTempData> friends;
    private ListMenuAdapter menuAdapter;
    
    private String TAG = "MenuListFragment";
    
    public MenuListFragment(){
    }
 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mContext = getActivity();
    	buddiesHandler = new Handler();
    	menuAdapter = new ListMenuAdapter();
        return inflater.inflate(R.layout.list, null);
    }
 
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    	 optionsC =  new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new CircleBitmapDisplayer(0xffffffff, 2))
			.build();
      try {
         	xmppManager = NotificationService .getInstance().getXmppManager();		  
             if (!xmppManager.isConnected()) {
     			   buddiesHandler.postDelayed(new loadBuddiesList(),2000);
     		   }else{
     			   friends  = xmppManager.retrieveFriendList();
     			   viewRosters();
     		     }
 		} catch (Exception e) {
 			Log.e(TAG, "Exception on getting friendsList");
 			 buddiesHandler.postDelayed(new loadBuddiesList(),2000);
 			 mMenuItems.add(new SampleItem("", RowType.ACCOUNT));
 		     mMenuItems.add(new SampleItem("Navigation", RowType.HEADER));
 		     mMenuItems.add(new SampleItem("News", R.drawable.ic_tab_rss, RowType.NAVIGATION));
 		     mMenuItems.add(new SampleItem("Chats", R.drawable.ic_tab_chat, RowType.NAVIGATION));
 		     setListAdapter(menuAdapter);
 		}
    }
    
    private void viewRosters() {
    	
    	 mMenuItems.clear();
    	 mMenuItems.add(new SampleItem("", RowType.ACCOUNT));
         mMenuItems.add(new SampleItem("Navigation", RowType.HEADER));
         mMenuItems.add(new SampleItem("News", R.drawable.ic_tab_rss, RowType.NAVIGATION));
         mMenuItems.add(new SampleItem("Chats", R.drawable.ic_tab_chat, RowType.NAVIGATION));
         mMenuItems.add(new SampleItem("Friends", RowType.HEADER));
         for (int i = 0; i < friends.size(); i++) {
        	 mMenuItems.add(new SampleItem(friends.get(i), R.drawable.user, RowType.FRIENDS));
		}

         setListAdapter(menuAdapter);
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
				   }else{
					   Log.d(TAG, "loadBuddiesList not connected ...");
					   buddiesHandler.postDelayed(this, 3000);
				   }
				 
			 } catch (Exception e) {
			}
		}
    	
    }
 
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

     SampleItem item = mMenuItems.get(position);

        if (item.getRowType().equals(RowType.ACCOUNT)) {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        }
        else if (item.getRowType().equals(RowType.NAVIGATION)) {
               switch (position) {
               case 2:
                 ((AppBaseActivity) getActivity()).viewPager.setCurrentItem(0);
                 break;
                case 3:
                 ((AppBaseActivity) getActivity()).viewPager.setCurrentItem(1);
                 break;
         }
           ((AppBaseActivity)getActivity()).getSlidingMenu().showContent();

       }else  if (item.getRowType().equals(RowType.FRIENDS)){
           Intent intent = new Intent(getActivity(),MessageActivity.class);
           intent.putExtra("username", item.getFriend().getUserID());
           intent.putExtra("nickName", item.getFriend().getNickname());
           startActivity(intent);
          ((AppBaseActivity)getActivity()).getSlidingMenu().showContent();

     }
        super.onListItemClick(l, v, position, id);
    }
    
    private class ListMenuAdapter extends BaseAdapter{

    	
		@Override
		public int getCount() {
			return mMenuItems.size();
		}

		@Override
		public SampleItem getItem(int position) {
			return mMenuItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public int getViewTypeCount() {
			
			return RowType.values().length;
		}
		
		@Override
		public int getItemViewType(int position) {
			
			return getItem(position).rowType.ordinal();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			SampleItem mItem = getItem(position);
			
			if (convertView == null) {
				convertView = getInflatedLayoutForType(getItemViewType(position), parent);
			}
	
			if (mItem.rowType == RowType.ACCOUNT) {
				TextView fullName = (TextView)convertView.findViewById(R.id.menu_userName);
				//fullName.setText(new SessionManager(getActivity()).getUserDetails().getLname());
				fullName.setText("Faris Abu Saleem");
				String imageUri= "drawable://"+R.drawable.user_chat2;
				
				ImageLoader imageLoader = ImageLoader.getInstance();
			    
			    ImageSize targetSize = new ImageSize(300, 300);
			    Bitmap bitmapThumb =  imageLoader.loadImageSync(imageUri, targetSize, optionsC);
			    BootstrapCircleThumbnail m = 
			    		(BootstrapCircleThumbnail)convertView.findViewById(R.id.menu_userThumbnail);
			    m.setImage(bitmapThumb);
				
			}else if(mItem.rowType == RowType.NAVIGATION){
				
				ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
				icon.setImageResource(mItem.iconRes);
				TextView title = (TextView) convertView.findViewById(R.id.row_title);
				title.setText(mItem.tag);
			}
             else if(mItem.rowType == RowType.FRIENDS){
            	String imageUri = "https://lh3.googleusercontent.com/-JcLbIQ8e0I8/Ti_HGnBojII/AAAAAAAAC5Q/WxHgOHrYL54/w406-h404-no/4085554266_624eb7d638_z.jpg";
				ImageView icon = (ImageView) convertView.findViewById(R.id.buddyThumb);
				//ImageLoader.getInstance().displayImage(imageUri, icon, optionsC);
				TextView name = (TextView) convertView.findViewById(R.id.buddyName);
                FriendTempData mFriend = mItem.getFriend();
				name.setText(mFriend.getNickname());
				
			}
             else if(mItem.rowType == RowType.HEADER){
 				TextView headerTitle = (TextView) convertView.findViewById(R.id.menu_header_tilte);
 				headerTitle.setText(mItem.tag);
 			}
			
			return convertView;
		}
		
		private View getInflatedLayoutForType(int typePosition, ViewGroup parent) {
			if (typePosition == RowType.ACCOUNT.ordinal()) {
				return LayoutInflater.from(getActivity()).inflate(R.layout.menu_row_user, null);
			} else if (typePosition == RowType.NAVIGATION.ordinal()) {
				return LayoutInflater.from(mContext).inflate(R.layout.menu_list_row, null);
			}
			 else if (typePosition == RowType.HEADER.ordinal()) {
				return LayoutInflater.from(mContext).inflate(R.layout.menu_header_row, null);
		     }
			 else if (typePosition == RowType.FRIENDS.ordinal()) {
					return LayoutInflater.from(mContext).inflate(R.layout.menu_friend_row, null);
			}
			 else {
				return null;
			}
		}
    }
    
    private class SampleItem {
    	private String tag;
    	private int iconRes;
    	private RowType rowType;
        private FriendTempData friend;
    	
    	
    	
    	public SampleItem(String hederTitle , RowType type) {
 			this.tag = hederTitle;
 			this.rowType = type;
 		}

 		public SampleItem(String tag, int iconRes , RowType type) {
            this.tag = tag;
            this.iconRes = iconRes;
            this.rowType = type;
        }

        public SampleItem(FriendTempData mFriend, int iconRes , RowType type) {
            this.friend=mFriend;
            this.iconRes = iconRes;
            this.rowType = type;
        }


		public RowType getRowType() {
			return rowType;
		}

        public FriendTempData getFriend() {
            return friend;
        }
    }
    

    private static enum RowType {
	    ACCOUNT, NAVIGATION,HEADER,FRIENDS,
	}
    
    
}