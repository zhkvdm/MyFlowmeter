package com.zhkvdm.myflowmeter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhkvdm.myflowmeter.DataBaseContract.StoryTable;
import com.zhkvdm.myflowmeter.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity /*implements ConfirmationDialogFragment.ConfirmationDialogListener*/ {

    public static final String LOG_TAG = "myLog";

    private LinearLayout
            LinearLayoutNaturalGas,
            LinearLayoutCompressedAir,
            LinearLayoutOther,
            LinearLayoutWater,
            LinearLayoutSteam;

    //Массив строк для передачи в Spiner
    String[] spinnerItemsArray;
    String arrayName = "MeasuredMediums";
    //int[] spinnerIconsArray;
    int spinnerIconsArray[] = {
            R.mipmap.ic_gas,
            R.mipmap.ic_air,
            R.mipmap.ic_other,
            R.mipmap.ic_water,
            R.mipmap.ic_steam};

    private Spinner spinnerMeasuredMedium;

    //protected PDFDocument PDFDocumentAdapter;

    protected DatabaseHelper mDatabaseHelper;
    //protected SQLiteDatabase mSQLiteDatabase;
    //Cursor cursor;

    //private ImageButton ActionBarButton;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Обработка нажатий пунктов меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                Intent i = new Intent(this, HistoryActivity.class);
                startActivity(i);

                //mDatabaseHelper.getDatabaseCount();
                // выводим сообщение
                //Toast.makeText(this, "menu_item1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item2:
                //mDatabaseHelper.dropTable();
                return true;
            //case R.id.delete_database_table:

                //showNoticeDialog();

                //mDatabaseHelper.dropTable();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            //initViews();
        }

        // создание документа PDF
        //PDFDocumentAdapter = new PDFDocument(getApplicationContext());
        // работа с БД
        //mDatabaseHelper = new DatabaseHelper(this);
        //mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        // Настройка вида actionbar'а
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.app_name);
/*
        //Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.main_page_actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.app_name);*/

        //ActionBarButton = (ImageButton)this.findViewById(R.id.ActionBarButton1);

        LinearLayoutNaturalGas = (LinearLayout)this.findViewById(R.id.LinearLayoutNaturalGas);
        LinearLayoutCompressedAir = (LinearLayout)this.findViewById(R.id.LinearLayoutCompressedAir);
        LinearLayoutOther  = (LinearLayout)this.findViewById(R.id.LinearLayoutOther);
        LinearLayoutWater = (LinearLayout)this.findViewById(R.id.LinearLayoutWater);
        LinearLayoutSteam = (LinearLayout)this.findViewById(R.id.LinearLayoutSteam);


        //Конвертация массива R.array.MeasuredMediums в массив String[]
        int arrayName_ID= getResources().getIdentifier(arrayName , "array",this.getPackageName());
        spinnerItemsArray = getResources().getStringArray(arrayName_ID);

        // Получаем экземпляр элемента Spinner
        spinnerMeasuredMedium = (Spinner)findViewById(R.id.spinnerMeasuredMedium);
        // Настраиваем адаптер для Spinner
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), spinnerIconsArray, spinnerItemsArray);
        // Вызываем адаптер  для Spinner
        spinnerMeasuredMedium.setAdapter(customSpinnerAdapter);
        // Настраиваем слушателя для Spinner
        spinnerMeasuredMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position) {
                    case (0): //
                        LinearLayoutNaturalGas.setVisibility(View.VISIBLE);
                        LinearLayoutCompressedAir.setVisibility(View.INVISIBLE);
                        LinearLayoutOther.setVisibility(View.INVISIBLE);
                        LinearLayoutWater.setVisibility(View.INVISIBLE);
                        LinearLayoutSteam.setVisibility(View.INVISIBLE);
                        break;
                    case(1): //
                        LinearLayoutNaturalGas.setVisibility(View.INVISIBLE);
                        LinearLayoutCompressedAir.setVisibility(View.VISIBLE);
                        LinearLayoutOther.setVisibility(View.INVISIBLE);
                        LinearLayoutWater.setVisibility(View.INVISIBLE);
                        LinearLayoutSteam.setVisibility(View.INVISIBLE);
                        break;
                    case(2): //
                        LinearLayoutNaturalGas.setVisibility(View.INVISIBLE);
                        LinearLayoutCompressedAir.setVisibility(View.INVISIBLE);
                        LinearLayoutOther.setVisibility(View.VISIBLE);
                        LinearLayoutWater.setVisibility(View.INVISIBLE);
                        LinearLayoutSteam.setVisibility(View.INVISIBLE);

                        //LinearLayoutRecalculationType.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));


                        break;
                    case(3): //
                        LinearLayoutNaturalGas.setVisibility(View.INVISIBLE);
                        LinearLayoutCompressedAir.setVisibility(View.INVISIBLE);
                        LinearLayoutOther.setVisibility(View.INVISIBLE);
                        LinearLayoutWater.setVisibility(View.VISIBLE);
                        LinearLayoutSteam.setVisibility(View.INVISIBLE);
                        break;
                    case(4): //
                        LinearLayoutNaturalGas.setVisibility(View.INVISIBLE);
                        LinearLayoutCompressedAir.setVisibility(View.INVISIBLE);
                        LinearLayoutOther.setVisibility(View.INVISIBLE);
                        LinearLayoutWater.setVisibility(View.INVISIBLE);
                        LinearLayoutSteam.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinnerMeasuredMedium.setSelection(0);
    }


    public void onActionBarButtonClick(View v) {
        // выводим сообщение
        //Toast.makeText(this, "ActionBarButtonClick", Toast.LENGTH_SHORT).show();

        //PDFDocumentAdapter.createPDF("Some" + System.getProperty ("line.separator") + "Text");
        //Intent i = new Intent(this, SettingsActivity.class);
        //startActivity(i);
    }

    public void onGas8740ButtonClick(View view)
    {
        // выводим сообщение
        //Toast.makeText(this, "onGas8740ButtonClick", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, GasFlowmeterActivity.class);
        startActivity(i);
    }

    public void onAirMR11203ButtonClick(View view)
    {
        // выводим сообщение
        //Toast.makeText(this, "onAirMR11203ButtonClick", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, AirFlowmeterActivity.class);
        startActivity(i);
    }

    public void onGas26672011ButtonClick(View view)
    {
        // выводим сообщение
        Toast.makeText(this, "onGas26672011ButtonClick", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(this, GasAnnubarActivity.class);
        //startActivity(i);
    }

    public void onWaterButtonClick(View view)
    {
        // выводим сообщение
        Toast.makeText(this, "onWaterButtonClick", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(this, WaterActivity.class);
        //startActivity(i);
    }

    public void onSaturatedSteamButtonClick(View view)
    {
        // выводим сообщение
        Toast.makeText(this, "onSaturatedSteamButtonClick", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(this, WaterActivity.class);
        //startActivity(i);
    }

    public void onSuperheatedSteamButtonClick(View view)
    {
        // выводим сообщение
        Toast.makeText(this, "onSuperheatedSteamButtonClick", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(this, WaterActivity.class);
        //startActivity(i);
    }

    // Проверка разрешений для приложения
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //initViews();
        } else {

            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    public static String getDateTime () {
        // текущее время
        Date currentDate = new Date();
        // форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        // форматирование времени как "часы:минуты:секунды"
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        return dateText + " " +timeText;
        //Toast.makeText(context, dateText + " " +timeText, Toast.LENGTH_LONG).show();
    }

/*
    // Отобраэение диалога AllertDialog
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialogFragment();
        ((ConfirmationDialogFragment) dialog).setBuilder("" ,getString(R.string.dialog_delete_database_table));
        dialog.show(getSupportFragmentManager(), "ConfirmationDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getTag() == "ConfirmationDialogFragment") {
            // выводим сообщение
            Log.d(LOG_TAG, "onDialogPositiveClick from MainActivity");
            mDatabaseHelper.dropTable();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog.getTag() == "ConfirmationDialogFragment") {
            // выводим сообщение
            Log.d(LOG_TAG, "onDialogNegativeClick from MainActivity");

        }
    }*/
}
