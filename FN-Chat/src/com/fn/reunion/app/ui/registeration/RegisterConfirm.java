package com.fn.reunion.app.ui.registeration;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.R;
import com.fn.reunion.app.model.SuggestedFriend;
import com.fn.reunion.app.model.UserInfo;
import com.fn.reunion.app.tasks.ConfirmRegisterTask;
import com.fn.reunion.app.tasks.DefualtFriendsTask;
import com.fn.reunion.app.tasks.TaskDelegate;
import com.fn.reunion.app.ui.base.AppBaseActivity;
import com.fn.reunion.app.ui.searchfriends.SuggestedFriendsActivity;
import com.fn.reunion.app.utility.SessionManager;

public class RegisterConfirm  extends Activity implements TaskDelegate{
  private EditText editCode;
  private ConfirmRegisterTask confirmRegisterTask;
  private DefualtFriendsTask defualtFriendsTask;
  private SessionManager session;
  private Context context;
  private String phone,familyName;
  private String tag = "RegisterConfirm";
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_confirm);
		editCode = (EditText)findViewById(R.id.edittext_searchKeyword);
		context = getBaseContext();
		session = new SessionManager(context);
	}
	
	public void verfiyAccount(View view){
		
    if (confirmRegisterTask != null ) {
    	confirmRegisterTask.cancel(true);
	}
        
      Bundle bundle = getIntent().getExtras();
        
        if (bundle != null ) {
           phone = getIntent().getExtras().getString(Constants.KEY_PHONE);
           familyName = getIntent().getExtras().getString(Constants.KEY_LAST_NAME);
		}

      confirmRegisterTask = new ConfirmRegisterTask(context, this);
      defualtFriendsTask = new DefualtFriendsTask(getBaseContext(), this);
	  String code = editCode.getText().toString();
	 
	  Log.i(tag, "Phone "+phone);
	  Log.i(tag, "Family "+familyName);
	 
		if ( code.length() > 0) {
			
			  confirmRegisterTask.execute(
					         Constants.HOST + Constants.API_URL,
					         Constants.KEY_GET,
					         Constants.KEY_TAG_CONFIRM, phone , code);
		}
		//view.setEnabled(false);
	}

	@Override
	public void taskCompletionResult(String taskName,String result) {
		Log.d(tag, "taskName= "+taskName);
		
		if ( taskName.equals(ConfirmRegisterTask.class.getSimpleName()) ) {
			
			if (result == Constants.KEY_SUCCESS) {
				UserInfo user = new UserInfo();
				user.setConfirmed(true);
				user.setPhone(phone);
				user.setFname(familyName);
				session.createLoginSession(user);
				//startActivity(new Intent(context, AppMainActivity.class));
				//finish();
				defualtFriendsTask.execute(
					         Constants.HOST + Constants.API_URL,
					         Constants.KEY_GET,
					         Constants.KEY_TAG_SEARCH_USERS,familyName);
			}
		}else if ( taskName.equals(DefualtFriendsTask.class.getSimpleName()) ) {
			
			Log.d(tag, "response = "+result);
			
			ArrayList<SuggestedFriend> lisSuggestedFriends = new ArrayList<SuggestedFriend>();
			
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
					
				   ///Open suggest friends activity
					final Bundle b = new Bundle();
					b.putSerializable(Constants.KEY_SUGG_FRIENDS, lisSuggestedFriends);
				     new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                     .setTitleText("Verification!")
                     .setContentText("You have been verified!")
                     .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							startActivity(new Intent(context, SuggestedFriendsActivity.class).putExtras(b));
							finish();
						}
					})
                     .show();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			   catch (NullPointerException e) {
					e.printStackTrace();
			 }

		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (confirmRegisterTask !=null) {
			confirmRegisterTask.cancel(true);
		}
	}
}
