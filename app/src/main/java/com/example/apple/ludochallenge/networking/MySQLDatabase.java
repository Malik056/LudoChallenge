package com.example.apple.ludochallenge.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.ludochallenge.GameType;
import com.example.apple.ludochallenge.UserProgressData;
import com.example.apple.ludochallenge.Versus;
import com.example.apple.ludochallenge.WinAndLoses;

import java.util.ArrayList;

public class MySQLDatabase extends SQLiteOpenHelper {


    static MySQLDatabase mySQLDatabase;
    public static String TABLE_NAME = "USERS";
    private static String FLAG_NAME = "FLAG_NAME";
    private static String FLAG_PIC = "FLAG_PIC";
    private static String ID = "ID";
    private static String PROFILE_IMAGE_COL = "IMAGES";
    private static String USER_NAME = "USER_NAME";
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
    public static String FACEBOKK_FRIENDS_DATA_TABLE = "FFDT";
    public static String USER_PROGRESS_TABLE = "USER_PROGRESS";
    public static String GAME_TYPE_COL = "GAME_TYPE";
    public static String SNAKES_AND_LADDERS = "Snake_and_Ladders";
    public static String LUDO_CHALLENGE = "LUDO_CHALLENGE";
    public static String VS_COMPUTER_OR_MULTIPLAYER_COL = "VS_COMPUTER_OR_MULTIPLAYER";
    public static String VS_COMPUTER = "VS_COMPUTER";
    public static String VS_MULTIPLAYTER = "VS_MULTIPLAYER";
    public static String WINS_COL = "WINS";
    public static String LOSES_COL = "LOSES";
    public static String COINS_COL = "COINS";
    public static String USER_ID = "USER_ID";

    public static MySQLDatabase getInstance(Context context)
    {
        if(mySQLDatabase == null)
        {
            return new MySQLDatabase(context);
        }
        else return mySQLDatabase;
    }

    public MySQLDatabase(Context context) {
        super(context, "LUDO_CHALLENGE_DATABASE", null, 1);

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

        queuryData("CREATE TABLE IF NOT EXISTS " + FACEBOKK_FRIENDS_DATA_TABLE + "(" +
                ID + " VARCHAR PRIMARY KEY ,"+
                USER_NAME + " VARCHAR, " +
                PROFILE_IMAGE_COL + " BLOG)");

        queuryData("CREATE TABLE IF NOT EXISTS " + USER_PROGRESS_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                USER_ID + " VARCHAR, " +
                GAME_TYPE_COL + " VARCHAR, " +
                VS_COMPUTER_OR_MULTIPLAYER_COL + " VARCHAR, " +
                WINS_COL + " VARCHAR, " +
                LOSES_COL + " VARCHAR, " +
                COINS_COL + " VARCHAR "+
                ")");

    }

    public void setCurrentSession(String ID, String logInAs)
    {
        queuryData("DROP TABLE IF EXISTS " + LOGINAS);
        queuryData("CREATE TABLE IF NOT EXISTS " + LOGINAS + "( " +
                MySQLDatabase.ID + " VARCHAR PRIMARY KEY, " +
                LOGIN_STATUS + " VARCHAR  )"
        );

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + LOGINAS + " VALUES (?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(2, logInAs);
        statement.bindString(1, ID);
        statement.executeInsert();
    }

    public String fetchCurrentLoggedInStatus(){
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

    public String fetchCurrentLoggedInID()
    {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query
                (
                        LOGINAS,
                        new String[] {ID},
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

    private void queuryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String name, byte[] image, byte[] flag_image, String country_name, String idOrEmail, String TABLE_NAME){

        SQLiteDatabase database = getReadableDatabase();

        String ID = MySQLDatabase.ID;

        Cursor cursor;

        if(!TABLE_NAME.equals(MySQLDatabase.TABLE_NAME))
        {
            ID = MySQLDatabase.EMAIL;
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

    public void insertData(String name, byte[] image, String idOrEmail, String TABLE_NAME){

        SQLiteDatabase database = getReadableDatabase();



        String ID;
        if(TABLE_NAME.equals(FACEBOOK_USER_TABLE)){
            ID = EMAIL;
        }
        else{
            ID = MySQLDatabase.ID;
        }
        Cursor cursor;

        cursor = database.query
                (
                        TABLE_NAME,
                        new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL},
                        ID + " =?",
                        new String[]{idOrEmail}, null, null, null, null
                );

        SQLiteDatabase database1 = getWritableDatabase();
        if(cursor.moveToFirst()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_NAME, name);
            contentValues.put(PROFILE_IMAGE_COL, image);
            database1.update(TABLE_NAME, contentValues, ID + "=?", new String[]{idOrEmail});
        }
        else {

            if(TABLE_NAME.equals(FACEBOKK_FRIENDS_DATA_TABLE)) {

                String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?)";
                SQLiteStatement statement = database1.compileStatement(sql);

                statement.clearBindings();
                statement.bindString(2, name);
                statement.bindString(1, idOrEmail);
                statement.bindBlob(3, image);
                statement.executeInsert();
            }
            else{
                String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ? , ?)";
                SQLiteStatement statement = database1.compileStatement(sql);

                statement.clearBindings();
                statement.bindString(2, name);
                statement.bindString(1, idOrEmail);
                statement.bindBlob(3, image);
                statement.bindString(4, name);
                statement.bindBlob(5, image);
                statement.executeInsert();
            }
        }
        cursor.close();
    }

    public void insertGameProgressData(String idOrEmail, String gameType, String vs_computer_or_multiplayer, String wins, String loses, String coins){

        SQLiteDatabase database = getReadableDatabase();


        Cursor cursor;

        cursor = database.query
                (
                        USER_PROGRESS_TABLE,
                        new String[] { USER_ID, GAME_TYPE_COL, VS_COMPUTER_OR_MULTIPLAYER_COL, WINS_COL, LOSES_COL, COINS_COL},
                        USER_ID + "=? AND " + VS_COMPUTER_OR_MULTIPLAYER_COL + "=? AND " + GAME_TYPE_COL + " =?" ,
                        new String[]{idOrEmail, vs_computer_or_multiplayer, gameType} ,
                        null, null, null, null
                );

        SQLiteDatabase database1 = getWritableDatabase();
        if(cursor.moveToFirst()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(WINS_COL, wins);
            contentValues.put(LOSES_COL, loses);
            contentValues.put(COINS_COL, coins);

            database1.update(USER_PROGRESS_TABLE, contentValues, USER_ID + "=? AND " + VS_COMPUTER_OR_MULTIPLAYER_COL + "=? AND " + GAME_TYPE_COL + " =?" , new String[]{idOrEmail, vs_computer_or_multiplayer, gameType});
        }
        else {

            String sql = "INSERT INTO " + USER_PROGRESS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = database1.compileStatement(sql);

            statement.clearBindings();
            statement.bindString(3, gameType);
            statement.bindString(2, idOrEmail);
            statement.bindString(4, vs_computer_or_multiplayer);
            statement.bindString(5, wins);
            statement.bindString(6, loses);
            statement.bindString(7, coins);
            statement.executeInsert();
        }
        cursor.close();
    }

    public ArrayList<UserProgressData> getUserProgressData(String ID)
    {
        ArrayList<UserProgressData> userProgressData = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(USER_PROGRESS_TABLE,
                new String[]{USER_ID, GAME_TYPE_COL, VS_COMPUTER_OR_MULTIPLAYER_COL, WINS_COL, LOSES_COL, COINS_COL},
                USER_ID + " =?", new String[]{ID},null, null, null);

        while(cursor.moveToNext())
        {
            String wins = cursor.getString(3);
            String loses = cursor.getString(4);

            WinAndLoses winAndLoses = new WinAndLoses(wins,loses);

            String gameType = cursor.getString(1);
            String vs = cursor.getString(2);
            Versus versus = new Versus(winAndLoses, vs);
            GameType gameType1 = new GameType(versus,gameType);
            UserProgressData tempUserProgressData = new UserProgressData(gameType1, cursor.getString(0), cursor.getString(5));
            userProgressData.add(tempUserProgressData);
        }
        cursor.close();
        return userProgressData;

    }

    public Object getData(String username, String dataType, String TABLE_NAME){
        SQLiteDatabase database = getReadableDatabase();

        String ID = MySQLDatabase.ID;
        Cursor cursor;
        if(!TABLE_NAME.equals(MySQLDatabase.TABLE_NAME))
        {
            ID = MySQLDatabase.EMAIL;
            cursor = database.query
                    (
                            TABLE_NAME,
                            new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL, FLAG_NAME, FLAG_PIC},
                            ID + " = ?",
                            new String[]{username}, null, null, null, null
                    );

        }
        else {
            cursor = database.query
                    (
                            TABLE_NAME,
                            new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL, FLAG_NAME, FLAG_PIC},
                            ID + " = ?",
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

    public ArrayList<FacebookUser> getFacebookFriendList()
    {
        ArrayList<FacebookUser> users  = new ArrayList<>();

        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.query
                (
                        FACEBOKK_FRIENDS_DATA_TABLE,
                        new String[] { ID, USER_NAME ,PROFILE_IMAGE_COL},
                        null,
                        null, null, null, null, null
                );

        while(cursor.moveToNext())
        {

            Bitmap bitmap;

            byte[] image = cursor.getBlob(2);

            bitmap = BitmapFactory.decodeByteArray(image,0, image.length);

            FacebookUser user = new FacebookUser(cursor.getString(0),cursor.getString(1),bitmap);

            users.add(user);
        }

        return users;

    }


}
