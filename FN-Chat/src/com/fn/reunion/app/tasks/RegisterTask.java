package com.fn.reunion.app.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.json.JSONParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RegisterTask extends AsyncTask<String, String, String> {
	private TaskDelegate delegate;
	
	private List<NameValuePair> listParams = new ArrayList<NameValuePair>();
	private final String tag = DefualtFriendsTask.class.getSimpleName();

	public RegisterTask(Context contextGiven, TaskDelegate delegate) {
     this.delegate = delegate;
    }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		
		listParams.add(new BasicNameValuePair(Constants.KEY_TAG, params[2]));
		listParams.add(new BasicNameValuePair(Constants.KEY_FIRST_NAME, params[3]));
		listParams.add(new BasicNameValuePair(Constants.KEY_LAST_NAME, params[4]));
		listParams.add(new BasicNameValuePair(Constants.KEY_PHONE, params[5]));
		
		JSONObject jsonObject = null;
		try {
			
			jsonObject  = JSONParser.makeHttpRequest(params[0], params[1], listParams);

		} catch (NullPointerException e) {
			Log.e("RegisterTask", e.getMessage());
		}
		
		if (jsonObject != null) {
			return jsonObject.toString();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		delegate.taskCompletionResult(tag,result);
	}
}
