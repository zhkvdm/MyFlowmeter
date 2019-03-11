package com.zhkvdm.myflowmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PDFDocument {

    Context context;

    public PDFDocument(Context applicationContext){
        this.context = applicationContext;
    }


    public void createPDF(String environ, String method, String result, String dateTime){
        // String title - название измеряемой среды
        // String result - результаты расчетов

        // создаем документ PDF
        PdfDocument document = new PdfDocument();
        // определяем размер страницы
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(210, 297, 1).create();
        // получаем страницу, на которой будем генерировать контент
        PdfDocument.Page page = document.startPage(pageInfo);
        // получаем холст (Canvas) страницы
        Canvas canvas = page.getCanvas();

        TextPaint paint = new TextPaint(/*Paint.ANTI_ALIAS_FLAG*/);
        // рисуем содержимое и закрываем страницу
        paint.setColor(Color.BLACK);
        paint.setTextSize(8);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                "Отчет о расчетах\n\nИзмеряемая среда: " + environ + "\n\n" + "Метод рассчета: " + method + "\n\n" + result + "\n\n" + dateTime,
                        paint, 210, Layout.Alignment.ALIGN_LEFT, 1, 0, false);

        // get position of text's top left corner
        float x = 10;
        float y = 20;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        document.finishPage(page);

        // создаем папку для файлов
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Flowmeter reports");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // сохраняем записанный контент
        String targetPdf = dir.getAbsolutePath() + "/" + dateTime + " report.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(context, "Отчет сохранён в " + dir.getAbsolutePath()/*filePath.getAbsolutePath()*/, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "PDf сохранён в " + filePath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка сохранения: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }



}
