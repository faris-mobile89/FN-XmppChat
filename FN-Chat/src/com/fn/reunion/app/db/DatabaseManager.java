package com.fn.reunion.app.db;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;

import com.fn.reunion.app.db.tables.FriendTable;
import com.fn.reunion.app.db.tables.MessageTable;
import com.fn.reunion.app.db.tables.UserTable;
import com.fn.reunion.app.model.Friend;
import com.fn.reunion.app.model.MessageCache;
import com.fn.reunion.app.utility.Consts;

public class DatabaseManager {

    private static String USER_FRIEND_RELATION_KEY = UserTable.TABLE_NAME + "." + UserTable.Cols.USER_ID + " = " + FriendTable.TABLE_NAME + "." + FriendTable.Cols.USER_ID;

    public static void savePeople(Context context,List<Friend> friendsList) {
    	
        for (Friend friend : friendsList) {
            if (!isUserRequested(context, friend.getUserId())) {
                saveFriend(context, friend);
            }
        }
    }

    public static void saveFriend(Context context, Friend friend) {

    }

    private static ContentValues getContentValuesFriendTable(Friend friend) {
        ContentValues values = new ContentValues();

        values.put(FriendTable.Cols.USER_ID, friend.getUserId());
        values.put(FriendTable.Cols.RELATION_STATUS_ID, friend.getRelationStatusId());
        values.put(FriendTable.Cols.IS_STATUS_ASK, friend.isAskStatus());
        values.put(FriendTable.Cols.IS_REQUESTED_FRIEND, friend.isRequestedFriend());

        return values;
    }

    public static boolean isUserRequested(Context context, int userId) {
        boolean isUserRequested = false;

        Cursor cursor = context.getContentResolver().query(FriendTable.CONTENT_URI, null,
                FriendTable.Cols.USER_ID + " = " + userId, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            isUserRequested = cursor.getInt(cursor.getColumnIndex(
                    FriendTable.Cols.IS_REQUESTED_FRIEND)) > Consts.ZERO_INT_VALUE;
        }

        if (cursor != null) {
            cursor.close();
        }

        return isUserRequested;
    }

    public static boolean isUserInBase(Context context, int searchId) {
        String condition = FriendTable.Cols.USER_ID + " = " + searchId;

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(UserTable.CONTENT_URI, null, condition, null, null);

        boolean isUserInBase = cursor.getCount() > Consts.ZERO_INT_VALUE;

        cursor.close();

        return isUserInBase;
    }

    public static void deleteAllFriends(Context context) {
        context.getContentResolver().delete(FriendTable.CONTENT_URI, null, null);
    }

    public static void deleteAllUsers(Context context) {
        context.getContentResolver().delete(UserTable.CONTENT_URI, null, null);
    }

    public static MessageCache getMessageCacheFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MessageTable.Cols.ID));
        String dialogId = cursor.getString(cursor.getColumnIndex(MessageTable.Cols.DIALOG_ID));
        String packetId = cursor.getString(cursor.getColumnIndex(MessageTable.Cols.PACKET_ID));
        Integer senderId = cursor.getInt(cursor.getColumnIndex(MessageTable.Cols.SENDER_ID));
        String body = cursor.getString(cursor.getColumnIndex(MessageTable.Cols.BODY));
        long time = cursor.getLong(cursor.getColumnIndex(MessageTable.Cols.TIME));
        String attachUrl = cursor.getString(cursor.getColumnIndex(MessageTable.Cols.ATTACH_FILE_ID));
        boolean isRead = cursor.getInt(cursor.getColumnIndex(
                MessageTable.Cols.IS_READ)) > Consts.ZERO_INT_VALUE;
        boolean isDelivered = cursor.getInt(cursor.getColumnIndex(
                MessageTable.Cols.IS_DELIVERED)) > Consts.ZERO_INT_VALUE;

        MessageCache messageCache = new MessageCache(id, dialogId, packetId, senderId, body, attachUrl, time,
                isRead, isDelivered);

        return messageCache;
    }

    public static Cursor getAllDialogMessagesByDialogId(Context context, String dialogId) {
        return context.getContentResolver().query(MessageTable.CONTENT_URI, null,
                MessageTable.Cols.DIALOG_ID + " = '" + dialogId + "'", null,
                MessageTable.Cols.ID + " ORDER BY " + MessageTable.Cols.TIME + " COLLATE NOCASE ASC");
    }

    public static void deleteAllMessages(Context context) {
        context.getContentResolver().delete(MessageTable.CONTENT_URI, null, null);
    }

    public static void saveChatMessage(Context context, MessageCache messageCache) {
        ContentValues values = new ContentValues();
        String body = messageCache.getMessage();

        values.put(MessageTable.Cols.ID, messageCache.getId());
        values.put(MessageTable.Cols.DIALOG_ID, messageCache.getDialogId());
        values.put(MessageTable.Cols.PACKET_ID, messageCache.getPacketId());
        values.put(MessageTable.Cols.SENDER_ID, messageCache.getSenderId());

        if (TextUtils.isEmpty(body)) {
            values.put(MessageTable.Cols.BODY, body);
        } else {
            values.put(MessageTable.Cols.BODY, Html.fromHtml(body).toString());
        }

        values.put(MessageTable.Cols.TIME, messageCache.getTime());
        values.put(MessageTable.Cols.ATTACH_FILE_ID, messageCache.getAttachUrl());
        values.put(MessageTable.Cols.IS_READ, messageCache.isRead());
        values.put(MessageTable.Cols.IS_DELIVERED, messageCache.isDelivered());
        context.getContentResolver().insert(MessageTable.CONTENT_URI, values);

  
    }

    public static void deleteMessagesByDialogId(Context context, String dialogId) {
        context.getContentResolver().delete(MessageTable.CONTENT_URI,
                MessageTable.Cols.DIALOG_ID + " = '" + dialogId + "'", null);
    }

    public static boolean deleteUserById(Context context, int userId) {
        int deletedRow = context.getContentResolver().delete(FriendTable.CONTENT_URI,
                FriendTable.Cols.USER_ID + " = " + userId, null);
        return deletedRow > Consts.ZERO_INT_VALUE;
    }

  

    public static int getCountUnreadMessagesByRoomJid(Context context, String dialogId) {
        Cursor cursor = context.getContentResolver().query(MessageTable.CONTENT_URI, null,
                MessageTable.Cols.IS_READ + " = 0 AND " + MessageTable.Cols.DIALOG_ID + " = '" + dialogId + "'",
                null, null);
        int countMessages = cursor.getCount();
        cursor.close();
        return countMessages;
    }

    public static void clearAllCache(Context context) {
        deleteAllUsers(context);
        deleteAllFriends(context);
        deleteAllMessages(context);
        // TODO clear something else
    }

    public static void updateStatusMessage(Context context, String messageId, boolean isRead) {
        ContentValues values = new ContentValues();
        String condition = MessageTable.Cols.ID + "='" + messageId + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MessageTable.CONTENT_URI, null, condition, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            values.put(MessageTable.Cols.IS_READ, isRead);
            resolver.update(MessageTable.CONTENT_URI, values, condition, null);
            cursor.close();
          }
    }

    public static void updateMessageDeliveryStatus(Context context, String messageId, boolean isDelivered) {
        ContentValues values = new ContentValues();
        String condition = MessageTable.Cols.ID + "='" + messageId + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MessageTable.CONTENT_URI, null, condition, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            values.put(MessageTable.Cols.IS_DELIVERED, isDelivered);
            resolver.update(MessageTable.CONTENT_URI, values, condition, null);
            cursor.close();
        }
    }
}