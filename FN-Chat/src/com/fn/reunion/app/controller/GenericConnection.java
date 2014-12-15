/* GenericConnection.java
*
* Programmed By:
*     Kevin Fahy
*     
* Change Log:
*     2009-June-27
*         First write. Created the interface so that it could be placed in
*         a field in Account Data. This interface would be implemented by
*         classes as a gateway to access XMPP, Twitter, and other
*         protocols. For example, an XMPPManager class would implement the
*         login() method, and would hold a copy of the XMPPConnection object
*         from the smack API. A TwitterManager class would do the same.
*         This interface will simplify the program flow, as we can execute
*         statements like, AccountData.disconnect() and it will work for any
*         protocol.
*         
* Known Issues:
*     none
*        
* Copyright (C) 2009  Pirate Captains
*
* License: GNU General Public License version 2.
* Full license can be found in ParrotIM/LICENSE.txt.
*/

package com.fn.reunion.app.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.jivesoftware.smack.XMPPException;

import com.fn.reunion.app.model.FriendTempData;

import android.widget.ImageView;

/**
* An interface the dictates the functions that connections can perform.
* Specifically, this interface defines a set of operations on connections that
* are common to all types of connections, be it XMPP, Twitter, ICQ, or others.
*/
public interface GenericConnection {

   public void login(String userID, String password) throws BadConnectionException;

   public void addFriend(String userID, String userNickname) throws BadConnectionException;

   public boolean removeFriend(String userID) throws BadConnectionException;

   public void changeStatus(UserStateType state, String status)
           throws BadConnectionException;

   public String retrieveStatus(String userID) throws BadConnectionException;

   public UserStateType retrieveState(String userID)
           throws BadConnectionException;

   public ArrayList<FriendTempData> retrieveFriendList()
           throws BadConnectionException;

   public void sendMessage(String toUserID, String message)
           throws BadConnectionException;

   /**
    *
    * changing typing state
    *
    * @param state
    *            represent different typing state
    * @throws BadConnectionException
    * @throws XMPPException
    */
   public void setTypingState(int state, String userID)
           throws BadConnectionException, XMPPException;

   public  byte[] getAvatarPicture(String userID) throws XMPPException;

   public void setAvatarPicture(byte[] bytes) throws XMPPException;

   public void setAvatarPicture(File file) throws XMPPException;

   public void setAvatarPicture(URL url) throws XMPPException;


   public int hashCode();

   //public void sendFile(String filePath, String userID,ProgressMonitorScreen progress) throws XMPPException;
   public void sendFile(File mFile, String userID) throws XMPPException;

   public boolean isValidUserID(String userID);

   public void createRoom(String room) throws XMPPException;

   public void inviteFriend(String userID, String roomName)
           throws XMPPException;

   public boolean isConferenceChat();

   public void sendMultMessage(String message, String roomName)
           throws BadConnectionException;

   // For Twitter
   public boolean doesExist(String userID);

   public boolean isFollowing(String userID);
   
   public boolean isConnected();
   


}