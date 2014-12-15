package com.fn.reunion.app.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.util.Log;

public class MessagePacketListener implements PacketListener {

    /**
     * Processes the incoming packet upon new arrival.
     *
     * @param packet
     */
	private XMPPConnection connection;
	private Context context;
	
	private String tag = MessagePacketListener.class.getSimpleName();
	
	
	public MessagePacketListener(Context mContext , XMPPConnection mConnection){
		this.connection = mConnection;
		this.context = mContext;
	}

    public void processPacket(Packet packet) {
        Message message = (Message) packet;
        String fromUserID = StringUtils.parseName(message.getFrom());
        String toUserID = StringUtils.parseName(connection.getUser());
        
        if (message.getBody() != null) {
        	Log.i(tag, message.getBody() +"from: "+fromUserID);
        	 new BuddiesController(context).notifyIncomingMessage(fromUserID,
        			 toUserID, message.getBody().toString());
        }
        
        return;
    }
}