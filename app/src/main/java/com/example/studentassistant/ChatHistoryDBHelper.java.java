package com.sudanese.studentassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatHistoryDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ChatHistory.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_SESSIONS = "chat_sessions";
    private static final String TABLE_MESSAGES = "chat_messages";
    
    private static final String COLUMN_SESSION_ID = "session_id";
    private static final String COLUMN_SESSION_TITLE = "session_title";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String COLUMN_SUBJECT = "subject";
    
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_MESSAGE_CONTENT = "message_content";
    private static final String COLUMN_MESSAGE_TYPE = "message_type";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IS_SAVED = "is_saved";
    
    public ChatHistoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SESSIONS_TABLE = "CREATE TABLE " + TABLE_SESSIONS + "("
                + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SESSION_TITLE + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_CREATED_AT + " INTEGER,"
                + COLUMN_UPDATED_AT + " INTEGER" + ")";
        db.execSQL(CREATE_SESSIONS_TABLE);
        
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SESSION_ID + " INTEGER,"
                + COLUMN_MESSAGE_CONTENT + " TEXT,"
                + COLUMN_MESSAGE_TYPE + " INTEGER,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_IS_SAVED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_SESSION_ID + ") REFERENCES " + TABLE_SESSIONS + "(" + COLUMN_SESSION_ID + ")" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }
    
    public long createSession(String sessionTitle, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_TITLE, sessionTitle);
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_CREATED_AT, new Date().getTime());
        values.put(COLUMN_UPDATED_AT, new Date().getTime());
        
        long sessionId = db.insert(TABLE_SESSIONS, null, values);
        db.close();
        return sessionId;
    }
    
    public List<ChatSession> getAllSessions() {
        List<ChatSession> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_SESSIONS + " ORDER BY " + COLUMN_UPDATED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                ChatSession session = new ChatSession();
                session.setSessionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)));
                session.setSessionTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_TITLE)));
                session.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
                session.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))));
                session.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))));
                
                session.setMessages(getMessagesForSession(session.getSessionId()));
                
                sessions.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessions;
    }
    
    public ChatSession getSessionById(int sessionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ChatSession session = null;
        
        String query = "SELECT * FROM " + TABLE_SESSIONS + " WHERE " + COLUMN_SESSION_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId)});
        
        if (cursor.moveToFirst()) {
            session = new ChatSession();
            session.setSessionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)));
            session.setSessionTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_TITLE)));
            session.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
            session.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))));
            session.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))));
            session.setMessages(getMessagesForSession(sessionId));
        }
        cursor.close();
        db.close();
        return session;
    }
    
    public void updateSessionTitle(int sessionId, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_TITLE, newTitle);
        values.put(COLUMN_UPDATED_AT, new Date().getTime());
        
        db.update(TABLE_SESSIONS, values, COLUMN_SESSION_ID + " = ?", 
                 new String[]{String.valueOf(sessionId)});
        db.close();
    }
    
    public void deleteSession(int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_MESSAGES, COLUMN_SESSION_ID + " = ?", 
                 new String[]{String.valueOf(sessionId)});
        
        db.delete(TABLE_SESSIONS, COLUMN_SESSION_ID + " = ?", 
                 new String[]{String.valueOf(sessionId)});
        db.close();
    }
    
    public long addMessageToSession(int sessionId, ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_ID, sessionId);
        values.put(COLUMN_MESSAGE_CONTENT, message.getMessage());
        values.put(COLUMN_MESSAGE_TYPE, message.getMessageType());
        values.put(COLUMN_SUBJECT, message.getSubject());
        values.put(COLUMN_TIMESTAMP, message.getTimestamp().getTime());
        values.put(COLUMN_IS_SAVED, message.isSaved() ? 1 : 0);
        
        long messageId = db.insert(TABLE_MESSAGES, null, values);
        
        ContentValues sessionValues = new ContentValues();
        sessionValues.put(COLUMN_UPDATED_AT, new Date().getTime());
        db.update(TABLE_SESSIONS, sessionValues, COLUMN_SESSION_ID + " = ?", 
                 new String[]{String.valueOf(sessionId)});
        
        db.close();
        return messageId;
    }
    
    public List<ChatMessage> getMessagesForSession(int sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_MESSAGES + 
                      " WHERE " + COLUMN_SESSION_ID + " = ?" +
                      " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId)});
        
        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setMessageId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
                message.setSessionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)));
                message.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_CONTENT)));
                message.setMessageType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TYPE)));
                message.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
                message.setTimestamp(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))));
                message.setSaved(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SAVED)) == 1);
                
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }
    
    public int getSessionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SESSIONS;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
    
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, null, null);
        db.delete(TABLE_SESSIONS, null, null);
        db.close();
    }
}