package com.pandah.ap3_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PLAYERS = "players";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable = "CREATE TABLE " + TABLE_PLAYERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL" +
                ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        onCreate(db);
    }

    // Metodo para adicionar um jogador
    public void addPlayer(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(TABLE_PLAYERS, null, values);
        db.close();
    }

    // Metodo para pegar todos os jogadores
    public Cursor getAllPlayers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_NAME};
        return db.query(TABLE_PLAYERS, columns, null, null, null, null, null);
    }

    // Metodo para apagar todos os jogadores
    public void clearPlayers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS, null, null);
        db.close();
    }

}
