package com.zhkvdm.myflowmeter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryActivity extends /*AppCompatActivity*/MainActivity implements ResultsDialogFragment.ResultDialogListener, ConfirmationDialogFragment.ConfirmationDialogListener {

    public static final String LOG_TAG = "myLog";
    Cursor cursor;
    ListView listView;

    // Массивы для хранения данных
    ArrayList<String> environArray;
    ArrayList<String> methodArray;
    ArrayList<String> resultArray;
    ArrayList<String> datetimeArray;

    String environ;
    String method;
    String result;
    String dateTime;

    // Настройка меню в ActionBar'е
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history_activity, menu);
        return true;
    }

    // Обработка нажатия кнопки Home в ActionBar'е
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Настройка ActionBar'а
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.history_activity_name);
        // Вызов onCreateOptionsMenu
        invalidateOptionsMenu();

        // Заполнение ListView данными из массивов
        listView = (ListView) findViewById(R.id.idListView);

        // Заполняем ListView данными
        fillListView();

        // Обработка нажатий на элемент ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                environ = environArray.get(position);
                method = methodArray.get(position);
                result = resultArray.get(position);
                dateTime = datetimeArray.get(position);

                // Создание диалога для вывода результата
                DialogFragment dialog = new ResultsDialogFragment();
                // Настройка заголовка и содержимого диалога
                ((ResultsDialogFragment) dialog).setBuilder(environ, result);
                // Вызов диалога
                dialog.show(getSupportFragmentManager(), "ResultDialogFragment");
                //Log.d(LOG_TAG, "itemClick: position = " + position + ", descriptions = " + result);
            }
        });
    }
    public final void clearListView() {
        listView.setAdapter(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_database_table:

                // Создание диалога для вывода результата
                DialogFragment dialog = new ConfirmationDialogFragment();
                // Настройка заголовка и содержимого диалога
                ((ConfirmationDialogFragment) dialog).setBuilder("", getString(R.string.dialog_delete_database_table));
                // Вызов диалога
                dialog.show(getSupportFragmentManager(), "ConfirmationDialogFragment");
                //mDatabaseHelper.dropTable();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Заполняем ListView данными
    public void fillListView() {

        // Инициализация массивов для хранения данных
        environArray = new ArrayList<String>();
        methodArray = new ArrayList<String>();
        resultArray = new ArrayList<String>();
        datetimeArray = new ArrayList<String>();

        // Получаем данные из базы данных
        cursor = mDatabaseHelper.getAllDatabaseData();
        // Заполнение массивов данными из cursor
        fillArrays(cursor);
        // Закрытие базы данных
        mDatabaseHelper.closeDatabase();

        ListViewAdapter adapter = new ListViewAdapter(this, environArray, methodArray, resultArray, datetimeArray);
        listView.setAdapter(adapter);
    }

    // Заполнение массивов данными из cursor
    private void fillArrays(Cursor cursor) {

        try {
            // выводим сообщение
            //Toast.makeText(mContext, "Таблица содержит " + cursor.getCount() + " записей.\n\n", Toast.LENGTH_SHORT).show();

            // Ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (cursor.moveToFirst()) {

                // Узнаем индекс каждого столбца
                int idColumnIndex = cursor.getColumnIndex(DataBaseContract.StoryTable._ID);
                int environColumnIndex = cursor.getColumnIndex(DataBaseContract.StoryTable.COLUMN_ENVIRON);
                int methodColumnIndex = cursor.getColumnIndex(DataBaseContract.StoryTable.COLUMN_METHOD);
                int resultColumnIndex = cursor.getColumnIndex(DataBaseContract.StoryTable.COLUMN_RESULT);
                int datetimeColumnIndex = cursor.getColumnIndex(DataBaseContract.StoryTable.COLUMN_DATETIME);

                do {
                    environArray.add(cursor.getString(environColumnIndex));
                    methodArray.add(cursor.getString(methodColumnIndex));
                    resultArray.add(cursor.getString(resultColumnIndex));
                    datetimeArray.add(cursor.getString(datetimeColumnIndex));
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (cursor.moveToNext());
            } else
                Log.d(LOG_TAG, "База данных пуста");
            cursor.close();

        } finally {
            //cursor.moveToFirst();
            //Toast.makeText(mContext, String(currentID) + " \n" + currentTitle  + " \n" + currentResult  + " \n" + currentDateTime, Toast.LENGTH_LONG).show();
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(LOG_TAG, "onDialogPositiveClick from HistoryActivity");
        if(dialog.getTag() == "ConfirmationDialogFragment") {
            // выводим сообщение
            mDatabaseHelper.dropTable();
            clearListView();
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        Log.d(LOG_TAG, "onDialogNeutralClick from HistoryActivity");
        PDFDocumentAdapter.createPDF(environ, method, result, dateTime);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}

class ListViewAdapter extends ArrayAdapter {
    ArrayList<String> environArray;
    ArrayList<String> methodArray;
    ArrayList<String> descArray;
    ArrayList<String> dateTimeArray;
    public ListViewAdapter(Context context, ArrayList environArr, ArrayList methodArr, ArrayList resultArr, ArrayList dateTimeArr) {
        super(context, R.layout.listview_history, R.id.idEnviron, environArr);
        this.environArray = environArr;
        this.methodArray = methodArr;
        this.descArray = resultArr;
        this.dateTimeArray = dateTimeArr;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.listview_history, parent, false);

        TextView myEnviron = (TextView) row.findViewById(R.id.idEnviron);
        TextView myRecalculationType = (TextView) row.findViewById(R.id.idRecalculationType);
        TextView myDateTime = (TextView) row.findViewById(R.id.idDateTime);

        myEnviron.setText(environArray.get(position));
        myRecalculationType.setText(methodArray.get(position));
        myDateTime.setText(dateTimeArray.get(position));

        return row;
    }
}
