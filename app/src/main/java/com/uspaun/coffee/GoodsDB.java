package com.uspaun.coffee;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.ViewDebug;

import java.util.logging.StreamHandler;

/**
 * Created by root on 01.07.15.
 */
public class GoodsDB {

    private class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

        private static final String DATABASE_NAME = "uspaungoodsdb.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "goods";
        private static final String DATABASE_TABLE_SOLD = "sold";
        private static final String DATABASE_TABLE_TILES = "tiles";

        public static final String GOODS_NAME_COLUMN = "good_name";
        public static final String BARCODE_COLUMN = "barcode";
        public static final String COUNT_COLUMN = "count";
        public static final String PRICE_COLUMN = "price";
        public static final String ORIGINAL_PRICE_COLUMN = "original_price";
        public static final String CHANGED_COLUMN = "changed";
        public static final String DATETIME_COLUMN = "date";
        public static final String IMAGE_COLUMN = "image";

        private static final String DATABASE_CREATE_SCRIPT = "create table "
                + DATABASE_TABLE + " (" + BaseColumns._ID
                + " integer primary key autoincrement, " + GOODS_NAME_COLUMN
                + " text not null, " + BARCODE_COLUMN + " text not null, " + COUNT_COLUMN
                + " integer not null default 0, " + PRICE_COLUMN + " float not null default 0, " +
                ORIGINAL_PRICE_COLUMN + " float not null default 0, " + CHANGED_COLUMN + " integer default 0);";
        private static final String DATABASE_SOLD_CREATE_SCRIPT = "create table "
                + DATABASE_TABLE_SOLD + " (" + BaseColumns._ID
                + " integer primary key autoincrement, " + GOODS_NAME_COLUMN
                + " text not null, " + BARCODE_COLUMN + " text not null, " + COUNT_COLUMN
                + " integer not null default 0, " + PRICE_COLUMN + " float not null default 0, " +
                ORIGINAL_PRICE_COLUMN + " float not null default 0);";
        private static final String DATABASE_TILES_CREATE_SCRIPT = "create table "
                + DATABASE_TABLE_TILES + " (" + BaseColumns._ID
                + " integer primary key autoincrement, " + GOODS_NAME_COLUMN
                + " text not null, " + IMAGE_COLUMN + " text null);";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SCRIPT);
            db.execSQL(DATABASE_SOLD_CREATE_SCRIPT);
            db.execSQL(DATABASE_TILES_CREATE_SCRIPT);
            Log.w("myLog", "Created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Запишем в журнал
            //Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
            Log.w("myLog", "Updated");
            // Удаляем старую таблицу и создаём новую
            //db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
            // Создаём новую таблицу
            //onCreate(db);
        }
    }
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private ContentValues newValues;

    //String Name, BarCode;
    //int Count = 0;
    //float Price = 0;

    public void CreateGoodsDB(Context context) {
        mDatabaseHelper = new DatabaseHelper(context, "uspaungoodsdb.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        newValues = new ContentValues();
    }

    public int  InsertGoods(String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice, float GoodsOriginalPrice)
    {
        newValues.put(mDatabaseHelper.GOODS_NAME_COLUMN, GoodsName);
        newValues.put(mDatabaseHelper.BARCODE_COLUMN, GoodsBarCode);
        newValues.put(mDatabaseHelper.COUNT_COLUMN, GoodsCount);
        newValues.put(mDatabaseHelper.PRICE_COLUMN, GoodsPrice);
        newValues.put(mDatabaseHelper.ORIGINAL_PRICE_COLUMN, GoodsOriginalPrice);
        newValues.put(mDatabaseHelper.CHANGED_COLUMN, 1);
        int InsCount = (int) mSqLiteDatabase.insert(mDatabaseHelper.DATABASE_TABLE, null, newValues);
        newValues.clear();
        return InsCount;
    }

    public int  InsertGoods(int GoodsID, String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice, float GoodsOriginalPrice)
    {
        newValues.put(BaseColumns._ID, GoodsID);
        newValues.put(mDatabaseHelper.GOODS_NAME_COLUMN, GoodsName);
        newValues.put(mDatabaseHelper.BARCODE_COLUMN, GoodsBarCode);
        newValues.put(mDatabaseHelper.COUNT_COLUMN, GoodsCount);
        newValues.put(mDatabaseHelper.PRICE_COLUMN, GoodsPrice);
        newValues.put(mDatabaseHelper.ORIGINAL_PRICE_COLUMN, GoodsOriginalPrice);
        newValues.put(mDatabaseHelper.CHANGED_COLUMN, 1);
        int InsCount = (int) mSqLiteDatabase.insert(mDatabaseHelper.DATABASE_TABLE, null, newValues);
        newValues.clear();
        return InsCount;
    }

    public int  InsertSoldGoods(String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice,
                                float GoodsOriginalPrice)
    {
        newValues.put(mDatabaseHelper.GOODS_NAME_COLUMN, GoodsName);
        newValues.put(mDatabaseHelper.BARCODE_COLUMN, GoodsBarCode);
        newValues.put(mDatabaseHelper.COUNT_COLUMN, GoodsCount);
        newValues.put(mDatabaseHelper.PRICE_COLUMN, GoodsPrice);
        newValues.put(mDatabaseHelper.ORIGINAL_PRICE_COLUMN, GoodsOriginalPrice);
        int InsCount = (int) mSqLiteDatabase.insert(mDatabaseHelper.DATABASE_TABLE_SOLD, null, newValues);
        newValues.clear();
        return InsCount;
    }

    public void deleteGoodsByName(String FullName)
    {
        String query = "DELETE FROM goods WHERE good_name = '" + FullName + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public void deleteSoldGoodsByName(String FullName)
    {
        String query = "DELETE FROM sold WHERE good_name = '" + FullName + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public void DeleteGoodsByBarCode(String BarCode)
    {
        String query = "DELETE FROM goods WHERE barcode = '" + BarCode + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public void truncateSoldGoods()
    {
        mSqLiteDatabase.delete("sold", null, null);
    }

    public Goods GetDoodsInfoByName(String FullName)
    {
        String query = "SELECT * FROM goods WHERE good_name = '" + FullName + "';";
        Goods gdDB;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            gdDB = new Goods(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.GOODS_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(mDatabaseHelper.BARCODE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.PRICE_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.ORIGINAL_PRICE_COLUMN)));
            gdDB.GoodsId = cursor.getString(cursor.getColumnIndex("_id"));
            cursor.close();
            return gdDB;
        }
        else return null;
    }

    public boolean isExist(int id)
    {
        String query = "SELECT * FROM goods WHERE _id = '" + id + "';";
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public Goods GetDoodsInfoByBarCode(String GoodsBarCode)
    {
        String query = "SELECT * FROM goods WHERE barcode = '" + GoodsBarCode + "';";
        Goods gdBD;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            gdBD = new Goods(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.GOODS_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(mDatabaseHelper.BARCODE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.PRICE_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.ORIGINAL_PRICE_COLUMN)));
            gdBD.GoodsId = cursor.getString(cursor.getColumnIndex("_id"));
            cursor.close();
            return gdBD;
        }
        else return null;
    }

    public int GetGoodsCountByName(String Name)
    {
        String query = "SELECT count FROM goods WHERE good_name = '" + Name + "';";
        int res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res =  cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN));
        else res = 0;
        cursor.close();
        return res;
    }

    public int GetSoldGoodsCountByName(String Name)
    {
        String query = "SELECT count FROM sold WHERE good_name = '" + Name + "';";
        int res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res =  cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN));
        else res = 0;
        cursor.close();
        return res;
    }

    public int GetGoodsCountByBarCode(String BarCode)
    {
        String query = "SELECT count FROM goods WHERE good_name = '" + BarCode + "';";
        int res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        res = cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN));
        cursor.close();
        return res;
    }

    public void ChangeGoodsCountByName(String Name, int NewCount)
    {
        String query = "UPDATE goods SET count = " + Integer.toString(NewCount) + ", changed = 1" + " WHERE good_name = '"
                + Name + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public void ChangeSoldGoodsCountByName(String Name, int NewCount)
    {
        String query = "UPDATE sold SET count = " + Integer.toString(NewCount) + " WHERE good_name = '"
                + Name + "';";
        mSqLiteDatabase.execSQL(query);

    }

    public void changeGoodsById(int id, String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice, float GoodsOriginalPrice)
    {
        String query = "UPDATE goods SET good_name = '"+GoodsName+"', barcode = '"+GoodsBarCode+"', count = '"+GoodsCount+
                "', price = '"+GoodsPrice+"', original_price = '"+GoodsOriginalPrice+"' WHERE _id = '" + id + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public String GetGoodsNameByBarCode(String BarCode)
    {
        String query = "SELECT good_name FROM goods WHERE barcode = '" + BarCode + "';";
        String res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.GOODS_NAME_COLUMN));
        else
            res = "Немає в наявності!";
        cursor.close();
        return res;
    }

    public String getGoodsBarCodeByName(String Name)
    {
        String query = "SELECT barcode FROM goods WHERE good_name = '" + Name + "';";
        String res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.BARCODE_COLUMN));
        else
            res = "";
        cursor.close();
        return res;
    }

    public float getGoodsPriceByName(String Name)
    {
        String query = "SELECT price FROM goods WHERE good_name = '" + Name + "';";
        float res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.PRICE_COLUMN));
        else
            res = 0;
        cursor.close();
        return res;
    }

    public float getGoodsOriginalPriceByName(String Name)
    {
        String query = "SELECT original_price FROM goods WHERE good_name = '" + Name + "';";
        float res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.ORIGINAL_PRICE_COLUMN));
        else
            res = 0;
        cursor.close();
        return res;
    }

    public Goods GetFirstGoods()
    {
        Goods gdDB = new Goods();
        /*Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE, null,
                //new String[]{ DatabaseHelper.GOODS_NAME_COLUMN, DatabaseHelper.BARCODE_COLUMN, DatabaseHelper.COUNT_COLUMN, DatabaseHelper.PRICE_COLUMN},
                null,
                null,
                null,
                null,
                null
        );*/
        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT * FROM goods;", null);
        if(cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.GOODS_NAME_COLUMN));
            gdDB = new Goods(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.GOODS_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(mDatabaseHelper.BARCODE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.COUNT_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.PRICE_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(mDatabaseHelper.ORIGINAL_PRICE_COLUMN)));
            cursor.close();
        }
        return gdDB;
    }

    public void updateGoodsById(Goods gd,String Id)
    {
        String query = "UPDATE goods SET good_name = '" + gd.Name + "', barcode = '" + gd.BarCode +
                "', count = " + Integer.toString(gd.Count) + ", price = " + Float.toString(gd.Price) +
                ", original_price = " + Float.toString(gd.OriginalPrice) + ", changed = 1" + " WHERE _id = '" + Id + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public String[] getGoodsNamesArray()
    {
        Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE, new String[]{
                DatabaseHelper.GOODS_NAME_COLUMN}, null, null, null, null, null);
        int NamesCount = cursor.getCount();
        String[] result = new String[NamesCount];
        cursor.moveToFirst();
        for(int i = 0; i < NamesCount; i++) {
            result[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GOODS_NAME_COLUMN));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Goods[] getSoldGoodsArray()
    {
        Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE_SOLD, new String[]{
                DatabaseHelper.GOODS_NAME_COLUMN, DatabaseHelper.BARCODE_COLUMN, DatabaseHelper.COUNT_COLUMN,
                DatabaseHelper.PRICE_COLUMN, DatabaseHelper.ORIGINAL_PRICE_COLUMN }, null, null, null, null, null);
        int NamesCount = cursor.getCount();
        Goods[] result = new Goods[NamesCount];
        cursor.moveToFirst();
        for(int i = 0; i < NamesCount; i++) {
            String Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GOODS_NAME_COLUMN));
            String BarCode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BARCODE_COLUMN));
            int Count = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COUNT_COLUMN));
            float Price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.PRICE_COLUMN));
            float OrigPrice = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_PRICE_COLUMN));
            result[i] = new Goods(Name, BarCode, Count, Price, OrigPrice);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Goods[] getGoodsRemainingArray()
    {
        Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE, new String[]{
                DatabaseHelper.GOODS_NAME_COLUMN, DatabaseHelper.BARCODE_COLUMN, DatabaseHelper.COUNT_COLUMN,
                DatabaseHelper.PRICE_COLUMN, DatabaseHelper.ORIGINAL_PRICE_COLUMN }, DatabaseHelper.COUNT_COLUMN + ">?",
                new String[] { String.valueOf(0) }, null, null, null, null);
        int NamesCount = cursor.getCount();
        Goods[] result = new Goods[NamesCount];
        if(cursor != null)
            cursor.moveToFirst();
        for(int i = 0; i < NamesCount; i++) {
            int Count = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COUNT_COLUMN));
            String Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GOODS_NAME_COLUMN));
            String BarCode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BARCODE_COLUMN));
            float Price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.PRICE_COLUMN));
            float OrigPrice = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_PRICE_COLUMN));
            result[i] = new Goods(Name, BarCode, Count, Price, OrigPrice);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Goods[] getNewGoodsArray()
    {
        Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE, new String[]{
                DatabaseHelper.GOODS_NAME_COLUMN, DatabaseHelper.BARCODE_COLUMN, DatabaseHelper.COUNT_COLUMN,
                DatabaseHelper.PRICE_COLUMN, DatabaseHelper.ORIGINAL_PRICE_COLUMN, DatabaseHelper.CHANGED_COLUMN }, DatabaseHelper.CHANGED_COLUMN + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);
        int NamesCount = cursor.getCount();
        Goods[] result = new Goods[NamesCount];
        cursor.moveToFirst();
        for(int i = 0; i < NamesCount; i++) {
            //int gdChanged = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CHANGED_COLUMN));
                String Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GOODS_NAME_COLUMN));
                String BarCode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BARCODE_COLUMN));
                int Count = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COUNT_COLUMN));
                float Price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.PRICE_COLUMN));
                float OrigPrice = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_PRICE_COLUMN));
                result[i] = new Goods(Name, BarCode, Count, Price, OrigPrice);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public void changeGoodsChanged(String Name)
    {
        String query = "UPDATE goods SET changed = 0 WHERE good_name = '"
                + Name + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public String[] getTilesNamesArray()
    {
        Cursor cursor = mSqLiteDatabase.query(mDatabaseHelper.DATABASE_TABLE_TILES, new String[]{
                DatabaseHelper.GOODS_NAME_COLUMN}, null, null, null, null, null);
        int NamesCount = cursor.getCount();
        String[] result = new String[NamesCount];
        cursor.moveToFirst();
        for(int i = 0; i < NamesCount; i++) {
            result[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GOODS_NAME_COLUMN));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public int putTileName(String strTileName)
    {
        newValues.put(mDatabaseHelper.GOODS_NAME_COLUMN, strTileName);
        int InsCount = (int) mSqLiteDatabase.insert(mDatabaseHelper.DATABASE_TABLE_TILES, null, newValues);
        newValues.clear();
        return InsCount;
    }


    public void deleteTilesByName(String FullName)
    {
        String query = "DELETE FROM tiles WHERE good_name = '" + FullName + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public void changeTilePicture(String Name, String ImageSrc)
    {
        String query = "UPDATE tiles SET image = '" + ImageSrc + "' WHERE good_name = '"
                + Name + "';";
        mSqLiteDatabase.execSQL(query);
    }

    public int getLastID()
    {
        String query = "SELECT _id FROM goods ORDER BY _id DESC LIMIT 1;";
        int res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getInt(cursor.getColumnIndex("_id"));
        else
            res = 0;
        cursor.close();
        return res;
    }

    public int getLastReceipt()
    {
        String query = "SELECT receipt FROM sold ORDER BY receipt DESC LIMIT 1;";
        int res;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            res = cursor.getInt(cursor.getColumnIndex("receipt"));
        else
            res = 0;
        cursor.close();
        return res;
    }
}
