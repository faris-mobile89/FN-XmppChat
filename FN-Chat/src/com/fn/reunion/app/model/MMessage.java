package com.fn.reunion.app.model;

import java.io.Serializable;

/**
 * Message is a Custom Object to encapsulate message information/fields
 * 
 * @author Adil Soomro
 *
 */
public class MMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5707316482021977680L;
	/**
	 * The content of the message
	 */
	public String message;
	/**
	 * boolean to determine, who is sender of this message
	 */
	public boolean isMine;
	/**
	 * boolean to determine, whether the message is a status message or not.
	 * it reflects the changes/updates about the sender is writing, have entered text etc
	 */
	public boolean isStatusMessage;
	
	public MessageType type;
	
	public static enum MessageType {
	    TEXT,IMAGE
	}
	
	
	/**
	 * Constructor to make a Message object
	 */
	public MMessage(String message, boolean isMine , MessageType type) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
		this.type  = type;
	}
	
	/**
	 * Constructor to make a status Message object
	 * consider the parameters are swaped from default Message constructor,
	 *  not a good approach but have to go with it.
	 */
	public MMessage(boolean status, String message) {
		super();
		this.message = message;
		this.isMine = false;
		this.isStatusMessage = status;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public boolean isStatusMessage() {
		return isStatusMessage;
	}
	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	
}
