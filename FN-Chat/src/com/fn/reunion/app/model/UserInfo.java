package com.fn.reunion.app.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fn.reunion.app.Constants;
public class UserInfo implements Serializable{

	private static final long serialVersionUID = 46543445;
	private String fname, sname, lname, email, phone;
	private boolean isConfirmed;
	private static String tag = UserInfo.class.getSimpleName(); 
	
	public static UserInfo userFromJSON(JSONObject jsonObject){
		UserInfo user = new UserInfo();
         try {
			user.setEmail(jsonObject.getString(Constants.KEY_EMAIL));
		} catch (JSONException e) {
			Log.e(tag, e.getMessage());
		}
		
		return user;
	}

	public UserInfo(String fname, String sname, String lname, String email,String phone) {
		this.fname = fname;
		this.sname = sname;
		this.lname = lname;
		this.email = email;
		this.phone = phone;
	}

	public UserInfo() {
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public boolean isConfirmed() {
		return isConfirmed;
	}

	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	@Override
	public String toString() {
		return "UserInfo [fname=" + fname + ", sname=" + sname + ", lname="
				+ lname + ", email=" + email + ", phone=" + phone
				+ ", isConfirmd=" + isConfirmed + "]";
	}


}
