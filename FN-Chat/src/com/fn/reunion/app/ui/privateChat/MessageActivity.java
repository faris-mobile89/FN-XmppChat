package com.fn.reunion.app.ui.privateChat;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fn.reunion.app.R;
import com.fn.reunion.app.adapters.AwesomeAdapter;
import com.fn.reunion.app.model.MMessage;
import com.fn.reunion.app.ui.custom.InsertionListView;
import com.fn.reunion.app.ui.custom.OnRowAdditionAnimationListener;
import com.fn.reunion.app.utility.FNSerializableManager;
import com.fn.reunion.app.utility.Utility;
import com.fn.reunion.app.xmpp.NotificationService;
import com.fn.reunion.app.xmpp.XmppManager;

/**
 * MessageActivity is a main Activity to show a ListView containing Message items
 * 
 * @author Faris.it.cs@gmail.com
 *
 */

public class MessageActivity extends Activity implements OnRowAdditionAnimationListener{

	private static ArrayList<MMessage> messages;
	private static AwesomeAdapter adapter;
	private EmojiconEditText emojiconEditText;
	private Button emojoBtn;
	private static Random rand = new Random();	
	private static String sender;
	private static String reciever;
	private String nickName;
	private Chat newChat;
	private PacketListener packetListener;
 	private static InsertionListView listView;
 	private EmojiconsPopup popup;
 	private XmppManager xmppManager;
 	private String filePath;
    public static boolean isComplete;
    private int isShowingEmoji = 0;
    private static final int FILE_SELECT_CODE = 100;
    private static final int IMAGE_SELECT_CODE = 101;
    private static String TAG = "MessageActivity";
    
	public static void notifyFileRecieved(String senderID , String FilePath){
		
		Log.d(TAG, "notifyFileRecieved() called");
		Log.d(TAG, "Sender ID : "+senderID);
		Log.d(TAG, "File Path : "+FilePath);
		String sender = StringUtils.parseName(senderID);
		
	    try{
	      if (!reciever.equals(sender)) {
		       Log.w(TAG, "disable message from "+sender);
			   return;
		  }
	      addNewMessage(new MMessage(FilePath, false ,MMessage.MessageType.IMAGE));
	    }catch(Exception e){
	    	Log.e(TAG, "error disable caused by "+e.getMessage());
	    }
	    
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    xmppManager = NotificationService .getInstance().getXmppManager();
		messages = new ArrayList<MMessage>();
		reciever = StringUtils.parseName(getIntent().getExtras().getString("username"));
		nickName = getIntent().getExtras().getString("nickName");
		
	    Log.d(TAG, "Reading object ...");
	    ArrayList<MMessage> mm =  FNSerializableManager.loadSerializedMessages(getBaseContext(), reciever);
	    
	    if (mm!=null ) {
	    	FNSerializableManager.deleteChatFile(reciever);
			messages = mm;
			Log.d(TAG, mm.size()+" object fetched");
		}
	    
		setContentView(R.layout.main_chat);
		initEmojiKeyboard();
		emojoBtn = (Button)findViewById(R.id.emojoBtn);
		emojoBtn.setEnabled(false);
		
		RelativeLayout mLayout = (RelativeLayout)findViewById(R.id.relative_layout);
		
		listView = (InsertionListView)findViewById(R.id.listView1);
		
		//sender = "chat with "+nickName;
		//sender += reciever;
		Log.d(TAG,"chat session opend with user id:"+ reciever);
		//this.setTitle(sender.replace("@"+xmppManager.getConnection().getHost(), ""));
		this.setTitle(nickName);
		adapter = new AwesomeAdapter(this, messages);
		listView.setAdapter(adapter);
		listView.setData(messages);
		listView.setLayout(mLayout);
		listView.setRowAdditionAnimationListener(this);
		listView.setSelection(messages.size()-1);
		initChat();
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed");
		
		if (isShowingEmoji == 1) {
			 popup.dismiss();
			 isShowingEmoji = 0;
			 Log.d(TAG, "onBackPressed dismiss"); 
			 return;
		}
		super.onBackPressed();
	}
	
	private void initEmojiKeyboard() {
		
        emojiconEditText = (EmojiconEditText) this.findViewById(R.id.emojiconEditText);
		// Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
		 popup = new EmojiconsPopup(getWindow().getDecorView().getRootView(), this);

		//Will automatically set size according to the soft keyboard size        
		popup.setSizeForSoftKeyboard();

		//Set on emojicon click listener
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

		            @Override
		            public void onEmojiconClicked(Emojicon emojicon) {
		                emojiconEditText.append(emojicon.getEmoji());
		            }
		        });

		//Set on backspace click listener
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

		    @Override
		    public void onEmojiconBackspaceClicked(View v) {
		        KeyEvent event = new KeyEvent(
		                 0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
		                 emojiconEditText.dispatchKeyEvent(event);
		    }
		});

		//Set listener for keyboard open/close
		popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

		            @Override
		            public void onKeyboardOpen(int keyBoardHeight) {
		            	Log.d(TAG, "onKeyboardOpen");
		            	emojoBtn.setEnabled(true);
		            	
		                if(!popup.isShowing()){
		                   // popup.showAtBottom();
		                	//popup.dismiss();
		                }
		            }

		            @Override
		            public void onKeyboardClose() {
		            	Log.d(TAG, "onKeyboardClose");
		            	emojoBtn.setEnabled(false);
		            	
		            	if (isShowingEmoji == 1) {
		       			 popup.dismiss();
		       			 isShowingEmoji = 0;
		       			 Log.d(TAG, "onBackPressed dismiss"); 
		       		}
		                if(popup.isShowing());
		                   // popup.dismiss();
		            }
		        });
	}
    
    public void showEmoji(View v){
    	
    	switch (isShowingEmoji) {
		case 0:
			 popup.showAtBottom();
			 isShowingEmoji = 1;
			break;
			case 1:
				 popup.dismiss();
				 isShowingEmoji = 0;
    	}
    }
    
	private void initChat() {
		
		ChatManager chatmanager = xmppManager.getConnection().getChatManager();
		
		newChat = chatmanager.createChat(reciever+"@"+xmppManager.getConnection().getHost(), new MessageListener() {
		  public void processMessage(Chat chat, Message message) {
			  Log.i(TAG, "1 new message");
		  }
		});
		
		//Add a packet listener to get messages sent to us
	    PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		packetListener = new PacketListener() {
			
			  public void processPacket(Packet packet) {
					Message message = (Message) packet;
				    String body = message.getBody();
				    String from = message.getFrom();
				    Log.i(TAG, "new message from "+StringUtils.parseName(from));
				    try{
				      if (!reciever.equals(StringUtils.parseName(from))) {
					       Log.w(TAG, "disable message from "+StringUtils.parseName(from));
						   return;
					  }
				    }catch(Exception e){
				    	Log.e(TAG, "error disable caused by "+e.getMessage());
				    }
				      messages.add(new MMessage(body, false,MMessage.MessageType.TEXT));
					  Handler handler = new Handler(Looper.getMainLooper());
					  handler.post(new Runnable() {
					     @Override
					     public void run() {
					 		adapter.refresh();
					 		listView.setSelection(messages.size()-1);
					     }
					  });
		          }
		};
		
		xmppManager.getConnection().addPacketListener(packetListener, filter);
		
	}

	// button listener 
	public void sendMessage(View v)
	{
		String newMessage = emojiconEditText.getText().toString().trim(); 
		if(newMessage.length() > 0)
		{
			emojiconEditText.setText("");
			
			addNewMessage(new MMessage(newMessage, true,MMessage.MessageType.TEXT));
			
			try {
				newChat.sendMessage(newMessage);
				
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}
	
    public void getImage(View v){
    	 Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	 startActivityForResult(i, RESULT_OK);
     }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

/*
 *  http://stackoverflow.com/questions/12153795/file-transfer-using-xmpp-extension-xep-0065
 */
	    if (resultCode == RESULT_OK && resultCode  == IMAGE_SELECT_CODE) {
         Log.i(TAG, "resultCode");
	     Uri selectedImageUri = data.getData();
	     filePath = Utility.getRealPathFromURI(getBaseContext(),selectedImageUri);
	     Log.i(TAG, "path file "+filePath);
	     Handler handler = new Handler();
	     handler.postDelayed(new SendFile(), 500);
	   }else if(resultCode == RESULT_OK && resultCode  == FILE_SELECT_CODE){
		   Toast.makeText(getBaseContext(), "bh", Toast.LENGTH_SHORT).show();
	   }
	}
	
	private class SendFile implements Runnable {

		@Override
		public void run() {
			
			  Log.d(TAG,"SendFile.run()...");
			
			   File  imageFile = new File(filePath);
			   
			    if (imageFile.exists()) {
					Log.i(TAG,"file exist");
					addNewMessage(new MMessage(filePath, true,MMessage.MessageType.IMAGE));
					try {
						//xmppManager.sendFile(imageFile,reciever+"/Smack");
						xmppManager.sendFile(imageFile,
								reciever+"@"+xmppManager.getConnection().getHost()+"/Spark 2.6.3");
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}else{
					Log.e(TAG,"file does not exist");
					return;
			}
		}
	}
	
	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(
	                Intent.createChooser(intent, "Select a File to Send"),FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
	        Toast.makeText(this, "Please install a File Manager.", 
	                Toast.LENGTH_SHORT).show();
	    }
	}

	public static void addNewMessage(MMessage m)
	{
		messages.add(m);
		adapter.refresh();
	    listView.setSelection(messages.size()-1);
	    
	   //listView.addRow(m);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		FNSerializableManager.wirteObject(getBaseContext(), messages, reciever);
	    Log.i(TAG, "Writing object ...");
	    
		if (packetListener != null  && xmppManager.getConnection() != null) {
    		xmppManager.getConnection().removePacketListener(packetListener);
    		Log.d(TAG, "packetListener removed !");
		}
	}
    
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (packetListener != null  && xmppManager.getConnection() != null) {
    		xmppManager.getConnection().removePacketListener(packetListener);
    		Log.d(TAG, "packetListener removed !");
    	}
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    
    	new MenuInflater(this).inflate(R.menu.menu_chat, menu);
       return (super.onCreateOptionsMenu(menu));
 }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.action_attach_image) {
    	 Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     	 startActivityForResult(i, IMAGE_SELECT_CODE);
    }
    else if(item.getItemId() == R.id.action_attach_file){
    	showFileChooser();
    }
    else if(item.getItemId() == R.id.clearChat){
    	deleteFile(reciever);
    	messages.clear();
    	adapter.refresh();
    }
    
    return (super.onOptionsItemSelected(item));
}

	@Override
	public void onRowAdditionAnimationStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRowAdditionAnimationEnd() {
		// TODO Auto-generated method stub
		
	}   


}
