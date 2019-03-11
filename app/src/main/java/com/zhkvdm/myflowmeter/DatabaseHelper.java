package com.zhkvdm.myflowmeter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;

import com.zhkvdm.myflowmeter.DataBaseContract.StoryTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    //protected DatabaseHelper mDatabaseHelper;
    protected SQLiteDatabase mSQLiteDatabase;
    Cursor cursor;
    Context mContext;

    public static final String LOG_TAG = "myLog";

    // имя базы данных
    private static final String DATABASE_NAME = "MyFlowmeterDB.db";
    // версия базы данных
    private static final int DATABASE_VERSION = 1;

    // скрипт создания таблицы
    private static final String DATABASE_TABLE_CREATE_SCRIPT = "CREATE TABLE " + StoryTable.TABLE_NAME + " ("
            + DataBaseContract.StoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "// id integer primary key autoincrement
            + StoryTable.COLUMN_ENVIRON + " TEXT NOT NULL, "
            + StoryTable.COLUMN_METHOD + " TEXT NOT NULL, "
            + StoryTable.COLUMN_RESULT + " TEXT NOT NULL, "
            + StoryTable.COLUMN_DATETIME + " TEXT NOT NULL"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    // вызывается при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // запишем в журнал
        Log.w(LOG_TAG, "Собираемя создать базу данных");
        db.execSQL(DATABASE_TABLE_CREATE_SCRIPT);
        // запишем в журнал
        Log.w(LOG_TAG, "Создали базу данных");

        //mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // запишем в журнал
        Log.w(LOG_TAG, "Обновляем базу данных с версии " + oldVersion + " на версию " + newVersion);

        // удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + StoryTable.TABLE_NAME);
        // создаём новую таблицу
        onCreate(db);
    }

    // Закрытие базы данных
    public void closeDatabase() {
        mSQLiteDatabase.close();
    }

    public boolean insertData(String environ, String method, String result, String date) {
        // запишем в журнал
        Log.w(LOG_TAG, "Собираемя записать данные в БД");
        int _id = getDatabaseCount();
        // Открытие базы данных для записи
        mSQLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StoryTable._ID, _id + 1);
        contentValues.put(StoryTable.COLUMN_ENVIRON, environ);
        contentValues.put(StoryTable.COLUMN_METHOD, method);
        contentValues.put(StoryTable.COLUMN_RESULT, result);
        contentValues.put(StoryTable.COLUMN_DATETIME, date);
        long rowID = mSQLiteDatabase.insert(StoryTable.TABLE_NAME, null, contentValues);
        closeDatabase();

        if(rowID == -1) {
            Log.w(LOG_TAG, "Ошибка записи данных в БД");
            return false;
        }
        else {
            Log.w(LOG_TAG, "Успешно записали данные в БД -> Всего записей :" + rowID);
            return true;
        }
    }

    protected void dropTable() {
        // запишем в журнал
        Log.w(LOG_TAG, "Удаляем таблицу из БД");
        // Открытие базы данных для чтения
        mSQLiteDatabase = this.getWritableDatabase();
        // удаляем старую таблицу и создаём новую
        mSQLiteDatabase.execSQL("delete from "+ StoryTable.TABLE_NAME);
        // Закрытие базы данных
        closeDatabase();
    }

    protected int getDatabaseCount() {
        // запишем в журнал
        Log.w(LOG_TAG, "Собираемcя считать количество полей в таблице БД");
        // Открытие базы данных для чтения
        mSQLiteDatabase = this.getReadableDatabase();
        // Делаем запрос
        Cursor cursor = mSQLiteDatabase.query(
                StoryTable.TABLE_NAME, // таблица
                null,
                null, // столбцы для условия WHERE
                null, // значения для условия WHERE
                null, // Don't group the rows
                null, // Don't filter by row groups
                null); // порядок сортировки

        // запишем в журнал
        Log.w(LOG_TAG, "Таблица содержит " + cursor.getCount() + " записей");
        //Toast.makeText(mContext, "Таблица содержит " + cursor.getCount() + " записей.\n\n", Toast.LENGTH_SHORT).show();
        // Закрытие базы данных
        closeDatabase();

        return cursor.getCount();
    }

    protected Cursor getAllDatabaseData() {
        // Открытие базы данных для чтения
        mSQLiteDatabase = this.getReadableDatabase();
        // Делаем запрос
        Cursor res = mSQLiteDatabase.query(
                StoryTable.TABLE_NAME, // таблица
                null, // столбцы
                null, // столбцы для условия WHERE
                null, // значения для условия WHERE
                null, // Don't group the rows
                null, // Don't filter by row groups
                null); // порядок сортировки


        //Cursor res = mSQLiteDatabase.rawQuery("Select * from " + StoryTable.TABLE_NAME, null);
        // Закрытие базы данных
        //mSQLiteDatabase.close();
        return res;
    }


    protected void displayDatabaseInfo() {
        // Открытие базы данных для чтения
        mSQLiteDatabase = this.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                StoryTable._ID,
                StoryTable.COLUMN_ENVIRON,
                StoryTable.COLUMN_METHOD,
                StoryTable.COLUMN_RESULT,
                StoryTable.COLUMN_DATETIME };

        // Делаем запрос
        Cursor cursor = mSQLiteDatabase.query(
                StoryTable.TABLE_NAME, // таблица
                null, // столбцы
                null, // столбцы для условия WHERE
                null, // значения для условия WHERE
                null, // Don't group the rows
                null, // Don't filter by row groups
                null); // порядок сортировки

        //TextView displayTextView = (TextView) findViewById(R.id.text_view_info);

        try {
            // выводим сообщение
            //Toast.makeText(mContext, "Таблица содержит " + cursor.getCount() + " записей.\n\n", Toast.LENGTH_SHORT).show();
/*
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(StoryTable._ID);
            int titleColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_TITLE);
            int resultColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_RESULT);
            int datetimeColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_DATETIME);
*/
            int currentID;
            String currentTitle;
            String currentResult;
            String currentDateTime;

            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            //Cursor c = db.query("mytable", null, null, null, null, null, null);

            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (cursor.moveToFirst()) {

                // Узнаем индекс каждого столбца
                int idColumnIndex = cursor.getColumnIndex(StoryTable._ID);
                int environColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_ENVIRON);
                int methodColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_METHOD);
                int resultColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_RESULT);
                int datetimeColumnIndex = cursor.getColumnIndex(StoryTable.COLUMN_DATETIME);

                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Log.d(LOG_TAG,
                            "ID = " + cursor.getInt(idColumnIndex) +
                                    ", environ = " + cursor.getString(environColumnIndex) +
                                    ", method = " + cursor.getString(methodColumnIndex) +
                                    ", result = " + cursor.getString(resultColumnIndex) +
                                    ", datetime = " + cursor.getString(datetimeColumnIndex) );
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (cursor.moveToNext());
            } else
                Log.d(LOG_TAG, "0 rows");
            cursor.close();
/*
            cursor.moveToFirst();
            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                currentID = cursor.getInt(idColumnIndex);
                currentTitle = cursor.getString(titleColumnIndex);
                currentResult = cursor.getString(resultColumnIndex);
                currentDateTime = cursor.getString(datetimeColumnIndex);

                Log.d(LOG_TAG,
                        "ID = " + currentID +
                                ", Title = " + currentTitle +
                                ", Result = " + currentResult +
                                ", DateTime = " + currentDateTime);
                // выводим сообщение
                //Toast.makeText(mContext, currentID + " \n" + currentTitle  + " \n" + currentResult  + " \n" + currentDateTime, Toast.LENGTH_LONG).show();

            }
*/
        } finally {
            //cursor.moveToFirst();
            //Toast.makeText(mContext, String(currentID) + " \n" + currentTitle  + " \n" + currentResult  + " \n" + currentDateTime, Toast.LENGTH_LONG).show();
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
        // Закрытие базы данных
        closeDatabase();
    }
}
