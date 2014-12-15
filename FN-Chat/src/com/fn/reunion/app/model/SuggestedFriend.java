package com.fn.reunion.app.model;

import java.io.Serializable;
import java.util.ArrayList;


public class SuggestedFriend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -694628091539039118L;
	private String name;
	private String jabberId;
	private boolean box;
	private ArrayList<SuggestedFriend> items;

	public SuggestedFriend(String name, String jabberId) {
		super();
		this.name = name;
		this.jabberId = jabberId;
		this.box = false;
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
	
	public boolean isBox() {
		return box;
	}

	public void setBox(boolean box) {
		this.box = box;
	}

	@Override
	public String toString() {
		return "SuggestedFriend [name=" + name + ", jabberId=" + jabberId + "]";
	}
	

}