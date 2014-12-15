package com.fn.reunion.app.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class SuggestedFriend_parcelable implements Parcelable {

	private String name;
	private String jabberId;
	private ArrayList<SuggestedFriend_parcelable> items;

	public SuggestedFriend_parcelable(String name, String jabberId) {
		super();
		this.name = name;
		this.jabberId = jabberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJabberId() {
		return jabberId;
	}

	public void setJabberId(String jabberId) {
		this.jabberId = jabberId;
	}

	@Override
	public String toString() {
		return "SuggestedFriend [name=" + name + ", jabberId=" + jabberId + "]";
	}
	
	 public ArrayList<SuggestedFriend_parcelable> getItems() {
		return items;
		}
	
	
    protected SuggestedFriend_parcelable(Parcel in) {
        name = in.readString();
        jabberId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(jabberId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SuggestedFriend_parcelable> CREATOR = new Parcelable.Creator<SuggestedFriend_parcelable>() {
        @Override
        public SuggestedFriend_parcelable createFromParcel(Parcel in) {
            return new SuggestedFriend_parcelable(in);
        }

        @Override
        public SuggestedFriend_parcelable[] newArray(int size) {
            return new SuggestedFriend_parcelable[size];
        }
    };
}