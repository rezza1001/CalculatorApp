package com.rezzza.calculatorapp.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rezzza.calculatorapp.R;
import com.rezzza.calculatorapp.adapter.PagerAdapter;
import com.rezzza.calculatorapp.tools.FileProcessing;
import com.rezzza.calculatorapp.tools.Utility;
import com.rezzza.calculatorapp.view.TabItmeView;
import com.skyhope.textrecognizerlibrary.TextScanner;
import com.skyhope.textrecognizerlibrary.callback.TextExtractCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Mochamad Rezza Gumilang
 */


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    ArrayList<String> listTab = new ArrayList<>();
    TabLayout tab_header;
    ViewPager2 vwpg_main;

    private String mBroadcast = "RESULT";

    private static final String ROOT_FILE = "process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.rvly_camera).setOnClickListener(view -> openCamera());
        findViewById(R.id.rvly_file).setOnClickListener(view -> showFileChooser());

        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        createTab();

    }

    private void createTab(){
        tab_header = findViewById(R.id.tab_header);
        vwpg_main = findViewById(R.id.vwpg_main);

        PagerAdapter pagerAdapter = new PagerAdapter(this, listTab);
        vwpg_main.setAdapter(pagerAdapter);

        listTab.add("Database Storage");
        listTab.add("File Storage");
        new TabLayoutMediator(tab_header, vwpg_main, this::configTab).attach();

        tab_header.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabItmeView tabView = (TabItmeView) tab.getCustomView();
                if (tabView != null){
                    tabView.setSelected();
                    if (tabView.getText().equalsIgnoreCase("Database Storage")){
                        mBroadcast = "RESULT";
                    }
                    else {
                        mBroadcast = "RESULT_2";
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TabItmeView tabView = (TabItmeView) tab.getCustomView();
                if (tabView != null){
                    tabView.unSelected();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void configTab(TabLayout.Tab tab, int position){
        TabItmeView tabView = new TabItmeView(this,null);
        String tabHolder = listTab.get(position);
        tabView.setText(tabHolder);
        tab.setCustomView(tabView);

        if (position == 0){
            tabView.setSelected();
        }
    }


    private void showFileChooser() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent.createChooser(intent, "Choose File to Upload..");
        startActivityForResult(intent,1);

    }
    private void openCamera() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT+"/"+ROOT_FILE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+ROOT_FILE;
        String file =mediaPath+"/certificateTemp.jpg";
        File newfile = new File(file);
        try {
            if (newfile.exists()){
                boolean deleted =  newfile.delete();
            }
            boolean created = newfile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputFileUri = FileProcessing.getUriFormFile(this, newfile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+ROOT_FILE;
            String file =mediaPath+"/certificateTemp.jpg";
            Uri uri = Uri.fromFile(new File(file));
            process(uri);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(mBroadcast);
                intent.putExtra("request", requestCode);
                intent.putExtra("data", uri);
                sendBroadcast(intent);
                Log.d(TAG,"SEND BROADCAST");
            },100);
        }
        else {
            if (data == null){
                Toast.makeText(this,"Data is error", Toast.LENGTH_LONG).show();
                return;
            }
            if (resultCode != RESULT_OK){
                return;
            }
            process(data.getData());
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(mBroadcast);
                intent.putExtra("request", requestCode);
                intent.putExtra("data", data.getData());
                sendBroadcast(intent);
                Log.d(TAG,"SEND BROADCAST");
            },100);
        }

    }

    private void process(Uri uri){

    }


}