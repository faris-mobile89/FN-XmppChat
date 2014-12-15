package com.fn.reunion.app.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.fn.reunion.app.Constants;
import com.fn.reunion.app.json.JSONParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SearchFriendsTask extends AsyncTask<String, String, String> {
	
	private TaskDelegate delegate;
	private JSONObject jsonObject = null;
	private List<NameValuePair> listParams = new ArrayList<NameValuePair>();
	private final String tag = SearchFriendsTask.class.getSimpleName();

	public SearchFriendsTask(Context contextGiven, TaskDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {

		listParams.add(new BasicNameValuePair(Constants.KEY_TAG, params[2]));
		listParams.add(new BasicNameValuePair(Constants.KEYWORD, params[3]));
		listParams.add(new BasicNameValuePair(Constants.KEY_GENDER, params[4]));

		jsonObject = null;
		try {

			jsonObject = JSONParser.makeHttpRequest(params[0], params[1],
					listParams);

		} catch (NullPointerException e) {
			Log.e(tag, e.getMessage());
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
