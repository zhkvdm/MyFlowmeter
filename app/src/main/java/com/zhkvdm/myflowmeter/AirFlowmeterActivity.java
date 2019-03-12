package com.zhkvdm.myflowmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//

import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AirFlowmeterActivity extends AppCompatActivity {

    private CheckBox checkBoxAbsolute;

    private LinearLayout
            LinearLayoutAbsolutePressure,
            LinearLayoutUNAbsolutePressure;

    private Spinner
            spinnerTemperatureDimension,
            spinnerVolumeFlowDimension,
            spinnerAbsolutePressureDimension,
            spinnerAtmosphericPressureDimension,
            spinnerRedundantPressureDimension;

    private EditText
            editTextTemperature,
            editTextVolumeFlow,
            editTextAbsolutePressure,
            editTextAtmosphericPressure,
            editTextRedundantPressure;

    String result;

    // Входные переменные
    double tc;// Температура среды, градусы С (диапазон минус 23,15 - 76,85)
    double Qv;// Объемный расход среды при рабочих условиях, куб.м/ч
    double PAbsolute;// Абсолютное давление среды, Па (диапазон 0,1 - 7,5 МПа)
    double PAtmospheric;// Атмосферное давление
    double PRedundant;// Избыточное давление среды

    double
            T, // Абсолютная (термодинамическая) температура среды, К
            p, // Приведенное давление, МПа
            t, // Приведенная температура, К
            K, // Коэфициент сжимаемости
            RO, // Плотность в РУ
            mu, // Динамическая вязкость
            Kad; // Показатель адиабаты

    double Qc;// Объемный расход среды, приведенный к С.У., куб.м/ч

    static double
            Pcr = 3.766, // Давление в критической точке, МПа
            Tcr = 132.5, // Температура в критической точке, К
            ROcr = 316.5, // Плотность в критической точке, кг/куб.м
            Zcr = 0.3127934, // Фактор сжимаемости в критической точке
            Zs = 0.99961, // Фактор сжимаемости при стандартных условиях
            Ts = 293.15, // Стандартная температура среды, К
            Ps = 0.101325; // Стандартное абсолютное давление среды, МПа

    double[][] b = {
            { 0.366812e0, -0.252712e0, -2.84986e0, 3.60179e0, -3.18665e0, 1.54029e0, -0.260953e0, -0.391073e-1 }, //b0
            { 0.140979e0, -0.724337e-1, 0.780803e0, -0.143512e0, 0.633134e0, -0.891012e0, 0.582531e-1, 0.172908e-1 },
            { -0.790202e-1, -0.213427e0, -1.25167e0, -0.164970e0, 0.684822e0, 0.221185e0, 0.634056e-1},
            { 0.313247e0, 0.885714e0, 0.634585e0, -0.162912e0, -0.217973e0, 0.925251e-1, 0.893863e-3},
            { -0.444978e0, -0.734544e0, 0.199522e-1, -0.176007e0, -0.998455e-1, -0.620965e-1},
            { 0.28578e0, 0.258413e0, 0.749790e-1, 0.859487e-1, -0.884071e-3},
            { -0.636588e-1, -0.105811e0, -0.345172e-1, 0.429817e-1, 0.631385e-2},
            { 0.116375e-3, 0.361900e-1, -0.195095e-1, -0.379583e-2}};

    double[] a = {-66.9619e0, 322.119e0, -547.958e0, 347.643e0, 38.4042e0, -2.18923e0};

    double[] cj = { 93.6970, -82.4089, 132.488, -177.977, 73.9072, 20.5440, 137.268, -107.034, -27.9017, 29.0736};
    double[] rj = { 1, 1, 2, 3, 3, 3, 4, 4, 5, 5};
    double[] tj = { 1, 2, 0, 0, 1, 2, 0, 1, 0, 1};

    double[] alpha = {6.61738e0, -1.05885e0, 0.201650e0, -0.196930e-1, 0.106460e-2, -0.303284e-4, 0.355861e-6};

    double[] beta = {0, -5.49169e0, 5.85171e0, -3.72865e0, 1.33981e0, -0.233758e0, 0.125718e-1};

    // Коэффициенты уравнения состояния (1) МР 112-03
    double
            b10 = 0.366812e0,
            b11 = -0.252712e0,
            b12 = -2.84986e0,
            b13 = 3.60179e0,
            b14 = -3.18665e0,
            b15 = 1.54029e0,
            b16 = -0.260953e0,
            b17 = -0.391073e-1,

    b20 = 0.140979e0,
            b21 = -0.724337e-1,
            b22 = 0.780803e0,
            b23 = -0.143512e0,
            b24 = 0.633134e0,
            b25 = -0.891012e0,
            b26 = 0.582531e-1,
            b27 = 0.172908e-1,

    b30 = -0.790202e-1,
            b31 = -0.213427e0,
            b32 = -1.25167e0,
            b33 = -0.164970e0,
            b34 = 0.684822e0,
            b35 = 0.221185e0,
            b36 = 0.634056e-1,

    b40 = 0.313247e0,
            b41 = 0.885714e0,
            b42 = 0.634585e0,
            b43 = -0.162912e0,
            b44 = -0.217973e0,
            b45 = 0.925251e-1,
            b46 = 0.893863e-3,

    b50 = -0.444978e0,
            b51 = -0.734544e0,
            b52 = 0.199522e-1,
            b53 = -0.176007e0,
            b54 = -0.998455e-1,
            b55 = -0.620965e-1,

    b60 = 0.28578e0,
            b61 = 0.258413e0,
            b62 = 0.749790e-1,
            b63 = 0.859487e-1,
            b64 = -0.884071e-3,

    b70 = -0.636588e-1,
            b71 = -0.105811e0,
            b72 = -0.345172e-1,
            b73 = 0.429817e-1,
            b74 = 0.631385e-2,

    b80 = 0.116375e-3,
            b81 = 0.361900e-1,
            b82 = -0.195095e-1,
            b83 = -0.379583e-2;



    //Обработка нажатия кнопки Home в ActionBar'е
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_air_flowmeter);


        //Настройка ActionBar'а
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.title_activity_air_flowmeter);

        checkBoxAbsolute = (CheckBox)findViewById(R.id.checkBoxAbsolute);

        LinearLayoutAbsolutePressure = (LinearLayout)this.findViewById(R.id.LinearLayoutAbsolutePressure);
        LinearLayoutUNAbsolutePressure = (LinearLayout)this.findViewById(R.id.LinearLayoutUNAbsolutePressure);

        editTextTemperature = (EditText)findViewById(R.id.editTextTemperature);
        editTextVolumeFlow = (EditText)findViewById(R.id.editTextVolumeFlow);
        editTextAbsolutePressure = (EditText)findViewById(R.id.editTextAbsolutePressure);
        editTextAtmosphericPressure = (EditText)findViewById(R.id.editTextAtmosphericPressure);
        editTextRedundantPressure = (EditText)findViewById(R.id.editTextRedundantPressure);

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
        // Настраиваем адаптер
        ArrayAdapter<?> adapterTemperatureDimension =
                ArrayAdapter.createFromResource(this, R.array.TemperatureDimension, android.R.layout.simple_spinner_item);
        adapterTemperatureDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        spinnerTemperatureDimension.setAdapter(adapterTemperatureDimension);

        // Получаем экземпляр элемента spinnerAbsolutePressureDimension
        spinnerAbsolutePressureDimension = (Spinner)findViewById(R.id.spinnerAbsolutePressureDimension);
        // Получаем экземпляр элемента spinnerAtmosphericPressureDimension
        spinnerAtmosphericPressureDimension = (Spinner)findViewById(R.id.spinnerAtmosphericPressureDimension);
        // Получаем экземпляр элемента spinnerRedundantPressureDimension
        spinnerRedundantPressureDimension = (Spinner)findViewById(R.id.spinnerRedundantPressureDimension);
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

        checkBoxAbsolute.setChecked(true);
        spinnerVolumeFlowDimension.setSelection(1);
        spinnerTemperatureDimension.setSelection(0);
        spinnerAbsolutePressureDimension.setSelection(2);
        spinnerAtmosphericPressureDimension.setSelection(2);
        spinnerRedundantPressureDimension.setSelection(2);
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

        // выводим сообщение
        //Toast.makeText(this, String.valueOf(Qv), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(tc), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(PAbsolute), Toast.LENGTH_SHORT).show();

        // Проверка диапазонов введенных параметров
        //Проверка диапазона температуры среды
        if ((tc < -79) && (tc > 127)) {
            Toast.makeText(this, "Значение температуры среды вне диапазона [-73; 127] C", Toast.LENGTH_LONG).show();
            return;
        }
        //Проверка диапазона абсолютного давления сре-ды
        if (PAtmospheric > 20) {
            Toast.makeText(this, "Значение абсолютного давления 20 МПа", Toast.LENGTH_LONG).show();
            return;
        }


        // Вызов функции расчета
        Calculate();

        // Формирование строки результата
        result = "Объемный расход\n" + String.format("%.2f", Qc) + "[куб.м/ч]" +
                "\n\nПлотность в РУ\n" + String.format("%.2f", RO) + " [кг/куб.м]" +
                "\n\nКоэф-т сжимаемости\n" + String.format("%.6f", K) +
                "\n\nПоказатель адиабаты\n" + String.format("%.6f", Kad) +
                "\n\nДинамическая вязкость\n" + String.format("%.6f", mu) + " [мкПа*с]???";

        // Инициализация диалога для вывода результата
        AlertDialog.Builder builder = new AlertDialog.Builder(AirFlowmeterActivity.this);
        builder.setTitle(R.string.gas_name)
                .setMessage(result)
                //.setIcon(R.drawable.ic_android_cat)
                //.setCancelable(false)
                .setNeutralButton("PDF",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Закрыть",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // Создание диалога для вывода результата
        AlertDialog alert = builder.create();
        // Отображение диалога для вывода результата
        alert.show();
    }


    private void Calculate() {
        //Toast.makeText(this, String.valueOf(T), Toast.LENGTH_SHORT).show();

        double teta = 0,
                dw,
                w, // Значение приведенной плотности
                w0, // Начальное значение приведенной плотности
                Z, // Z из УС ф.(1) МР 112-03
                A1, A2, A3, // Комплексы А1, A2, A3 ф.(3-5) МР 112-03
                Cp0r = 0, // Безразмерная изобарная теплоемкость; // Показатель адиабаты
                mu0 = 0 ,
                dmu = 0;

        p = (PAbsolute /** Math.pow(10.0, -6)*/)/Pcr; // Расчет приведенного давления, МПа; ф(10) МР 112-03
        t = (273.15 + tc)/Tcr; // Расчет приведенной температуры, К; ф(10) МР 112-03
        //TK = 273.15 + tc; // Перевод температуры сж.воздуха из С в К

        w0 = (p * Zcr)/t; // Начальное значение приведенной плотности; ф(11) МР 112-03
        w = w0;

        do {
            //while ((dw / w) > Math.pow(10.0, -6)){
            A1 = 0;
            Z = 1;

            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    A1 = A1 + ((i + 1) + 1) * b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
                }
            }

            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    Z = Z + b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
                }
            }

            // ф(12) МР 112-03
            dw = (w0 - Z * w) / (1.0 + A1);
            w = w + dw; // Приведенная плотность
        } while (Math.abs(dw / w) > Math.pow(10.0, -6));

        Z = 1;

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                Z = Z + b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
            }
        }


        K = Z/Zs; // Вычисление коэффициента сжимаемости сж.воздуха по ГОСТ Р 8.740

        Qc = (Qv * PAbsolute * Ts) / (Ps * T * K); //  Вычисление объемного расхода среды по ГОСТ Р 8.740

        RO = w * ROcr;

        A1 = 0;
        A2 = 0;
        A3 = 0;

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                A1 = A1 + ((i + 1) + 1) * b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
                A2 = A2 + (j - 1) * b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
                A3 = A3 + (j * (j - 1) / (i + 1)) * b[i][j] * Math.pow(w, (i + 1)) / Math.pow(t, j);
            }
        }
        A2 = -A2;
        A3 = -A3;

        teta = T / 100;

        for (int j = 0; j < 6; j++) {
            Cp0r = Cp0r + alpha[j] * Math.pow(teta, j) + beta[j] * Math.pow(teta, -j);
        }

        Kad = (1 + A1 + Math.pow((1 + A2), 2) / (Cp0r - 1 + A3)) / Z;


        for (int i = 0; i < 6; i++) {
            mu0 = mu0 + a[i] * Math.pow( t, (0.5 * (i-3)) );
        }
        for (int j = 0; j < 9; j++) {
            dmu = cj[j] * Math.pow(w, rj[j]) / Math.pow(t, tj[j]);
        }

        mu = 0.1 * (mu0 * + dmu);
    }

}
