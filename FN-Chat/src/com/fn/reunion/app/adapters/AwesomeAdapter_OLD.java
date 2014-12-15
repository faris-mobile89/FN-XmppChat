package com.fn.reunion.app.adapters;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fn.reunion.app.R;
import com.fn.reunion.app.model.MMessage;
import com.fn.reunion.app.utility.Utility;
/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Faris
 *
 */
public class AwesomeAdapter_OLD extends BaseAdapter{
	private Context mContext;
	private ArrayList<MMessage> mMessages;
	private String tag = "AwesomeAdapter";


	public AwesomeAdapter_OLD(Context context, ArrayList<MMessage> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}
	
	// Total number of types is the number of enum values
	@Override
	public int getViewTypeCount() {
		return MMessage.MessageType.values().length;
	}
	
	// Return an integer representing the type by fetching the enum type ordinal
	@Override
	public int getItemViewType(int position) {
		
		return getItem(position).type.ordinal();
		
		/*if (mMessages.get(position).getType().equals(MMessage.MessageType.TEXT)) {
			return 1;
		}
		else if (mMessages.get(position).getType().equals(MMessage.MessageType.TEXT)) {
			return 1;
		}
		return 1;*/
	}
	
	@Override
	public MMessage getItem(int position) {		
		return mMessages.get(position);
	}
	public void refresh(){
		
		this.notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MMessage message = (MMessage) this.getItem(position);

		ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
		
			   if (message.getType() == MMessage.MessageType.TEXT){
			         convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
			         holder.time = (TextView) convertView.findViewById(R.id.message_time);
			         holder.message = (EmojiconTextView) convertView.findViewById(R.id.message_text);
			         holder.msgContent = (LinearLayout)convertView.findViewById(R.id.message_content);
	          }else
	        	 if (message.getType() == MMessage.MessageType.IMAGE){
				      convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_row_image, parent, false);
				      holder.time = (TextView) convertView.findViewById(R.id.message_time);
				      holder.image = (ImageView) convertView.findViewById(R.id.image_msg);
				      holder.msgContent = (LinearLayout)convertView.findViewById(R.id.message_content);
			  }
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		LayoutParams lp = (LayoutParams) holder.msgContent.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			if (message.getType() == MMessage.MessageType.TEXT) {
				holder.message.setTextColor(mContext.getResources().getColor(R.color.textFieldColor));
				holder.message.setBackgroundDrawable(null);
			}
			lp.gravity = Gravity.LEFT;
			
		}
		else
		{
			//Check whether message is mine to show green background and align to right
			if(message.isMine())
			{
				  holder.msgContent.setBackgroundResource(R.drawable.bubble_green);
				  holder.time.setTextColor(mContext.getResources().getColor(R.color.textColor));
				  lp.gravity = Gravity.RIGHT;
				  
			   if (message.getType() == MMessage.MessageType.TEXT) {
				      holder.message.setTextColor(mContext.getResources().getColor(R.color.textColor));
				      holder.message.setText(message.getMessage());
				}else
				   if (message.getType() == MMessage.MessageType.IMAGE) {
					   //Log.d(tag,"adapter message image :"+ message.getMessage());
				    	boolean empty = message.getMessage().length() < 1 ?  true : false;
				    	if (!empty) {
				    		File imgFile = new File(message.getMessage());
				    		if(imgFile.exists()){
				    		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				    		    holder.image.setImageBitmap(myBitmap);
				    		}else{
				    			holder.image.setImageResource(R.drawable.logo_splash);
				    		}
						}
				}
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.msgContent.setBackgroundResource(R.drawable.bubble_yellow);
				holder.time.setTextColor(mContext.getResources().getColor(R.color.textFieldColor));
				lp.gravity = Gravity.LEFT;
				
				/**
				 *  IF MESSAGE IS TEXT
				 */
				if (message.getType() == MMessage.MessageType.TEXT){
/*TODO bug */       holder.message.setTextColor(mContext.getResources().getColor(R.color.textFieldColor));
					holder.message.setLayoutParams(lp);
					holder.message.setText(message.getMessage());
				/**
				 *  IF MESSAGE IS IMAGE
				 */
			    }else if (message.getType() == MMessage.MessageType.IMAGE) {
			    	//Log.d(tag,"adapter message image :"+ message.getMessage());
			    	boolean empty = message.getMessage().length() < 1 ?  true : false;
			    	if (!empty) {
			    		File imgFile = new File(message.getMessage());
			    		if(imgFile.exists()){
/*TODO bug */       	     Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    		    holder.image.setImageBitmap(myBitmap);
			    		}else{
			    			holder.image.setImageResource(R.drawable.logo_splash);
			    		}
					}
				}//end Image message
			}
			holder.time.setText(Utility.getCurrentTime());
		}
		return convertView;
	}
	
	private static class ViewHolder
	{
		EmojiconTextView message;
		TextView time;
		LinearLayout msgContent;
		ImageView image;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return 0;
	}

}
