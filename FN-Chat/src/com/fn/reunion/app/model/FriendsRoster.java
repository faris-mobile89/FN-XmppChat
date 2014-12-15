package com.fn.reunion.app.model;

import java.util.List;

public class FriendsRoster {
    private static List<FriendTempData> friendsRoster = null;

    
    private FriendsRoster(){
    }
    
    private static volatile  FriendsRoster instance = null;
    public static FriendsRoster getInstance() {
        if (instance == null) {
            synchronized(FriendsRoster.class) {
                if (instance == null)
                	instance = new FriendsRoster(); 
            }
        }
        return instance;
    }
   
	public  List<FriendTempData> getFriendsRoster() {
		return friendsRoster;
	}

	public static void setFriendsRoster(List<FriendTempData> friendsRoster) {
		FriendsRoster.friendsRoster = friendsRoster;
	}

	public static void setInstance(FriendsRoster instance) {
		FriendsRoster.instance = instance;
	}
    
    
}
