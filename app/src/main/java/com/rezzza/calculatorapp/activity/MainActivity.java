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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.rezzza.calculatorapp.R;
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

    ArrayList<String> listTab = new ArrayList<>();
    private FrameLayout frame_body;

    private final String mBroadcast = "RESULT";

    private static final String ROOT_FILE = "process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame_body = findViewById(R.id.frame_body);

        findViewById(R.id.rvly_camera).setOnClickListener(view -> openCamera());
        findViewById(R.id.rvly_file).setOnClickListener(view -> showFileChooser());

        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        createTab();
    }

    private void createTab(){

        String packageName = getPackageName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (packageName.contains("greenfile")){
            Fragment fragment = FileFragment.newInstance();
            fragmentTransaction.replace(frame_body.getId(), fragment,"greenfile");
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
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