package com.rezzza.calculatorapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageView;
import com.rezzza.calculatorapp.MyApplication;
import com.rezzza.calculatorapp.R;

import java.io.ByteArrayOutputStream;

public class CroperImageActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private ImageView imvw_prview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        cropImageView = findViewById(R.id.cropImageView);
        imvw_prview = findViewById(R.id.imvw_prview);
        imvw_prview.setVisibility(View.GONE);

        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        cropImageView.setImageUriAsync(getIntent().getData());


        findViewById(R.id.rvly_crop).setOnClickListener(view -> {
            Bitmap bmp = cropImageView.getCroppedImage();
            imvw_prview.setImageBitmap(bmp);

            if (bmp == null){
                return;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            MyApplication.byteData = stream.toByteArray();
            setResult(RESULT_OK);
            finish();
        });
    }
}
