package com.fn.reunion.app.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.ChatStateManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.fn.reunion.app.model.FriendTempData;
import com.fn.reunion.app.model.TypingStateType;

public class JabberManager implements GenericConnection {

    private static final int DEFAULT_PORT = 5222;

	private static final String HOST = "192.168.43.58";

	private static final String SERVICE = "conference";

    private XMPPConnection connection;

    private Context controller;

    private GenericConnection genericConnection;

    private ArrayList<Chat> chats;

    private String server;

    private Chat lastChat;

    private String domain;

    private VCard vcard;
    
    private String tag = "JabberManager";

    public JabberManager(Context controller) {
    	
        this.connection = null;
        this.controller = controller;
        this.genericConnection = this;
        this.chats = new ArrayList<Chat>();
    	ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
    	vcard = new VCard();
    	
    	if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
    }

    public void addFriend(String userID) throws BadConnectionException {
    	
        Roster roster = null;
        String nickname = null;

        nickname = StringUtils.parseBareAddress(userID);

        roster = connection.getRoster();
        if (!roster.contains(userID)) {
            try {
                roster.createEntry(userID, nickname, null);
            } catch (XMPPException e) {
                throw new BadConnectionException();
            }
        }

        return;
    }

    public void disconnect() {
        connection.disconnect();
        return;
    }

    public void login(String userID, String password) throws BadConnectionException {
    	
    	// Create a connection
		ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, DEFAULT_PORT, SERVICE);
		this.connection = new XMPPConnection(connConfig);

		try {
			this.connection.connect();
			Log.i("XMPPChatDemoActivity",
					"Connected to " + connection.getHost());
		} catch (XMPPException ex) {
			Log.e("XMPPChatDemoActivity", "Failed to connect to "+ connection.getHost());
			Log.e("XMPPChatDemoActivity", ex.toString());
		}
		
		try {
			this.connection.login(userID, password);
			Log.i("XMPPChatDemoActivity","Logged in as " + connection.getUser());
		} catch (XMPPException e1) {
			e1.printStackTrace();
            Log.d("Error", "Error signing into Jabber!\nUser name and password do not match." );
		}
        // Setup the listeners for messages and buddy changes
        connection.addPacketListener(new MessagePacketListener(),new MessagePacketFilter());
        connection.getRoster().addRosterListener(new BuddyListener());

        return;
    }

    public boolean removeFriend(String userID) throws BadConnectionException {
        boolean removed = false; // Default return value
        Roster roster = this.connection.getRoster();

        for (RosterEntry r : roster.getEntries()) {
            if (r.getUser().equalsIgnoreCase(userID)) {
                try {
                    roster.removeEntry(r);
                } catch (XMPPException e) {
                    throw new BadConnectionException();
                }
                removed = true;
                break;
            }
        }

        return removed;
    }

    public void changeStatus(UserStateType state, String status) {
        Presence presence = new Presence(Presence.Type.available);
        if (state == UserStateType.ONLINE) {
            presence.setMode(Presence.Mode.available);
        } else if (state == UserStateType.AWAY) {
            presence.setMode(Presence.Mode.away);
        } else if (state == UserStateType.BUSY) {
            presence.setMode(Presence.Mode.dnd);
        } else {
            presence.setMode(Presence.Mode.chat);
        }
        presence.setStatus(status);
        connection.sendPacket(presence);

        return;
    }

    public String retrieveStatus(String userID) {
        String userStatus = ""; // default return value

        try {
            userStatus = this.connection.getRoster().getPresence(userID).getStatus();
        } catch (NullPointerException e) {
         //Invalid connection or user status
            userStatus = "";
        }
        // Server may set their status to null; we want empty string
        if (userStatus == null) {
            userStatus = "";
        }

        return userStatus;
    }
    
    public VCard getUserVCard( String userId ){
    	
    	 VCard vCard = new VCard();
    	 try {
			vCard.load(connection, userId);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
    	 return vCard;
    }
    
    public VCard getVcard(){
    	 
	    try {
	        vcard.load(connection);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	     return vcard; 
     }

     /**
      * Save user information.
     * @throws XMPPException 
      */
     public void setUserInfo (String firstName,String lastName){
    	 
    	 try {
    	     vcard.load(connection);
		} catch (XMPPException e1) {
			Log.e(tag, e1.getMessage());
		}
    	 
    	 vcard.setFirstName(firstName);
    	 vcard.setLastName(lastName);
    	// vcard.setAddressFieldHome("STREET", "Some street");
    	// vcard.setAddressFieldWork("CTRY", "US");
    	 //vcard.setPhoneWork("FAX", "3443233");
    	 
    	 try {
    		 Log.d(tag, "saving vCard...");
			vcard.save(connection);
		} catch (XMPPException e) {
			Log.e(tag, e.getMessage());
		}
    	 

     }
     
     
     /**
      * Set the avatar for the VCard by specifying the url to the image.
      *
      * @param avatarURL
      *            the url to the image(png,jpeg,gif,bmp)
      * @throws XMPPException
      */
     public void setAvatarPicture(URL avatarURL) throws XMPPException {
        
     	byte[] bytes = new byte[0];
         try {
             bytes = getBytes(avatarURL);
         } catch (IOException e) {
             e.printStackTrace();
         }

         setAvatarPicture(bytes);
     }
    /**
     * Specify the bytes for the avatar to use.
     *
     * @param bytes
     *            the bytes of the avatar.
     * @throws XMPPException
     */
    public void setAvatarPicture(File file) throws XMPPException {
        vcard = new VCard();
        vcard.load(connection);

        // Otherwise, add to mappings.
        byte[] bytes;
        try {
            bytes = getFileBytes(file);
            String encodedImage = StringUtils.encodeBase64(bytes);
            vcard.setAvatar(bytes, encodedImage);
            vcard.setEncodedImage(encodedImage);
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
                    + encodedImage + "</BINVAL>", true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        vcard.save(connection);
    }

    /**
     * Specify the bytes for the avatar to use.
     *
     * @param bytes
     *            the bytes of the avatar.
     * @throws XMPPException
     */
    public void setAvatarPicture(byte[] bytes) throws XMPPException {
    	
        vcard.load(connection);
        String encodedImage = StringUtils.encodeBase64(bytes);
        vcard.setField("Avatar", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
        vcard.save(connection);
    }

    /**
     * Common code for getting the bytes of a url.
     *
     * @param url
     *            the url to read.
     */
    public byte[] getBytes(URL url) throws IOException {
        final String path = url.getPath();
        final File file = new File(path);
        if (file.exists()) {
            return getFileBytes(file);
        }

        return null;
    }

    private byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int) file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if (readBytes != buffer.length) {
                throw new IOException("Entire file not read");
            }
            return buffer;
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    public byte[] getAvatarPicture(String userID) throws XMPPException {
    	    	
         byte[] avatarBytes;
            vcard.load(connection, userID); // load someone's VCard
           // Log.i("VCard", vcard.toXML().toString());
        	avatarBytes  = vcard.getAvatar();
          
        return avatarBytes;
    }

    public UserStateType retrieveState(String userID) {
    	
        UserStateType userState = UserStateType.OFFLINE; // default return value
        Presence userFromServer = null;
        Mode userStateFromServer = null;
        
        try {
            userFromServer = this.connection.getRoster().getPresence(userID);
            userStateFromServer = userFromServer.getMode();

            if (userStateFromServer == Presence.Mode.dnd) {
                userState = UserStateType.BUSY;
            } else if (userStateFromServer == Presence.Mode.away
                    || userStateFromServer == Presence.Mode.xa) {
                userState = UserStateType.AWAY;
            } else if (userFromServer.isAvailable()) {
                userState = UserStateType.ONLINE;
            } else { // user is offline
                userState = UserStateType.OFFLINE;
            }
        } catch (NullPointerException e) {
            // Invalid connection or user in the retrieve state
            userState = UserStateType.OFFLINE;
        }

        return userState;
    }

    public ArrayList<FriendTempData> retrieveFriendList() {
    	
        ArrayList<FriendTempData> friends = new ArrayList<FriendTempData>();
        FriendTempData friendToAdd = null;
        String userID = null;
        Roster roster = null;

        roster = this.connection.getRoster();

        for (RosterEntry r : roster.getEntries()) {
            userID = r.getUser();
            friendToAdd =
                    new FriendTempData(userID, r.getName(), this
                            .retrieveStatus(userID),
                            this.retrieveState(userID), false);
            friends.add(friendToAdd);
        }
        return friends;
    }

    public void sendMessage(String toUserID, String message)
    
            throws BadConnectionException {
        Chat ourChat = null;

        for (Chat c : this.chats) {
            if (c.getParticipant().equalsIgnoreCase(toUserID)) {
                ourChat = c;
                break;
            }
        }

        if (ourChat == null) {
            ourChat =
                    connection.getChatManager().createChat(toUserID,
                            new MessageListener() {
                                public void processMessage(Chat chat,
                                        Message message) {
                                    // Do nothing

                                    return;
                                }
                            });
        }

        try {
            ourChat.sendMessage(message);
        } catch (XMPPException e) {
            // Error in sending the message
            throw new BadConnectionException();
        }

        return;
    }


    // Section
    // Listeners

    private class MessagePacketFilter implements PacketFilter {
        public boolean accept(Packet packet) {
            // TODO Is this the source of the name is null bug? check
            // if we are receiving packets that aren't messages that we need
            // to deal with.
            return (packet instanceof Message);
        }
    }

    /**
     * Changes to the roster, that is, changes to friends' statuses or
     * availability, are handled by this class.
     */
    
    private class BuddyListener implements RosterListener {

        /**
         * Displays which user is added to the entry.
         *
         * @param addresses
         */
        public void entriesAdded(Collection<String> addresses) {
            // Fix me!
            return;
        }

        /**
         * Displays which user is updated in the entry.
         *
         * @param addresses
         */
        public void entriesUpdated(Collection<String> addresses) {
            // Fix me!
            return;
        }

        /**
         * Displays which user is deleted in the entry.
         *
         * @param addresses
         */
        public void entriesDeleted(Collection<String> addresses) {
            // Fix me!
            return;
        }

        /**
         * Updates user's changed presence
         *
         * @param presence
         */
        public void presenceChanged(Presence presence) {
            String bareAddress =  StringUtils.parseBareAddress(presence.getFrom());
           // controller.friendUpdated(genericConnection, bareAddress);
            return;
        }
    }

    private class MessagePacketListener implements PacketListener {

        /**
         * Processes the incoming packet upon new arrival.
         *
         * @param packet
         */

        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            String fromUserID = StringUtils.parseBareAddress(message.getFrom());
            String toUserID =
                    StringUtils.parseBareAddress(connection.getUser());

            if (message.getBody() != null) {
              //  controller.messageReceived(fromUserID, toUserID, message.getBody());
            }

            return;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = hash * 31 + "Google".hashCode();
        hash = hash * 31 + this.connection.hashCode();

        return hash;
    }

    /**
     *
     * set typing state
     *
     * @param state
     *            int that represents different state 1 = active 2 = composing 3
     *            = gone 4 = inactive 5 = paused
     */
    public void setTypingState(int state, String userID)
            throws BadConnectionException, XMPPException {
        ChatStateManager curState = ChatStateManager.getInstance(connection);
        if (lastChat == null) {
            lastChat =
                    connection.getChatManager().createChat(userID,
                            new DefaultChatStateListener());
        }
        
        if (state == 1) {
            curState.setCurrentState(ChatState.active, lastChat);
        } else if (state == 2) {
            curState.setCurrentState(ChatState.composing, lastChat);

        } else if (state == 3) {
            curState.setCurrentState(ChatState.gone, lastChat);
        } else if (state == 4) {
            curState.setCurrentState(ChatState.inactive, lastChat);
        } else if (state == 5) {
            curState.setCurrentState(ChatState.paused, lastChat);
        }
    }

    private class DefaultChatStateListener implements ChatStateListener {
        public void stateChanged(Chat user, ChatState event) {
            String state = event.name();
            TypingStateType typingState = null;
            if (state.equals("active")) {
                typingState = TypingStateType.ACTIVE;
            } else if (state.equals("composing")) {
                typingState = TypingStateType.TYPING;
            } else if (state.equals("paused")) {
                typingState = TypingStateType.PAUSED;
            } else if (state.equals("inactive")) {
                typingState = TypingStateType.INACTIVE;
            } else if (state.equals("gone")) {
                typingState = TypingStateType.GONE;
            }
           // controller.typingStateUpdated(genericConnection, typingState, user.getParticipant().toString());
        }

        public void processMessage(Chat arg0, Message arg1) {
            // Do nothing
        }
    }
    
    public void SearchUser() throws XMPPException{
    	
    	 UserSearchManager search = new UserSearchManager(connection);
         Form searchForm = search.getSearchForm("search." + connection.getServiceName());

         Form answerForm = searchForm.createAnswerForm();
         answerForm.setAnswer("search", "rahul rawat");
         answerForm.setAnswer("Username", true);

         ReportedData data = search.getSearchResults(answerForm, "search." + connection.getServiceName());

         System.out.println("\nThe jids from our each of our hits:");

         Iterator<Row> rows = data.getRows();
         while (rows.hasNext()) 
         {
            Row row = rows.next();

            Iterator<String> jids = row.getValues("jid");
            while (jids.hasNext()) {
               System.out.println(jids.next());
            }
         }
    }
    
    public void sampleSearch() throws XMPPException{
    	
        UserSearchManager search = new UserSearchManager(connection);
        Form searchForm = search.getSearchForm("search." + domain);
        
        System.out.println("Available search fields:");
        Iterator<FormField> fields = searchForm.getFields();
        
        while (fields.hasNext()) {
           FormField field = fields.next();
           System.out.println(field.getVariable() + " : " + field.getType());
        }
        
        Form answerForm = searchForm.createAnswerForm();
        answerForm.setAnswer("search", "a");
        answerForm.setAnswer("Email", true);
        
        ReportedData data = search.getSearchResults(answerForm, "search." + domain);
        
        System.out.println("\nColumns that are included as part of the search results:");
        Iterator<Column> columns = data.getColumns();
        while (columns.hasNext()) {
           System.out.println(columns.next().getVariable());
        }
        
        System.out.println("\nThe jids from our each of our hits:");
        Iterator<Row> rows = data.getRows();
        while (rows.hasNext()) {
           Row row = rows.next();
           
           Iterator<String> jids = row.getValues("jid");
           while (jids.hasNext()) {
              System.out.println(jids.next());
           }
        }
   }

    public String getUserEmailHome() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserEmailWork() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserFirstName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserLastName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserMiddleName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserNickName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserOrganization() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserOrganizationUnit() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserPhoneHome() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserPhoneWork() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public void load(String userID) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void load() throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserEmailHome(String email) throws XMPPException {
    	vcard.load(connection);
    	vcard.setEmailHome(email);
    	vcard.save(connection);
    	

    }

    public void setUserEmailWork(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserFirstName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setFirstName(name);
    	vcard.save(connection);

    }

    public void setUserLastName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setLastName(name);
    	vcard.save(connection);

    }

    public void setUserMiddleName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setMiddleName(name);
    	vcard.save(connection);

    }

    public void setUserNickName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setNickName(name);
    	vcard.save(connection);
    }

    public void setUserOrganization(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserOrganizationUnit(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserPhoneHome(String phone) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField("Home", phone);
    	vcard.save(connection);

    }

    public void setUserPhoneWork(String phone) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField("Work", phone);
    	vcard.save(connection);

    }
    public void setField(String name , String data) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField(name, data);
    	vcard.save(connection);

    }

    public void sendFile(File filePath, String userID) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public boolean isValidUserID(String userID) {
        // TODO Auto-generated method stub
        return false;
    }

    public void createRoom(String room) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void inviteFriend(String userID, String roomName)
            throws XMPPException {
        // TODO Auto-generated method stub

    }

    public boolean isConferenceChat() {
        return false;
    }

    public void sendMultMessage(String message, String roomName)
            throws BadConnectionException {
        // TODO Auto-generated method stub

    }

    public boolean doesExist(String userID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFollowing(String userID) {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isConnected(){
    	return connection.isConnected();
    }

	@Override
	public void addFriend(String userID, String userNickname)
			throws BadConnectionException {
		// TODO Auto-generated method stub
		
	}
    
}