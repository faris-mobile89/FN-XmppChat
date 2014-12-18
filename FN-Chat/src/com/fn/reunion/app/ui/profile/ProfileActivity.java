package com.fn.reunion.app.ui.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fn.reunion.app.R;
import com.fn.reunion.app.json.JSONParser;
import com.fn.reunion.app.utility.ImageUtillity;
import com.fn.reunion.app.utility.SessionManager;
import com.fn.reunion.app.utility.SharedPreferences;
import com.fn.reunion.app.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileActivity extends Activity {

    private CheckBox showLocationCheckBox;
    private ImageView userFullImageView,image_action_edit_image;
    private  UploadProfileImage uploadProfileImage;

    private final String TAG = ProfileActivity.class.getSimpleName();

    private final DisplayImageOptions options =  new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_profile);
        setTitle("Profile");
        defineViews();
        defineListenerOnViews();
        uploadProfileImage = new UploadProfileImage();
    }

    private void defineViews() {

        showLocationCheckBox =(CheckBox)findViewById(R.id.showLocationCheckBox);
        showLocationCheckBox.setChecked(SharedPreferences.getBooleanFormSharedPref(
                getBaseContext(),Constants.KEY_SHOW_MY_LOCATION));

        userFullImageView = (ImageView)findViewById(R.id.user_fullImage);
        setImageResource();

        image_action_edit_image = (ImageView)findViewById(R.id.image_action_edit_image);


    }

    private void setImageResource() {

        final ProgressBar spinner = (ProgressBar)findViewById(R.id.loading);

        ImageLoader.getInstance().displayImage("http://www.google.com/asdf9sdf.png",userFullImageView,options,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                spinner.setVisibility(View.GONE);
                userFullImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_user_default));
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                spinner.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private void defineListenerOnViews(){

        showLocationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.putBooleanToSharedPref(getBaseContext(),Constants.KEY_SHOW_MY_LOCATION,isChecked);
            }
        });

        image_action_edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RUN IMAGE UPLOADER
                if (uploadProfileImage !=null)
                 uploadProfileImage.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
       if (uploadProfileImage != null)
          uploadProfileImage.cancel(true);
    }

    private class UploadProfileImage extends AsyncTask<String,String,String>{

        private  String userPhone ;
        String strBase64Image;
        SweetAlertDialog pDialog;
        @Override
        protected void onPreExecute() {

            pDialog = new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("please wait...");
            pDialog.show();

            userPhone = new SessionManager(ProfileActivity.this).getUserDetails().getPhone();
            //TODO make user choose his image
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.user_chat2);
            strBase64Image = ImageUtillity.getBase64Image(bitmap1);

        }

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;
            try {

                List<NameValuePair> listParams = new ArrayList<NameValuePair>();
                //TODO move params constants to AppConstants class
                listParams.add(new BasicNameValuePair("tag", "uploadProfileImage"));
                listParams.add(new BasicNameValuePair("userPhone", userPhone));
                listParams.add(new BasicNameValuePair("image", strBase64Image));

                jsonObject =
                        jsonParser.makeHttpRequest(
                                Constants.HOST + Constants.API_URL,
                                "POST",
                                listParams);

            }catch (Exception e){
                Log.e(TAG,"Exception while uploading user image");
            }

            return jsonObject.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            pDialog.dismiss();
            Log.d(TAG,"returned response : "+response);
            if (response != null){
                try {
                    //TODO please change this KEYS to appConst Class
                    JSONObject jsonObject = new JSONObject(response);
                    int errorCode = jsonObject.getInt(Constants.KEY_ERROR);
                     Log.d(TAG,"errorCode = "+errorCode);
                     if ( errorCode == Constants.ERROR_CODE){
                        Log.w(TAG,"server response with error code "+errorCode);
                         showErrorMessage();
                     }else{
                         Log.i(TAG,"User image saved successfully !");
                         new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                 .setTitleText("Image updated successfully")
                                 .setContentText("")
                                 .show();
                     }
                } catch (JSONException e) {
                    Log.e(TAG,"JSONException");
                    e.printStackTrace();
                }catch (Exception e){}
            }else {
                showErrorMessage();
                Log.e(TAG,"response null");
            }
        }
        private void showErrorMessage(){

            new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Image update failed!")
                    .setContentText("")
                    .show();
        }
    }
}
