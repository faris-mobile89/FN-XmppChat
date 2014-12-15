package com.fn.reunion.app.chat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fn.reunion.app.R;
import com.fn.reunion.app.xmpp.XMPPLogic;

public class ChatMainActivity extends Activity{
	Chat newChat;
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		
        setContentView(R.layout.app_main_activity);
		ChatManager chatmanager = XMPPLogic.getInstance().getConnection().getChatManager();
		newChat = chatmanager.createChat("emulator@android-pc", new MessageListener() {
		  // Receiving Messages
		  public void processMessage(Chat chat, Message message) {
		    Message outMsg = new Message(message.getBody());
		    try {
		      //Send Message object
		      newChat.sendMessage(outMsg);
		    } catch (XMPPException e) {
		      //Error
		    }
		  }
		});
		try {
		  //Send String as Message
		  newChat.sendMessage("How are you?");
		} catch (XMPPException e) {
		  //Error
		}
		
		// Add a packet listener to get messages sent to us
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		 XMPPLogic.getInstance().getConnection().addPacketListener(new PacketListener() {
		  public void processPacket(Packet packet) {
			  Log.i("ChatMainActivity", "new message");
		    Message message = (Message) packet;
		    String body = message.getBody();
		    String from = message.getFrom();
		    Toast.makeText(ChatMainActivity.this, body+"\n"+from, Toast.LENGTH_LONG).show();
		  }
		}, filter);
	}
	
	

}
