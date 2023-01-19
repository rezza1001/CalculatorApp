package com.rezzza.calculatorapp.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.rezzza.calculatorapp.R;

public class OptionStorageDialog extends MyDialog{

    public OptionStorageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_option_storage;
    }

    @Override
    protected void initLayout(View view) {
        view.findViewById(R.id.rvly_root).setOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.txvw_file).setOnClickListener(view1 -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelect("File Storage");
                dismiss();
            }
        });
        view.findViewById(R.id.txvw_db).setOnClickListener(view1 -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelect("Database Storage");
                dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelect(String type);
    }
}
