package com.zhkvdm.myflowmeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    int itemSpinerIonsArray[];
    String[] itemSpinerTextArray;
    LayoutInflater layoutInflater;

    public CustomSpinnerAdapter(Context applicationContext, int[] itemIons, String[] itemText) {
        this.context = applicationContext;
        this.itemSpinerIonsArray = itemIons;
        this.itemSpinerTextArray = itemText;
        layoutInflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return itemSpinerIonsArray.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Настройка внешнего вида Spinner
        view = layoutInflater.inflate(R.layout.spinner_row_custom, null);
        ImageView itemSpinerIcon = (ImageView) view.findViewById(R.id.itemSpinerIcon);
        TextView itemSpinerText = (TextView) view.findViewById(R.id.itemSpinerText);
        itemSpinerIcon.setImageResource(itemSpinerIonsArray[i]);
        itemSpinerText.setText(itemSpinerTextArray[i]);
        return view;
    }
}