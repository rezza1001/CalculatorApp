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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.rezzza.calculatorapp.R;
import com.rezzza.calculatorapp.dialog.OptionStorageDialog;
import com.rezzza.calculatorapp.fragment.DatabaseFragment;
import com.rezzza.calculatorapp.fragment.FileFragment;
import com.rezzza.calculatorapp.tools.FileProcessing;
import com.rezzza.calculatorapp.tools.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Mochamad Rezza Gumilang
 */


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    private FrameLayout frame_body;
    private TextView txvw_type;

    private String mType = "Database Storage";


    private static final String ROOT_FILE = "process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame_body = findViewById(R.id.frame_body);
        txvw_type = findViewById(R.id.txvw_type);

        findViewById(R.id.rvly_camera).setOnClickListener(view -> openCamera());
        findViewById(R.id.rvly_file).setOnClickListener(view -> showFileChooser());
        findViewById(R.id.rvly_param).setOnClickListener(view -> showOptionTab());

        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }
        createTab();
    }

    private void showOptionTab(){
        OptionStorageDialog dialog = new OptionStorageDialog(this);
        dialog.show();
        dialog.setOnSelectedListener(type -> {
            mType = type;
            createTab();
        });
    }

    private void createTab(){
        txvw_type.setText(mType);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        if (mType.equals("File Storage")){
            fragment = FileFragment.newInstance();
            fragmentTransaction.replace(frame_body.getId(), fragment,"file");
        }
        else {
            fragment = DatabaseFragment.newInstance();
            fragmentTransaction.replace(frame_body.getId(), fragment,"database");
        }
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
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
            Log.d(TAG,"Create "+file+" "+created);
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
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setClass(this,CroperImageActivity.class);
            startActivityForResult(intent,3);
        }
        else if (requestCode == 1){
            if (data == null){
                Toast.makeText(this,"Data is error", Toast.LENGTH_LONG).show();
                return;
            }
            if (resultCode != RESULT_OK){
                return;
            }
            data.setClass(this,CroperImageActivity.class);
            startActivityForResult(data,3);
        }
        else if (requestCode == 3 && resultCode == RESULT_OK){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent("RESULT");
                sendBroadcast(intent);
                Log.d(TAG,"Notify");
            },100);
        }
    }


}