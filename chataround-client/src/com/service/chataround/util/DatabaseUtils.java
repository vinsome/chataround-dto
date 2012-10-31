package com.service.chataround.util;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.service.chataround.db.DB_Helper;
import com.service.chataround.dto.chat.ChatAroundDto;

public class DatabaseUtils {
	public static ChatAroundDto addMessageToDb(Context ctx, ChatAroundDto dto) {
		DB_Helper db = new DB_Helper(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
			dto = db.addMessage(dto, sdb);
		sdb.close();
		db.close();
		return dto;
	}
	
	public static ArrayList<ChatAroundDto> getMessageFromToDb(Context ctx) {
		DB_Helper db = new DB_Helper(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
			Cursor cursor = db.getAllMessages(sdb);
			ArrayList<ChatAroundDto> result = getMessages(cursor);
		sdb.close();
		db.close();
		return result;
	}
	
	public static void updateMessageFieldById(Context ctx,String id, String field,String value) {
		DB_Helper db = new DB_Helper(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
			db.updateToggleField(id, field, value, sdb);
		sdb.close();
		db.close();
	}
	public static void deleteAllFromDatabase(Context ctx) {
		DB_Helper db = new DB_Helper(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
		db.deleteAllFromDatabase(sdb);
		sdb.close();
		db.close();
	}		
		
	private  static ArrayList<ChatAroundDto> getMessages(Cursor c){
		 ArrayList<ChatAroundDto> mFilas = new ArrayList<ChatAroundDto>();
		 c.moveToFirst();
		 while (!c.isAfterLast()) {
			 	ChatAroundDto dto = cursorToChatAroundDto(c);
			 	mFilas.add(dto);
			 c.moveToNext();
		 }
		 c.close();
			
		 return mFilas;
	}
	
	private static ChatAroundDto cursorToChatAroundDto(Cursor c){
		ChatAroundDto dto = new ChatAroundDto();
		    dto.setId(c.getLong(0));
			dto.setNickName(c.getString(1));
			dto.setMessage(c.getString(2));
			dto.setTime(new Date(c.getLong(3)));
			dto.setDeviceId((c.getString(4)));
			dto.setSent(( c.getInt(5)==0?false:true ));
			dto.setMine(( c.getInt(6)==0?false:true ));
			return dto;
	}
}
