package com.zhkvdm.myflowmeter;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class GasFlowmeterActivity extends AppCompatActivity/*, MainActivity */ implements ResultsDialogFragment.ResultDialogListener {

    public static final String LOG_TAG = "myLog";

    protected PDFDocument PDFDocumentAdapter;
    protected DatabaseHelper mDatabaseHelper;

    String
            environ,
            method,
            result;

    final double Ts = 293.15; // Стандартная температура среды, К
    final double Ps = 0.101325; // Стандартное абсолютное давление среды, МПа

    int RecalculationType;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    //double P_MPa; // Абсолютное давление среды, МПа (диапазон 0,1 - 7,5 МПа)
    double T; // Абсолютная (термодинамическая) температура среды, К
    double Xa; // Молярная доля азота, молярные % (диапазон 0 - 0,2)
    double Xy; // Молярная доля диоксида углерода, молярные % (диапазон 0 - 0,2)
    double H; // Высшая теплота сгорания газа, МДж/куб.м (диапазон 20 - 48)
    double CompressFactor; // К-т сжимаемости для ввода
    double TemperatureSC; // Стандартная температура среды
    double PressureSC; // Стандартная плотность среды

    // Список входных каналов:
    double XaPrimary;// Молярная доля азота, % (диапазон 0 - 20)
    double XyPrimary;// Молярная доля диоксида углерода, % (диапазон 0 - 20)
    double tc;// Температура среды, градусы С (диапазон минус 23,15 - 76,85)
    double RO;// Плотность среды, кг/куб.м (диапазон 0,66 - 1,05)
    double ROs;// Плотность среды в СУ, кг/куб.м
    double PAbsolute;// Абсолютное давление среды, Па (диапазон 0,1 - 7,5 МПа)
    double PAtmospheric;// Атмосферное давление
    double PRedundant;// Избыточное давление среды
    double Qv;// Объемный расход среды при рабочих условиях, куб.м/ч

    // Список выходных каналов:
    double K;// Коэффициент сжимаемости газа по ГОСТ Р 8.740
    double Kad;// Показатель адиабаты природного газа по ГОСТ 30319.2-2015
    double Qc;// Объемный расход среды, приведенный к С.У., куб.м/ч

    private CheckBox checkBoxAbsolute;

    private LinearLayout
            LinearLayoutAbsolutePressure,
            LinearLayoutUNAbsolutePressure,
            LinearLayoutT_PT_PTZ_Recalculation,
            LinearLayoutT_PT_Recalculation,
            LinearLayoutPTZ_Recalculation,
            LinearLayoutRO_Recalculation;

    private Spinner
            spinnerRecalculationType,
            spinnerTemperatureDimension,
            spinnerTemperatureSC,
            spinnerVolumeFlowDimension,
            spinnerAbsolutePressureDimension,
            spinnerAtmosphericPressureDimension,
            spinnerRedundantPressureDimension,
            spinnerPressureSC;

    private EditText
            editTextTemperature,
            editTextDensity,
            editTextVolumeFlow,
            editTextNitrogen,
            editTextCarbon,
            editTextAbsolutePressure,
            editTextAtmosphericPressure,
            editTextRedundantPressure,
            editTextCompressFactor,
            editTextTemperatureSC,
            editTextPressureSC,
            editTextDensity_RORec,
            editTextDensitySC_RORec;

    // Обработка нажатия кнопки Home в ActionBar'е
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_flowmeter);

        // создание документа PDF
        PDFDocumentAdapter = new PDFDocument(getApplicationContext());
        // работа с БД
        mDatabaseHelper = new DatabaseHelper(this);

        //Настройка ActionBar'а
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.title_activity_gas_flowmeter);

        checkBoxAbsolute = (CheckBox)findViewById(R.id.checkBoxAbsolute);

        LinearLayoutAbsolutePressure = (LinearLayout)this.findViewById(R.id.LinearLayoutAbsolutePressure);
        LinearLayoutUNAbsolutePressure = (LinearLayout)this.findViewById(R.id.LinearLayoutUNAbsolutePressure);
        LinearLayoutT_PT_PTZ_Recalculation = (LinearLayout)this.findViewById(R.id.LinearLayoutT_PT_PTZ_Recalculation);
        LinearLayoutT_PT_Recalculation = (LinearLayout)this.findViewById(R.id.LinearLayoutT_PT_Recalculation);
        LinearLayoutPTZ_Recalculation = (LinearLayout)this.findViewById(R.id.LinearLayoutPTZ_Recalculation);
        LinearLayoutRO_Recalculation = (LinearLayout)this.findViewById(R.id.LinearLayoutRO_Recalculation);

        editTextTemperature = (EditText)findViewById(R.id.editTextTemperature);
        editTextDensity = (EditText)findViewById(R.id.editTextDensity);
        editTextVolumeFlow = (EditText)findViewById(R.id.editTextVolumeFlow);
        editTextNitrogen = (EditText)findViewById(R.id.editTextNitrogen);
        editTextCarbon = (EditText)findViewById(R.id.editTextCarbon);
        editTextAbsolutePressure = (EditText)findViewById(R.id.editTextAbsolutePressure);
        editTextAtmosphericPressure = (EditText)findViewById(R.id.editTextAtmosphericPressure);
        editTextRedundantPressure = (EditText)findViewById(R.id.editTextRedundantPressure);
        editTextCompressFactor = (EditText)findViewById(R.id.editTextCompressFactor);
        editTextTemperatureSC = (EditText)findViewById(R.id.editTextTemperatureSC);
        editTextPressureSC = (EditText)findViewById(R.id.editTextPressureSC);
        editTextDensity_RORec = (EditText)findViewById(R.id.editTextDensity_RORec);
        editTextDensitySC_RORec = (EditText)findViewById(R.id.editTextDensitySC_RORec);


        // Получаем экземпляр элемента spinnerRecalculationType
        spinnerRecalculationType = (Spinner)findViewById(R.id.spinnerRecalculationType);
        // Настраиваем адаптер
        ArrayAdapter<?> adapterRecalculationType =
                ArrayAdapter.createFromResource(this, R.array.RecalculationType, android.R.layout.simple_spinner_item);
        adapterRecalculationType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        spinnerRecalculationType.setAdapter(adapterRecalculationType);

        // Получаем экземпляр элемента spinnerVolumeFlowDimension
        spinnerVolumeFlowDimension = (Spinner)findViewById(R.id.spinnerVolumeFlowDimension);
        // Настраиваем адаптер
        ArrayAdapter<?> adapterVolumeFlowDimension =
                ArrayAdapter.createFromResource(this, R.array.VolumeFlowDimension, android.R.layout.simple_spinner_item);
        adapterVolumeFlowDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        spinnerVolumeFlowDimension.setAdapter(adapterVolumeFlowDimension);

        // Получаем экземпляр элемента spinnerTemperatureDimension
        spinnerTemperatureDimension = (Spinner)findViewById(R.id.spinnerTemperatureDimension);
        // Получаем экземпляр элемента spinnerTemperatureDimension
        spinnerTemperatureSC = (Spinner)findViewById(R.id.spinnerTemperatureSC);
        // Настраиваем адаптер
        ArrayAdapter<?> adapterTemperatureDimension =
                ArrayAdapter.createFromResource(this, R.array.TemperatureDimension, android.R.layout.simple_spinner_item);
        adapterTemperatureDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        spinnerTemperatureDimension.setAdapter(adapterTemperatureDimension);
        // Вызываем адаптер
        spinnerTemperatureSC.setAdapter(adapterTemperatureDimension);

        // Получаем экземпляр элемента spinnerAbsolutePressureDimension
        spinnerAbsolutePressureDimension = (Spinner)findViewById(R.id.spinnerAbsolutePressureDimension);
        // Получаем экземпляр элемента spinnerAtmosphericPressureDimension
        spinnerAtmosphericPressureDimension = (Spinner)findViewById(R.id.spinnerAtmosphericPressureDimension);
        // Получаем экземпляр элемента spinnerRedundantPressureDimension
        spinnerRedundantPressureDimension = (Spinner)findViewById(R.id.spinnerRedundantPressureDimension);
        // Получаем экземпляр элемента spinnerRedundantPressureDimension
        spinnerPressureSC = (Spinner)findViewById(R.id.spinnerPressureSC);
        // Настраиваем адаптер
        ArrayAdapter<?> adapterPressureDimension =
                ArrayAdapter.createFromResource(this, R.array.PressureDimension, android.R.layout.simple_spinner_item);
        adapterPressureDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        spinnerAbsolutePressureDimension.setAdapter(adapterPressureDimension);
        // Вызываем адаптер
        spinnerAtmosphericPressureDimension.setAdapter(adapterPressureDimension);
        // Вызываем адаптер
        spinnerRedundantPressureDimension.setAdapter(adapterPressureDimension);
        // Вызываем адаптер
        spinnerPressureSC.setAdapter(adapterPressureDimension);


        checkBoxAbsolute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LinearLayoutAbsolutePressure.setVisibility(View.VISIBLE);
                    LinearLayoutUNAbsolutePressure.setVisibility(View.INVISIBLE);
                }
                else{
                    LinearLayoutAbsolutePressure.setVisibility(View.INVISIBLE);
                    LinearLayoutUNAbsolutePressure.setVisibility(View.VISIBLE);
                }

            }
        });

        spinnerRecalculationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                RecalculationType = position;

                switch(position) {
                    case (0): // P-пересчет
                        LinearLayoutT_PT_PTZ_Recalculation.setVisibility(View.VISIBLE);
                        LinearLayoutRO_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutPTZ_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutT_PT_Recalculation.setVisibility(View.VISIBLE);
                        break;
                    case(1): // PT-пересчет
                        LinearLayoutT_PT_PTZ_Recalculation.setVisibility(View.VISIBLE);
                        LinearLayoutRO_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutPTZ_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutT_PT_Recalculation.setVisibility(View.VISIBLE);
                        break;
                    case(2): //PTZ-пересчет
                        LinearLayoutT_PT_PTZ_Recalculation.setVisibility(View.VISIBLE);
                        LinearLayoutRO_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutPTZ_Recalculation.setVisibility(View.VISIBLE);
                        LinearLayoutT_PT_Recalculation.setVisibility(View.INVISIBLE);
                        break;
                    case(3): // Пересчет по плотности
                        LinearLayoutT_PT_PTZ_Recalculation.setVisibility(View.INVISIBLE);
                        LinearLayoutRO_Recalculation.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBoxAbsolute.setChecked(true);
        spinnerRecalculationType.setSelection(2);
        spinnerVolumeFlowDimension.setSelection(1);
        spinnerTemperatureDimension.setSelection(0);
        spinnerAbsolutePressureDimension.setSelection(2);
        spinnerAtmosphericPressureDimension.setSelection(2);
        spinnerRedundantPressureDimension.setSelection(2);

    }

    // Обработка нажатия на кнопку ОК в диалоге вывода результата
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(LOG_TAG, "onDialogPositiveClick from GasFlowmeterActivity");
    }
    // Обработка нажатия на кнопку PDF в диалоге вывода результата
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        PDFDocumentAdapter.createPDF(getString(R.string.title_activity_gas_flowmeter), "P-пересчет", result, MainActivity.getDateTime());
    }


    // Обработка нажатия на кнопку ButtonCalculate
    public void onButtonCalculateClick(View view)
    {
        // выводим сообщение
        //Toast.makeText(this, "onButtonCalculateClick", Toast.LENGTH_SHORT).show();


        if(editTextVolumeFlow.getText().length() != 0) {
            Qv = Double.parseDouble(editTextVolumeFlow.getText().toString());// Объемный расход среды при рабочих условиях, куб.м/ч
            // Проверка единиц измерения (перевод в куб.м/ч)
            switch(spinnerVolumeFlowDimension.getSelectedItemPosition()){
                case(0): // куб.м/с
                    Qv = Qv * 3600;
                    break;
                case(1): // куб.м/ч
                    break;
                case(2): // л/с
                    Qv = Qv * 3.6;
                    break;
                case(3): // л/м
                    Qv = Qv * 0.06;
                    break;
            }
        }
        else{
            Toast.makeText(this, "Поле расхода пустое", Toast.LENGTH_LONG).show();
            return;
        }

        if(RecalculationType != 3) { // P, PT, PTZ - пересчет
            if (editTextTemperature.getText().length() != 0) {
                // Температура среды, градусы С (диапазон минус 23,15 - 76,85)
                tc = Double.parseDouble(editTextTemperature.getText().toString());
                // Проверка единиц измерения (перевод в С)
                switch (spinnerTemperatureDimension.getSelectedItemPosition()) {
                    case (0): // C
                        break;
                    case (1): // K
                        tc = tc - 275.15;
                        break;
                }
            } else {
                Toast.makeText(this, "Поле температуры пустое", Toast.LENGTH_LONG).show();
                return;
            }

            T = 273.15 + tc; // Перевод температуры природного газа из С в К

            if(checkBoxAbsolute.isChecked()) {

                if(editTextAbsolutePressure.getText().length() != 0) {
                    PAbsolute = Double.parseDouble(editTextAbsolutePressure.getText().toString());// Абсолютное давление среды, Па (диапазон 0,1 - 7,5 МПа)
                    // Проверка единиц измерения (перевод в МПа)
                    switch(spinnerAbsolutePressureDimension.getSelectedItemPosition()){
                        case(0): // Па
                            PAbsolute = PAbsolute * 1000000;
                            break;
                        case(1): // кПа
                            PAbsolute = PAbsolute * 1000;
                            break;
                        case(2): // МПа
                            break;
                        case(3): // бар
                            PAbsolute = PAbsolute * 0.1;
                            break;
                        case(4): // кгс/кв.см
                            PAbsolute = PAbsolute * 0.09807;
                            break;
                        case(5): // кгс/кв.м
                            PAbsolute = PAbsolute * 0.000009807;
                            break;
                        case(6): // мм.рт.ст
                            PAbsolute = PAbsolute * 0.0001333;
                            break;
                    }
                }
                else{
                    Toast.makeText(this, "Поле давления пустое", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            else {
                if(editTextAtmosphericPressure.getText().length() != 0) {
                    PAtmospheric = Double.parseDouble(editTextAtmosphericPressure.getText().toString());// Атмосферное давление
                    // Проверка единиц измерения (перевод в МПа)
                    switch(spinnerAtmosphericPressureDimension.getSelectedItemPosition()){
                        case(0): // Па
                            PAtmospheric = PAtmospheric * 1000000;
                            break;
                        case(1): // кПа
                            PAtmospheric = PAtmospheric * 1000;
                            break;
                        case(2): // МПа
                            break;
                        case(3): // бар
                            PAtmospheric = PAtmospheric * 0.1;
                            break;
                        case(4): // кгс/кв.см
                            PAtmospheric = PAtmospheric * 0.09807;
                            break;
                        case(5): // кгс/кв.м
                            PAtmospheric = PAtmospheric * 0.000009807;
                            break;
                        case(6): // мм.рт.ст
                            PAtmospheric = PAtmospheric * 0.0001333;
                            break;
                    }
                }
                else{
                    Toast.makeText(this, "Поле атмосферного давления пустое", Toast.LENGTH_LONG).show();
                    return;
                }
                if(editTextRedundantPressure.getText().length() != 0) {
                    PRedundant = Double.parseDouble(editTextRedundantPressure.getText().toString());// Избыточное давление среды
                    // Проверка единиц измерения (перевод в МПа)
                    switch(spinnerRedundantPressureDimension.getSelectedItemPosition()){
                        case(0): // Па
                            PRedundant = PRedundant * 1000000;
                            break;
                        case(1): // кПа
                            PRedundant = PRedundant * 1000;
                            break;
                        case(2): // МПа
                            break;
                        case(3): // бар
                            PRedundant = PRedundant * 0.1;
                            break;
                        case(4): // кгс/кв.см
                            PRedundant = PRedundant * 0.09807;
                            break;
                        case(5): // кгс/кв.м
                            PRedundant = PRedundant * 0.000009807;
                            break;
                        case(6): // мм.рт.ст
                            PRedundant = PRedundant * 0.0001333;
                            break;
                    }
                }
                else{
                    Toast.makeText(this, "Поле избыточного давления пустое", Toast.LENGTH_LONG).show();
                    return;
                }
                PAbsolute = PAtmospheric + PRedundant;
            }

        }
        else{ // Пересчет по плотности
            if (editTextDensity_RORec.getText().length() != 0) {
                // Плотность среды, кг/куб.м
                RO = Double.parseDouble(editTextDensity_RORec.getText().toString());
            } else {
                Toast.makeText(this, "Поле плотности пустое", Toast.LENGTH_LONG).show();
                return;
            }

            if (editTextDensitySC_RORec.getText().length() != 0) {
                // Плотность среды в СУ, кг/куб.м
                ROs = Double.parseDouble(editTextDensitySC_RORec.getText().toString());
            } else {
                Toast.makeText(this, "Поле плотности в СУ пустое", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(RecalculationType == 2) { // PTZ - пересчет
            if (editTextDensity.getText().length() != 0) {
                RO = Double.parseDouble(editTextDensity.getText().toString());// Плотность среды, кг/куб.м (диапазон 0,66 - 1,05)
            } else {
                Toast.makeText(this, "Поле плотности пустое", Toast.LENGTH_LONG).show();
                return;
            }
            if (editTextNitrogen.getText().length() != 0) {
                XaPrimary = Double.parseDouble(editTextNitrogen.getText().toString());// Молярная доля азота, % (диапазон 0 - 20)
            } else {
                Toast.makeText(this, "Поле доли азота пустое", Toast.LENGTH_LONG).show();
                return;
            }
            if (editTextCarbon.getText().length() != 0) {
                XyPrimary = Double.parseDouble(editTextCarbon.getText().toString());// Молярная доля диоксида углерода, % (диапазон 0 - 20)
            } else {
                Toast.makeText(this, "Поле доли углерода пустое", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if(RecalculationType == 0 || RecalculationType == 1) { // PT - пересчет; T - пересчет
            if (editTextCompressFactor.getText().length() != 0) {
                CompressFactor = Double.parseDouble(editTextCompressFactor.getText().toString()); // К-т сжимаемости для ввода
            } else {
                Toast.makeText(this, "Поле к-та сжимаемости пустое", Toast.LENGTH_LONG).show();
                return;
            }
            if (editTextTemperatureSC.getText().length() != 0) {
                TemperatureSC = Double.parseDouble(editTextTemperatureSC.getText().toString()); // Стандартная температура среды
                // Проверка единиц измерения (перевод в С)
                switch(spinnerTemperatureSC.getSelectedItemPosition()){
                    case(0): // C
                        break;
                    case(1): // K
                        TemperatureSC = TemperatureSC - 275.15;
                        break;
                }
            } else {
                Toast.makeText(this, "Поле температуры среды в СУ пустое", Toast.LENGTH_LONG).show();
                return;
            }
            if (editTextPressureSC.getText().length() != 0) {
                PressureSC = Double.parseDouble(editTextPressureSC.getText().toString()); // Стандартная плотность среды
            } else {
                Toast.makeText(this, "Поле плотности среды в СУ пустое", Toast.LENGTH_LONG).show();
                return;
            }

        }



        // Проверка диапазонов введенных параметров

        if(RecalculationType == 0) { // T - пересчет
            //Проверка диапазона объемного расхода среды
            if (Qv > 100) {
                Toast.makeText(this, "Значение объемного расхода среды превышает 100 [куб.м/ч]", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона молярной доли азота
            if (CompressFactor <= 0) {
                Toast.makeText(this, "Значение к-та сжимаемости равно нулю", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if(RecalculationType == 1) { // PT - пересчет
            //Проверка диапазона объемного расхода среды
            if (Qv > 1000) {
                Toast.makeText(this, "Значение объемного расхода среды превышает 1000 [куб.м/ч]", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона молярной доли азота
            if (CompressFactor <= 0) {
                Toast.makeText(this, "Значение к-та сжимаемости равно нулю", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(RecalculationType == 2) { // PTZ - пересчет
            Xa = XaPrimary / 100.0; // Перевод молярной доли азота из % в молярные доли (диапазон 0 - 0,2)
            Xy = XyPrimary / 100.0; // Перевод молярной доли диоксида углерода из % в молярные доли (диапазон 0 - 0,2)

            H = 92.819 * (0.51447 * RO + 0.05603 - 0.65689 * Xa - Xy); // Расчет высшей теплоты сгорания газа

            //Проверка допустимых диапазонов входных данных,
            //по п.4.3, табл.1 ГОСТ 30319.2
            //
            //Проверка диапазона молярной доли азота
            if ((XaPrimary < 0) || (XaPrimary > 20)) {
                Toast.makeText(this, "Значение молярной доли азота вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона молярной доли диоксида углерода
            if ((XyPrimary < 0) || (XyPrimary > 20)) {
                Toast.makeText(this, "Значение молярной доли диоксида углерода вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона плотности среды
            if ((RO < 0.66) || (RO > 1.05)) {
                Toast.makeText(this, "Значение плотности среды вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }

            //Проверка диапазона высшей теплоты сгорания газа
            if ((H < 20) || (H > 48)) {
                Toast.makeText(this, "Значение высшей теплоты сгорания газа вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if(RecalculationType == 3) { //Пересчет по плотности
            //Проверка диапазона плотности
            if (RO <= 0) {
                Toast.makeText(this, "Значение плотности среды равно нулю", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона плотности в СУ
            if (ROs <= 0) {
                Toast.makeText(this, "Значение плотности среды в СУ равно нулю", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else{
            //Проверка диапазона температуры среды
            if ((tc < (-23.15)) || (tc > 76.85)) {
                Toast.makeText(this, "Значение температуры среды вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }
            //Проверка диапазона абсолютного давления сре-ды
            if ((PAbsolute < 0.1) || (PAbsolute > 7.5)) {
                Toast.makeText(this, "Значение абсолютного давления среды вне диапазона", Toast.LENGTH_LONG).show();
                return;
            }
        }


        // Вызов функции расчета
        Calculate(RecalculationType);

        // Формирование строки результата
        switch(RecalculationType) {
            case 0: // T - пересчет
            case 1: //  PT - пересчет
                result = "Объемный расход\n" + String.format("%.2f", Qc) + " [куб.м/ч]" +
                        "\n\nКоэф-т сжимаемости\n" + String.format("%.6f", K);
                break;
            case 2: //  PT - пересчет
                result = "Объемный расход\n" + String.format("%.2f", Qc) + " [куб.м/ч]" +
                        "\n\nКоэф-т сжимаемости\n" + String.format("%.6f", K) +
                        "\n\nПоказатель адиабаты\n" + String.format("%.6f", Kad);
                break;
            case 3: //  PT - пересчет
                result = "Объемный расход\n" + String.format("%.2f", Qc) + " [куб.м/ч]";
                break;
        }

        // Создание диалога для вывода результата
        DialogFragment dialog = new ResultsDialogFragment();
        // Настройка заголовка и содержимого диалога
        ((ResultsDialogFragment) dialog).setBuilder(getString(R.string.gas_name), result);
        // Вызов диалога
        dialog.show(getSupportFragmentManager(), "ResultDialogFragment");
        // Добавление записи в БД
        mDatabaseHelper.insertData(getString(R.string.title_activity_gas_flowmeter), "ГОСТ Р 8.740",  result, MainActivity.getDateTime());
/*
        // Инициализация диалога для вывода результата
        AlertDialog.Builder builder = new AlertDialog.Builder(GasFlowmeterActivity.this);
        builder.setTitle(R.string.gas_name)
                .setMessage(result + "\n")
                //.setIcon(R.drawable.ic_android_cat)
                //.setCancelable(false)
                .setNeutralButton("Сохранить в PDF",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //dialog.cancel();

                                PDFDocumentAdapter.createPDF(getString(R.string.title_activity_gas_flowmeter), "P-пересчет", result, MainActivity.getDateTime());
                            }
                        })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //(String environ, String recalculation_type, String result, String date
                                mDatabaseHelper.insertData(getString(R.string.title_activity_gas_flowmeter), "P-пересчет",  result, MainActivity.getDateTime());
                                //saveResult();
                                //dialog.cancel();
                            }
                        });
        // Создание диалога для вывода результата
        builder.create().show();
        */

        //String TextTemperature = editTextTemperature.getText().toString();
        //double Temperature = Double.parseDouble(TextTemperature);
        //String TextTemperature2 = String.valueOf(Temperature);
        //Toast.makeText(this, TextTemperature2, Toast.LENGTH_SHORT).show();
    }

    public void saveLastResult () {

    }

    private void Calculate(int RecalculationType)
    {
        //Toast.makeText(this, String.valueOf(T), Toast.LENGTH_SHORT).show();

        switch(RecalculationType) {
            case 0: // T - пересчет
            case 1: //  PT - пересчет
                K = CompressFactor; // Коэффициент сжимаемости газа
                Qc = (Qv * PAbsolute * Ts * K) / (T * Ps); //  Вычисление объемного расхода среды
                break;

            case 2: // PTZ - пересчет

                double Bm; // Коэффициент уравнения состояния по ГОСТ 30319.2-2015
                double b, B0, B1, B2, B3, B23, Bzv; // Переменные для рассчетов в ГОСТ 30319.2-2015
                double Cm; // Коэффициент уравнения состояния по ГОСТ 30319.2-2015
                double C0, C1, C2, C3, C223, C233, Czv; // Переменные для рассчетов в ГОСТ 30319.2-2015
                double He; // Теплота сгорания эквивалентного углеводорода по ГОСТ 30319.2-2015
                double M; // Молярная масса эквивалентного углеводорода по ГОСТ 30319.2-2015
                double xe; // Молярная доля эквивалентного углеводорода по ГОСТ 30319.2-2015
                double Z; // Коэффициент сжимаемости газа по ГОСТ 30319.2-2015
                double Zc; // Коэффициент сжимаемости при СУ по ГОСТ 30319.2-2015

                double A0, A1, A2; // Переменные для рассчетов в ГОСТ 30319.2-2015

                xe = 1.0 - Xa - Xy; // ф.(4) ГОСТ 30319.2-2015 -  Молярная доля эквивалентного углеводорода

                Zc = 1.0 - Math.pow((0.0741 * RO - 0.006 - 0.063 * Xa - 0.0575 * Xy), 2.0); // ф.(18) ГОСТ 30319.2-2015
                M = (24.05525 * Zc * RO - 28.0135 * Xa - 44.01 * Xy)/xe; // ф.(17) ГОСТ 30319.2-2015
                He = 128.64 + 47.479 * M; // ф.(16) ГОСТ 30319.2-2015

                B1 = -0.425468 + 2.865 * Math.pow(10.0, -3.0) * T - 4.62073 * Math.pow(10.0, -6.0) * Math.pow(T, 2.0) +
                        (8.77118 * Math.pow(10.0, -4.0) - 5.56281 * Math.pow(10.0, -6.0) * T + 8.81514 * Math.pow(10.0, -9.0) * Math.pow(T, 2.0)) * He +
                        (-8.24747 * Math.pow(10.0, -7.0) + 4.31436 * Math.pow(10.0, -9.0) * T - 6.08319 * Math.pow(10.0, -12.0) * Math.pow(T, 2.0)) * Math.pow(He, 2.0); // ф.(5) ГОСТ 30319.2-2015

                B2 = -0.1446 + 7.4091 * Math.pow(10.0, -4.0) * T - 9.1195 * Math.pow(10.0, -7.0) * Math.pow(T, 2.0); // ф.(6) ГОСТ 30319.2-2015

                B23 = -0.339693 + 1.61176 * Math.pow(10.0, -3.0) * T - 2.04429 * Math.pow(10.0, -6.0) * Math.pow(T, 2.0); // ф.(7) ГОСТ 30319.2-2015

                B3 = -0.86834 + 4.0376 * Math.pow(10.0, -3.0) * T - 5.1657 * Math.pow(10.0, -6.0) * Math.pow(T, 2.0); // ф.(8) ГОСТ 30319.2-2015


                C1 = -0.302488 + 1.95861 * Math.pow(10.0, -3.0) * T - 3.16302 * Math.pow(10.0, -6.0) * Math.pow(T, 2.0) +
                        (6.46422 * Math.pow(10.0, -4.0) - 4.22876 * Math.pow(10.0, -6.0) * T + 6.88157 * Math.pow(10.0, -9.0) * Math.pow(T, 2.0)) * He +
                        (-3.32805 * Math.pow(10.0, -7.0) + 2.2316 * Math.pow(10.0, -9.0) * T - 3.67713 * Math.pow(10.0, -12.0) * Math.pow(T, 2.0)) * Math.pow(He, 2.0); // ф.(9) ГОСТ 30319.2-2015

                C2 = 7.8498 * Math.pow(10.0, -3.0) - 3.9895 * Math.pow(10.0, -5.0) * T + 6.1187 * Math.pow(10.0, -8.0) * Math.pow(T, 2.0); // ф.(10) ГОСТ 30319.2-2015

                C3 = 2.0513 * Math.pow(10.0, -3.0) + 3.4888 * Math.pow(10.0, -5.0) * T - 8.3703 * Math.pow(10.0, -8.0) * Math.pow(T, 2.0); // ф.(11) ГОСТ 30319.2-2015

                C223 = 5.52066 * Math.pow(10.0, -3.0) - 1.68609 * Math.pow(10.0, -5.0) * T + 1.57169 * Math.pow(10.0, -8.0) * Math.pow(T, 2.0); // ф.(12) ГОСТ 30319.2-2015

                C233 = 3.58783 * Math.pow(10.0, -3.0) + 8.06674 * Math.pow(10.0, -6.0) * T - 3.25798 * Math.pow(10.0, -8.0) * Math.pow(T, 2.0); // ф.(13) ГОСТ 30319.2-2015

                Bzv = 0.72 + 1.875 * Math.pow(10.0, -5.0) * Math.pow((320.0 - T), 2.0); // ф.(14) ГОСТ 30319.2-2015
                Czv = 0.92 + 0.0013 * (T - 270.0); // ф.(15) ГОСТ 30319.2-2015

                Bm = Math.pow(xe, 2.0) * B1 + xe * Xa * Bzv * (B1 + B2) - 1.73 * xe * Xy * Math.pow((B1 * B3), 0.5) +
                        Math.pow(Xa, 2.0) * B2 + 2.0 * Xa * Xy * B23 + Math.pow(Xy, 2.0) * B3; // ф.(2) ГОСТ 30319.2-2015

                Cm = Math.pow(xe, 3.0) * C1 + 3.0 * Math.pow(xe, 2.0) * Xa * Czv * Math.pow((Math.pow(C1, 2.0) * C2), (1.0/3.0)) +
                        2.76 * Math.pow(xe, 2.0) * Xy * Math.pow((Math.pow(C1, 2.0) * C3), (1.0/3.0)) +
                        3.0 * xe * Math.pow(Xa, 2.0) * Czv * Math.pow((C1 * Math.pow(C2, 2.0)), (1.0/3.0)) +
                        6.6 * xe * Xa * Xy * Math.pow((C1 * C2 * C3), (1.0/3.0)) +
                        2.76 * xe * Math.pow(Xy, 2.0) * Math.pow((C1 * Math.pow(C3, 2.0)), (1.0/3.0)) +
                        Math.pow(Xa, 3.0) * C2 + 3.0 * Math.pow(Xa, 2.0) * Xy * C223 +
                        3.0 * Xa * Math.pow(Xy, 2.0) * C233 + Math.pow(Xy, 3.0) * C3; // ф.(3) ГОСТ 30319.2-2015

                b = Math.pow(10.0, 3.0) * PAbsolute / (2.7715 * T); // ф.(25) ГОСТ 30319.2-2015

                B0 = b * Bm; // ф.(23) ГОСТ 30319.2-2015

                C0 = Math.pow(b, 2.0) * Cm; // ф.(24) ГОСТ 30319.2-2015

                A0 = 1.0 + 1.5 * (B0 + C0); // ф.(22) ГОСТ 30319.2-2015

                A1 = 1.0 + B0; // ф.(20) ГОСТ 30319.2-2015

                A2 = Math.pow((A0 - Math.pow((Math.pow(A0, 2.0) - Math.pow(A1, 3.0)), 0.5) ), (1.0/3.0)); // ф.(21) ГОСТ 30319.2-2015

                Z = (1.0 + A2 + A1/A2)/3.0; // ф.(19) ГОСТ 30319.2-2015 -  Вычисление коэффициента сжимаемости газа по ГОСТ 30319.2-2015
                K = Z / Zc; // Вычисление коэффициента сжимаемости газа по ГОСТ Р 8.740
                Kad = 1.556 * ( 1.0 + 0.074 * Xa) - 3.9 * Math.pow(10.0, -4) * T * (1.0 - 0.68 * Xa) - 0.208 * RO +
                        Math.pow((PAbsolute/T), 1.43) * (384.0 * (1.0 - Xa) * Math.pow((PAbsolute/T), 0.8) + 26.4 * Xa); // ф.(30) ГОСТ 30319.2-2015 - Вычисление показателя адиабаты природного газа

                Qc = (Qv * PAbsolute * Ts) / (Ps * T * K); //  Вычисление объемного расхода среды по ГОСТ Р 8.740
                break;
            case 3: // Пересчет по плотнисти
                Qc = (Qv * RO) / ROs; //  Вычисление объемного расхода среды
                break;
        }


    }
}
