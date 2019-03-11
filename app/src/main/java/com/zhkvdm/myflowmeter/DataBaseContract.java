package com.zhkvdm.myflowmeter;

import android.provider.BaseColumns;

public final class DataBaseContract {
    private DataBaseContract() {
    }

    public static final class StoryTable implements BaseColumns {
        // имя таблицы
        public static final String TABLE_NAME = "storytable";
        // названия столбцов
        public static final String _ID = BaseColumns._ID;// ID
        public static final String COLUMN_ENVIRON = "environ";// тип измеряемой среды environ
        public static final String COLUMN_METHOD = "method";// метод измерения
        public static final String COLUMN_RESULT = "result";// результаты вычислений
        public static final String COLUMN_DATETIME = "date";// дата и время вичислений
    }
}
