package com.fn.reunion.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.fn.reunion.app.R;
import com.fn.reunion.app.model.SuggestedFriend;

public class SuggestedListAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<SuggestedFriend> suggestedFriends;

	public SuggestedListAdapter(Context context, ArrayList<SuggestedFriend> friends) {
		ctx = context;
		suggestedFriends = friends;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return suggestedFriends.size();
	}

	@Override
	public SuggestedFriend getItem(int position) {
		return suggestedFriends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public ArrayList<SuggestedFriend> getCheckedFriendBox() {
		ArrayList<SuggestedFriend> friend = new ArrayList<SuggestedFriend>();
		for (SuggestedFriend p : suggestedFriends) {
			if (p.isBox())
				friend.add(p);
		}
		return friend;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.suggested_item, parent, false);
		}

		SuggestedFriend p = getItem(position);

		((TextView) view.findViewById(R.id.sugName)).setText(p.getName());
		//((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);

		CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
		cbBuy.setOnCheckedChangeListener(myCheckChangList);
		cbBuy.setTag(position);
		cbBuy.setChecked(p.isBox());
		return view;
	}

	OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			getItem((Integer) buttonView.getTag()).setBox( isChecked );
		}
	};

	public void clear() {
		
	}
}