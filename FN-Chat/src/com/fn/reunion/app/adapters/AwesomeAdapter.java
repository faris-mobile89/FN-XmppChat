/**
 * 
 * AwesomeAdapter is a Custom class to implement custom Heterogenous ListView
 * 
 * @author Faris
 *
 */

package com.fn.reunion.app.adapters;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fn.reunion.app.R;
import com.fn.reunion.app.model.MMessage;
import com.fn.reunion.app.model.MMessage.MessageType;
import com.fn.reunion.app.utility.Utility;


public class AwesomeAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<MMessage> mMessages;
	private static final String TAG = "AwesomeAdapter";
	private int lastPosition = -1;
	
	HashMap<MMessage, Integer> mIdMap = new HashMap<MMessage, Integer>();
	int mLayoutViewResourceId;
    int mCounter;


	public AwesomeAdapter(Context context, ArrayList<MMessage> messages) {
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
	}
	
	@Override
	public MMessage getItem(int position) {		
		return mMessages.get(position);
	}
	
	public void refresh(){
		
		this.notifyDataSetChanged();
	}
	
	  public void updateStableIds() {
		  mIdMap.clear();
	        mCounter = 0;
	        for (int i = 0; i < mMessages.size(); ++i) {
	            mIdMap.put(mMessages.get(i), mCounter++);
	        }
	    }

	    public void addStableIdForDataAtPosition(int position) {
	        mIdMap.put(mMessages.get(position), ++mCounter);
	    }

	    @Override
	    public boolean hasStableIds() {
	        return true;
	    }
	    
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		 MMessage message = (MMessage) this.getItem(position);
		 final ViewHolder holder; 

		if(convertView == null)
		{
			    int type = getItemViewType(position);
			     convertView = getInflatedLayoutForType(type , parent);
			     holder = new ViewHolder();
		
			   if (message.getType() == MMessage.MessageType.TEXT){
			         holder.message = (EmojiconTextView) convertView.findViewById(R.id.message_text);
			         holder.msgContent = (LinearLayout)convertView.findViewById(R.id.message_content);
	          }else if (message.getType() == MMessage.MessageType.IMAGE){
				      holder.image = (ImageView) convertView.findViewById(R.id.image_msg);
				      holder.msgContent = (LinearLayout)convertView.findViewById(R.id.message_content);
				      holder.image.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Utility.showImage(mContext,holder.image);
						}
					});
			  }
			   
			   holder.time = (TextView) convertView.findViewById(R.id.message_time);
			   holder.userThumb = (ImageView) convertView.findViewById(R.id.chat_thumb);
			   
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		    LayoutParams lp = (LayoutParams) holder.msgContent.getLayoutParams();
		    
		    /**
		     * INCOMING MESSAGE
		     */
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
			/**
		     * INCOMING MESSAGE
		     */
			if(message.isMine())
			{
				  holder.msgContent.setBackgroundResource(R.drawable.bubble_green);
				  holder.time.setTextColor(mContext.getResources().getColor(R.color.textColor));
				  lp.gravity = Gravity.RIGHT;
				  //lp.setMargins(3, 0, 30, 0);

				  
				  LayoutParams params =  (LayoutParams) holder.userThumb.getLayoutParams();
				  params.weight = 1.0f;
				  params.gravity = Gravity.RIGHT;
				  params.setMargins(3, 0, 30, 0);
				  holder.userThumb.setLayoutParams(params);
				  holder.userThumb.setVisibility(View.GONE);
				  
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
			/**
		     * OUTCOMING MESSAGE
		     */
			else
			{
				holder.msgContent.setBackgroundResource(R.drawable.bubble_yellow);
				holder.time.setTextColor(mContext.getResources().getColor(R.color.grayTitle));
				lp.gravity = Gravity.LEFT;
				
				LayoutParams params =  (LayoutParams) holder.userThumb.getLayoutParams();
				params.weight = 1.0f;
				params.gravity = Gravity.LEFT;
				holder.userThumb.setLayoutParams(params);
				holder.userThumb.setVisibility(View.VISIBLE);
				
				/**
				 *  IF MESSAGE IS TEXT
				 */
				if (message.getType() == MMessage.MessageType.TEXT){
                    holder.message.setTextColor(mContext.getResources().getColor(R.color.grayTitle));
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
                    	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    		    holder.image.setImageBitmap(myBitmap);
			    		}else{
			    			holder.image.setImageResource(R.drawable.logo_splash);
			    		}
					}
				}//end Image message
			}
			holder.time.setText(Utility.getCurrentTime());
		}
		
	   if(convertView != null) {
		  Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.down_from_top : R.anim.down_from_top);
		  convertView.startAnimation(animation);
		}
	   lastPosition = position;
	
		return convertView;
	}
	 // Given the item type, responsible for returning the correct inflated XML layout file
		private View getInflatedLayoutForType(int type, ViewGroup parent ) {
			if (type == MessageType.TEXT.ordinal()) {
				return LayoutInflater.from(mContext).inflate(R.layout.sms_row, null);
			} else if (type == MessageType.IMAGE.ordinal()) {
				return LayoutInflater.from(mContext).inflate(R.layout.msg_row_image, null);
			} else {
				return null;
			}
		}
	private static class ViewHolder
	{
		EmojiconTextView message;
		TextView time;
		LinearLayout msgContent;
		ImageView image,userThumb;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return 0;
	}
	
    /**
     * Returns a circular cropped version of the bitmap passed in.
     */
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;

        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	

}
