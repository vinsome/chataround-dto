package com.service.chataround.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.service.chataround.dto.chat.ChatMessageDto;

public class DB_Helper extends SQLiteOpenHelper {
	public static String dbName = "DB_CHATAROUND";

	public static String CHAT_AROUND_MESSAGE_TABLE = "CHAT_AROUND_MESSAGE";

	public static String ID_FIELD = "ID";
	public static String USER_FIELD = "USER";
	public static String MESSAGE_FIELD = "MESSAGE";
	public static String TIME_FIELD = "TIME";
	public static String SENT_FIELD = "SENT";
	public static String MINE_FIELD = "MINE";
	public static String REG_FIELD = "REGID";
	

	public DB_Helper(Context context) {
		super(context, dbName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + CHAT_AROUND_MESSAGE_TABLE + " (" + " " + ID_FIELD
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + " " + USER_FIELD
				+ " TEXT, " + " " + MESSAGE_FIELD + " TEXT, " + " "
				+ TIME_FIELD + " DATE, " + " " 
				+ " " + REG_FIELD + " TEXT, " + " " + SENT_FIELD
				+ " INTEGER NOT NULL DEFAULT 0, " + " " 
				+ " " + MINE_FIELD
				+ " INTEGER NOT NULL DEFAULT 0 " + " )");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CHAT_AROUND_MESSAGE_TABLE);
		onCreate(db);
	}

	public ChatMessageDto addMessage(ChatMessageDto dto, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(USER_FIELD, dto.getNickName());
		cv.put(MESSAGE_FIELD, dto.getMessage());
		cv.put(TIME_FIELD, new Date().getTime());
		cv.put(SENT_FIELD, dto.isSent());
		cv.put(MINE_FIELD, dto.isMine());
		cv.put(REG_FIELD, dto.getSenderId());
		long id = db.insert(CHAT_AROUND_MESSAGE_TABLE, null, cv);
		dto.setId(id);
		db.close();
		return dto;
	}

	public Cursor getAllMessages(SQLiteDatabase db) {
		Cursor cur = db.rawQuery("SELECT * FROM " + CHAT_AROUND_MESSAGE_TABLE, null);
		return cur;
	}

	public Cursor getMessageById(int id, SQLiteDatabase db) {
		String[] params = new String[] { String.valueOf(id) };
		Cursor c = db.rawQuery("SELECT * FROM " + CHAT_AROUND_MESSAGE_TABLE + " WHERE "
				+ ID_FIELD + " =? ", params);
		c.moveToFirst();
		return c;
	}

	public int getMessagesCount(SQLiteDatabase db) {
		Cursor cur = getAllMessages(db);
		int x = cur.getCount();
		cur.close();
		return x;
	}

	public void updateToggleField(String id, String field, String value,
			SQLiteDatabase db) {
		String update = " UPDATE " + CHAT_AROUND_MESSAGE_TABLE + " SET " + SENT_FIELD
				+ " = 1 " + " WHERE " + ID_FIELD + " = " + id;
		db.execSQL(update);

	}
	
	public void deleteAllFromDatabase(SQLiteDatabase db) {
	    db.delete(CHAT_AROUND_MESSAGE_TABLE, null, null);
	}	

}
