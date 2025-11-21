package com.sudanese.studentassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyScheduleDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudySchedules.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_SCHEDULES = "study_schedules";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_TOPIC = "topic";
    private static final String COLUMN_STUDY_DATE = "study_date";
    private static final String COLUMN_STUDY_TIME = "study_time";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_IS_COMPLETED = "is_completed";
    private static final String COLUMN_HAS_REMINDER = "has_reminder";
    private static final String COLUMN_REMINDER_MINUTES = "reminder_minutes";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_CREATED_AT = "created_at";
    
    public StudyScheduleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCHEDULES_TABLE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TOPIC + " TEXT,"
                + COLUMN_STUDY_DATE + " INTEGER,"
                + COLUMN_STUDY_TIME + " INTEGER,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0,"
                + COLUMN_HAS_REMINDER + " INTEGER DEFAULT 1,"
                + COLUMN_REMINDER_MINUTES + " INTEGER DEFAULT 15,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_CREATED_AT + " INTEGER" + ")";
        db.execSQL(CREATE_SCHEDULES_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        onCreate(db);
    }
    
    public long addSchedule(StudySchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SUBJECT, schedule.getSubject());
        values.put(COLUMN_TOPIC, schedule.getTopic());
        values.put(COLUMN_STUDY_DATE, schedule.getStudyDate().getTime());
        values.put(COLUMN_STUDY_TIME, schedule.getStudyTime().getTime());
        values.put(COLUMN_DURATION, schedule.getDuration());
        values.put(COLUMN_IS_COMPLETED, schedule.isCompleted() ? 1 : 0);
        values.put(COLUMN_HAS_REMINDER, schedule.isHasReminder() ? 1 : 0);
        values.put(COLUMN_REMINDER_MINUTES, schedule.getReminderMinutes());
        values.put(COLUMN_NOTES, schedule.getNotes());
        values.put(COLUMN_CREATED_AT, new Date().getTime());
        
        long id = db.insert(TABLE_SCHEDULES, null, values);
        db.close();
        return id;
    }
    
    public List<StudySchedule> getAllSchedules() {
        List<StudySchedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_SCHEDULES + " ORDER BY " + COLUMN_STUDY_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = extractScheduleFromCursor(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }
    
    public List<StudySchedule> getUpcomingSchedules() {
        List<StudySchedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        long currentTime = System.currentTimeMillis();
        
        String query = "SELECT * FROM " + TABLE_SCHEDULES + 
                      " WHERE " + COLUMN_STUDY_TIME + " > ? AND " + COLUMN_IS_COMPLETED + " = 0" +
                      " ORDER BY " + COLUMN_STUDY_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentTime)});
        
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = extractScheduleFromCursor(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }
    
    public List<StudySchedule> getSchedulesBySubject(String subject) {
        List<StudySchedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_SCHEDULES + 
                      " WHERE " + COLUMN_SUBJECT + " = ?" +
                      " ORDER BY " + COLUMN_STUDY_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{subject});
        
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = extractScheduleFromCursor(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }
    
    private StudySchedule extractScheduleFromCursor(Cursor cursor) {
        StudySchedule schedule = new StudySchedule();
        schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        schedule.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
        schedule.setTopic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOPIC)));
        schedule.setStudyDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDY_DATE))));
        schedule.setStudyTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDY_TIME))));
        schedule.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        schedule.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1);
        schedule.setHasReminder(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HAS_REMINDER)) == 1);
        schedule.setReminderMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_MINUTES)));
        schedule.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
        return schedule;
    }
    
    public void updateScheduleCompletion(int scheduleId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        
        db.update(TABLE_SCHEDULES, values, COLUMN_ID + " = ?", 
                 new String[]{String.valueOf(scheduleId)});
        db.close();
    }
    
    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COLUMN_ID + " = ?", 
                 new String[]{String.valueOf(scheduleId)});
        db.close();
    }
    
    public int getScheduleCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SCHEDULES;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
    
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, null, null);
        db.close();
    }
}