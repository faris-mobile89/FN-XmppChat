package com.fn.reunion.app.ui.registeration;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.R;
import com.fn.reunion.app.model.UserInfo;
import com.fn.reunion.app.tasks.ConfirmRegisterTask;
import com.fn.reunion.app.tasks.RegisterTask;
import com.fn.reunion.app.tasks.TaskDelegate;
import com.fn.reunion.app.ui.base.AppBaseActivity;
import com.fn.reunion.app.utility.FNSerializableManager;
import com.fn.reunion.app.utility.SessionManager;

public class RegisterationActivity extends Activity implements TaskDelegate {
	
	EditText txtPhone,textFamilyName;
	String phone,familyName;
    SessionManager session;
    private RegisterTask registerTask;
    private final String tag  = RegisterationActivity.class.getSimpleName();
    SweetAlertDialog pDialog;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		 getActionBar().hide();
		
		 setContentView(R.layout.registeration_layout);		
		 txtPhone =  (EditText)findViewById(R.id.editPhone);
		 textFamilyName = (EditText)findViewById(R.id.editFamilyName);
		 session = new SessionManager(getBaseContext());
	}
	
	public void registerUser(View v){
		
		phone = txtPhone.getText().toString();
		familyName = textFamilyName.getText().toString();
		
		if (familyName.length() <  2 ) {
			
			 new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
             .setTitleText("Registeration Error")
             .setContentText("Please fill all field!")
             .show();
			  
			 textFamilyName.setError("Family Name is not valid!");
			return;
		}
	
		if (phone.length() <  8 ) {
			txtPhone.setError( "Phone number is not valid!" ); 
			return;
		}
		
		 pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
         .setTitleText("Loading");
		  pDialog.show();
		
		 registerTask = new RegisterTask(getBaseContext(), this);
		 registerTask.execute(
				              Constants.HOST+Constants.API_URL
				              ,"GET","regUser","faris" ,familyName,phone);
	}

	@Override
	public void taskCompletionResult(String taskName, String result) {
		registerTask = null;
		pDialog.dismiss();
		JSONObject jsonObject = null;
		UserInfo user = new UserInfo();
		
	 try {
			jsonObject = new JSONObject(result);
			
			boolean isConfirmd = jsonObject.getInt(Constants.KEY_CONFIRMED) ==
					             Constants.CONFIRMED_FLAG ? true :false;
			
			user.setConfirmed(isConfirmd);
			user.setPhone(phone);
			user.setLname(familyName);
			
		} catch (NullPointerException e) {
			Log.e(tag, "NullPointerException");
			return;
		}
	 
		 catch (JSONException e) {
			 Log.e(tag,e.getMessage() );
		}

		if (user.isConfirmed()) {
			session.createLoginSession(user);
			startActivity(new Intent(getBaseContext(), AppBaseActivity.class));
			finish();
		}else {
			startActivity(new Intent(getBaseContext(),
					RegisterConfirm.class).putExtra(Constants.KEY_PHONE, phone).
					putExtra(Constants.KEY_LAST_NAME, familyName));
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (registerTask !=null) {
			registerTask.cancel(true);
		}
	}
	
	private void presentObject(){
		
		List <UserInfo> list = new ArrayList<UserInfo>();
		
		UserInfo user = new UserInfo("faris", "sf", "saleem", "fn@gmail.com", "23423");
		list.add(user);

		FNSerializableManager.wirteObject(getBaseContext(), list, "UserInfos");		
		List <UserInfo> memList = FNSerializableManager.loadSerializedUserInfo(getBaseContext(), "UserInfos");
		for (UserInfo userInfo : memList) {
			Log.i(tag, userInfo.toString());
			}
		
		Log.i(tag, FNSerializableManager.getObject(getBaseContext(), "UserInfo").toString());
	}
}
