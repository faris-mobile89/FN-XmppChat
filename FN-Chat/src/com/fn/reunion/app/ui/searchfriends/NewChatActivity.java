package com.fn.reunion.app.ui.searchfriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fn.reunion.app.R;

public class NewChatActivity  extends Activity{
	
    EditText editSearchKeyword;
    RadioButton radioButtonMale,radioButtonFemale;
    Button buttonSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_layout);
		editSearchKeyword = (EditText)findViewById(R.id.edittext_searchKeyword);
		buttonSearch = (Button)findViewById(R.id.buttonSearch);
		
		radioButtonMale =  (RadioButton)findViewById(R.id.radioMale);
		radioButtonFemale= (RadioButton)findViewById(R.id.radioFemale);
		buttonSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String keyword = editSearchKeyword.getText().toString();
				if (keyword.length() < 1 ) {
					editSearchKeyword.setError("plase enter name or phone number");
					return;
				}
				
				String gender = null ;
				if (radioButtonFemale.isChecked()) {
					gender = "f";
				}
				
				if (radioButtonMale.isChecked()) {
					gender = "m";
				}
				
				startActivity(new Intent(getBaseContext(), SearchFriendsActivity.class)
				.putExtra("keyword", keyword).putExtra("gender", gender));
			}
		});
		
		
		
	}
}
