package com.fn.reunion.app.ui.searchfriends;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.R;
import com.fn.reunion.app.adapters.SuggestedListAdapter;
import com.fn.reunion.app.model.SuggestedFriend;
import com.fn.reunion.app.ui.base.AppBaseActivity;

public class SuggestedFriendsActivity extends Activity {
	
	private ArrayList<SuggestedFriend> lisSuggestedFriends;
	
	private SuggestedListAdapter adapter;
	private ListView listView;
	private Button btnAddFriends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.suggested_friends_layout);
		listView = (ListView)findViewById(R.id.listView);
		btnAddFriends =(Button)findViewById(R.id.addFriendsButton);
		
		try{
		    Bundle bundleObject = getIntent().getExtras();
		      // Get ArrayList Bundle
		    @SuppressWarnings("unchecked")
			ArrayList<SuggestedFriend> classObject = (ArrayList<SuggestedFriend>) bundleObject.getSerializable(Constants.KEY_SUGG_FRIENDS);
		    lisSuggestedFriends = classObject;
		        //Retrieve Objects from Bundle
		       for(int index = 0; index < lisSuggestedFriends.size(); index++){
		    	 SuggestedFriend Object = lisSuggestedFriends.get(index);
		    	 Log.i("Found", Object.toString());
		    }
		       adapter = new SuggestedListAdapter(getBaseContext(), lisSuggestedFriends);
		       listView.setAdapter(adapter);
		} catch(Exception e){
		    e.printStackTrace();
		}
		
		btnAddFriends.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<SuggestedFriend> tempFirdSuggestedFriends = adapter.getCheckedFriendBox();
				
			    /*String result = "Selected Friends are :";
			    for (SuggestedFriend p : tempFirdSuggestedFriends) {
			      if (p.isBox()){
			        result += "\n" + p.getName()+" "+p.getJabberId();
			      }
			    }
			    Toast.makeText(SuggestedFriendsActivity.this, result+"\n", Toast.LENGTH_LONG).show();*/
			    
			    // pass friends to MainActivity
			    
				Bundle b = new Bundle();
				b.putSerializable(Constants.KEY_SUGG_FRIENDS, tempFirdSuggestedFriends);
				b.putBoolean("isAddFriend", true);
				startActivity(new Intent(SuggestedFriendsActivity.this, 
						AppBaseActivity.class).putExtras(b));
				finish();
			  }
		});
	}
	
	public void cancelButton(View view){
		startActivity(new Intent(SuggestedFriendsActivity.this, 
				AppBaseActivity.class));
		finish();
	}
}
