package com.fn.reunion.app.db;

import android.content.UriMatcher;
import android.net.Uri;

import com.fn.reunion.app.db.tables.FriendTable;
import com.fn.reunion.app.db.tables.MessageTable;
import com.fn.reunion.app.db.tables.UserTable;


public class ContentDescriptor {

    public static final String AUTHORITY = "com.fn.reunion.app";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, UserTable.PATH, UserTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, FriendTable.PATH, FriendTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UserTable.USER_FRIEND_PATH, UserTable.USER_FRIEND_PATH_TOKEN);
        matcher.addURI(AUTHORITY, MessageTable.PATH, MessageTable.PATH_TOKEN);
        // TODO Sergey Fedunets other tables can be added

        return matcher;
    }
}
