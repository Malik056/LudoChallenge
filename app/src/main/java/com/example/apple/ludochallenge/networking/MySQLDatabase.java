package com.example.apple.ludochallenge.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class MySQLDatabase extends SQLiteOpenHelper {


    static MySQLDatabase mySQLDatabase;
    public static String TABLE_NAME = "USERS";
    private String FLAG_NAME = "FLAG_NAME";
    private String FLAG_PIC = "FLAG_PIC";
    private String ID = "ID";
    private String PROFILE_IMAGE_COL = "IMAGES";
    private String USER_NAME = "USER_NAME";
    public static String NAME_FLAG = "NAME_FLAG";
    public static String PIC_FLAG = "PIC_FLAG";
    public static String IMAGE_PROFILE_COL = "IMAGE_PROFILE_COL";
    public static String USERID = "USERID";
    public static String NAME_USER = "NAME_USER";
    public static String LOGINAS = "LOGINAS";
    public static String LOGIN_STATUS = "LOGIN_STATUS";
    public static String LOGIN_STATUS_FACEBOOK = "FACEBOOK";
    public static String LOGIN_STATUS_LUDOCHALLENGE = "LUDOCHALLENGE";
    public static String LOGIN_STATUS_NULL = "NULL";
    public static String FACEBOOK_USER_TABLE = "FACEBOOK_USER_TABLE";
    public static String EMAIL = "EMAIL";

    public static MySQLDatabase getInstance(Context context)
    {
        if(mySQLDatabase == null)
        {
            return new MySQLDatabase(context);
        }
        else return mySQLDatabase;
    }

    public static MySQLDatabase getInstance(Context context, String loginAs)
    {
        if(mySQLDatabase == null)
        {
            return new MySQLDatabase(context, loginAs);
        }
        else return mySQLDatabase;
    }

    public MySQLDatabase(Context context) {
        super(context, "Images", null, 1);
        queuryData("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                ID + " VARCHAR PRIMARY KEY ,"+
                USER_NAME + " VARCHAR, " +
                PROFILE_IMAGE_COL + " BLOG ," +
                FLAG_NAME + " VARCHAR ," +
                FLAG_PIC + " BLOG)");

        queuryData("CREATE TABLE IF NOT EXISTS " + FACEBOOK_USER_TABLE + "( " +
                EMAIL + " VARCHAR PRIMARY KEY ,"+
                USER_NAME + " VARCHAR, " +
                PROFILE_IMAGE_COL + " BLOG ," +
                FLAG_NAME + " VARCHAR ," +
                FLAG_PIC + " BLOG)");


    }

    public MySQLDatabase(Context context, String loginAs) {
        super(context, "Images", null, 1);
        queuryData("DROP TABLE IF EXISTS " + loginAs);
        queuryData("CREATE TABLE IF NOT EXISTS " + loginAs + "( " +
                ID + " VARCHAR PRIMARY KEY, " +
                LOGIN_STATUS + " VARCHAR  )"
        );
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + LOGINAS + " VALUES (?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(2, loginAs);
        statement.bindString(1, "1");
        statement.executeInsert();
    }



    public void UpdateLoginAs(String loginAs){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOGIN_STATUS, loginAs);
        database.update(LOGINAS, contentValues, ID + "=?", new String[]{"1"});
    }


    public String fetchCurrentLoggedIn(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query
                (
                        LOGINAS,
                        new String[] {LOGIN_STATUS},
                        null,
                        null, null, null, null, null
                );

        if(cursor.moveToFirst()){
            String data = cursor.getString(0);
            return data;
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void queuryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String name, byte[] image, byte[] flag_image, String country_name, String idOrEmail, String TABLE_NAME){

        SQLiteDatabase database = getReadableDatabase();

        String ID = this.ID;

        Cursor cursor;

        if(!TABLE_NAME.equals(this.TABLE_NAME))
        {
            ID = this.EMAIL;
        }

        cursor = database.query
                (
                        TABLE_NAME,
                        new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL, FLAG_NAME, FLAG_PIC},
                        ID + " =?",
                        new String[]{idOrEmail}, null, null, null, null
                );

        SQLiteDatabase database1 = getWritableDatabase();
        if(cursor.moveToFirst()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_NAME, name);
            contentValues.put(PROFILE_IMAGE_COL, image);
            contentValues.put(FLAG_NAME, country_name);
            contentValues.put(FLAG_PIC, flag_image);
            database1.update(TABLE_NAME, contentValues, ID + "=?", new String[]{idOrEmail});
        }
        else {

            String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement statement = database1.compileStatement(sql);

            statement.clearBindings();
            statement.bindString(2, name);
            statement.bindString(1, idOrEmail);
            statement.bindString(4, country_name);
            statement.bindBlob(5, flag_image);
            statement.bindBlob(3, image);

            statement.executeInsert();
        }
        cursor.close();
//        String sql = "INSERT INTO " + TABLE_NAME + "( " +
//                ID + ", " + USER_NAME + ", " + PROFILE_IMAGE_COL + ", " +
//                FLAG_NAME  + ", " + FLAG_PIC + ") VALUES ( \'"+ id + "\', \'" + name + "\',\'" + Arrays.toString(image) + "\',\'" + country_name + "\',\'" + Arrays.toString(flag_image) + "\')";

//        database.execSQL(sql);

    }

    public Object getData(String username, String dataType, String TABLE_NAME){
        SQLiteDatabase database = getReadableDatabase();

        String ID = this.ID;
        Cursor cursor;
        if(!TABLE_NAME.equals(this.TABLE_NAME))
        {
            ID = this.EMAIL;
            cursor = database.query
                    (
                            TABLE_NAME,
                            new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL, FLAG_NAME, FLAG_PIC},
                            null,
                            null, null, null, null, null
                    );

        }
        else {
            cursor = database.query
                    (
                            TABLE_NAME,
                            new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL, FLAG_NAME, FLAG_PIC},
                            ID + " =?",
                            new String[]{username}, null, null, null, null
                    );
        }


        Object object = null;
        if(cursor.moveToFirst()) {
            if(dataType.equals(IMAGE_PROFILE_COL)) {
                object = cursor.getBlob(2);
                }
            else if(dataType.equals(PIC_FLAG)) {
                object = cursor.getBlob(4);
            }
            else if(dataType.equals(NAME_USER)) {
                object = cursor.getString(1);
            }
            else if(dataType.equals(NAME_FLAG)) {
                object = cursor.getString(3);
            }

        }
        cursor.close();
       return object;
    }

}
