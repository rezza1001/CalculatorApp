package com.rezzza.calculatorapp.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rezzza.calculatorapp.R;

public class TabItmeView extends MyView {
    private TextView txvw_tab;

    public TabItmeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.dashboard_tab_item;
    }

    @Override
    protected void initLayout() {
        txvw_tab = findViewById(R.id.txvw_tab);
    }

    @Override
    protected void initListener() {

    }


    public void setText(String text){
        txvw_tab.setText(text);
    }

    public String getText(){
        return txvw_tab.getText().toString();
    }
    public void setSelected(){
        txvw_tab.setTextColor(Color.parseColor("#39796B"));
    }

    public void unSelected(){
        txvw_tab.setTextColor(Color.parseColor("#636363"));
    }
}
