package com.fn.reunion.app.ui.searchfriends;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.R;
import com.fn.reunion.app.adapters.SuggestedListAdapter;
import com.fn.reunion.app.model.SuggestedFriend;
import com.fn.reunion.app.tasks.SearchFriendsTask;
import com.fn.reunion.app.tasks.TaskDelegate;
import com.fn.reunion.app.ui.base.AppBaseActivity;
import com.kanak.emptylayout.EmptyLayout;

public class SearchFriendsActivity extends Activity implements TaskDelegate {
	
	private ArrayList<SuggestedFriend> lisSuggestedFriends;
	
	private EmptyLayout mEmptyLayout; // this is required to show different layouts (loading or empty or error)
	
	private View.OnClickListener mErrorClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Toast.makeText(SearchFriendsActivity.this, "Try again button clicked", Toast.LENGTH_LONG).show();			
		}
	};
	
	private SuggestedListAdapter adapter;
	private ListView listView;
	private Button btnAddFriends,cancelButton;
	private SearchFriendsTask searchFriendsTask;
	private RelativeLayout controlHolder;
	private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.search_result_friends_layout);
		 
		controlHolder = (RelativeLayout)findViewById(R.id.controlHolder);
		controlHolder.setVisibility(View.GONE);
		listView = (ListView)findViewById(R.id.listView);
		btnAddFriends =(Button)findViewById(R.id.addFriendsButton);
		cancelButton =(Button)findViewById(R.id.cancelButton);
		
		// initialize the empty view
		mEmptyLayout = new EmptyLayout(this, listView);
		mEmptyLayout.setErrorButtonClickListener(mErrorClickListener);
		
		mEmptyLayout.showLoading();
		searchFriendsTask = new SearchFriendsTask(getBaseContext(), this);
		
		String keyword,gender;
		
		Bundle bundle = getIntent().getExtras();
		keyword = bundle.getString("keyword");
		gender = bundle.getString("gender");
		
	
		searchFriendsTask.execute(
		         Constants.HOST + Constants.API_URL,
		         Constants.KEY_GET,
		         Constants.KEY_TAG_SEARCH_USERS_ADVANCED,keyword,gender);
		  
		 pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
          .setTitleText("Loading");
		  pDialog.show();
	
		btnAddFriends.setEnabled(false);
		btnAddFriends.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<SuggestedFriend> tempFirdSuggestedFriends = adapter.getCheckedFriendBox();
				  
				Bundle b = new Bundle();
				b.putSerializable(Constants.KEY_SUGG_FRIENDS, tempFirdSuggestedFriends);
				b.putBoolean("isAddFriend", true);
				startActivity(new Intent(SearchFriendsActivity.this, 
						AppBaseActivity.class).putExtras(b));
				finish();
			  }
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SearchFriendsActivity.this, 
						AppBaseActivity.class));
				finish();
			}
		});
	}
	
	@Override
	public void taskCompletionResult(String taskName, String result) {
		
	     lisSuggestedFriends = new ArrayList<SuggestedFriend>();
	     pDialog.dismiss();
			
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				if (jsonObject.getString(Constants.KEY_ERROR).equals("0")) {
					JSONArray jsonArray = new JSONArray(jsonObject.getString(Constants.KEY_TAG_USERS));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = jsonArray.getJSONObject(i);
						
						lisSuggestedFriends.add(
								new SuggestedFriend(
									       obj.getString(Constants.KEY_FULL_NAME),
									       obj.getString(Constants.KEY_JABBER_ID ))
								);
					}
					
					 adapter = new SuggestedListAdapter(getBaseContext(), lisSuggestedFriends);
				     listView.setAdapter(adapter);
				     btnAddFriends.setEnabled(true);
				     controlHolder.setVisibility(View.VISIBLE);

				}else{
					
					/**
					 * empty result
					 */
					
					 new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                     .setTitleText("Search Result")
                     .setContentText("No Results Found!")
                     .show();
					mEmptyLayout.setEmptyMessage("No Results Found");
					mEmptyLayout.showEmpty();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			   catch (NullPointerException e) {
					e.printStackTrace();
			 }
	}
}
