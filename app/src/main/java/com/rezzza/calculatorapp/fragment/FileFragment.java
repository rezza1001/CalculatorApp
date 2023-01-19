package com.rezzza.calculatorapp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rezzza.calculatorapp.MyApplication;
import com.rezzza.calculatorapp.R;
import com.rezzza.calculatorapp.adapter.ResultAdapter;
import com.rezzza.calculatorapp.model.ResultDom;
import com.rezzza.calculatorapp.tools.FileProcessing;
import com.rezzza.calculatorapp.viewmodel.FileViewModel;

import java.util.ArrayList;

/**
 * Mochamad Rezza Gumilang
 */

public class FileFragment extends Fragment {
    String TAG = "FileFragment";
    protected FileViewModel fileViewModel;

    ArrayList<ResultDom> listResult = new ArrayList<>();
    ResultAdapter adapter;

    TextView txvw_path;

    public FileFragment() {
    }

    public static FileFragment newInstance() {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireContext().registerReceiver(receiver, new IntentFilter("RESULT"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        requireContext().unregisterReceiver(receiver);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(getContext()));
        txvw_path = view.findViewById(R.id.txvw_path);
        adapter = new ResultAdapter(listResult);
        rcvw_data.setAdapter(adapter);

        txvw_path.setText("File At : "+ FileProcessing.ROOT+"/"+FileViewModel.FOLDER+"/"+FileViewModel.fileName);


        initModel();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] byteArray = MyApplication.byteData;
            Log.d(TAG,"RECEIVE BROADCAST "+byteArray.length);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            fileViewModel.processImage(bmp);
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    private void initModel(){
        fileViewModel =  new ViewModelProvider(this).get(FileViewModel.class);
        fileViewModel.initLiveResult().observe(getViewLifecycleOwner(), resultDB -> {
            listResult.add(resultDB);
            adapter.notifyDataSetChanged();
        });

        fileViewModel.setOnLoadListener(dom -> {
            listResult.addAll(dom);
            adapter.notifyDataSetChanged();
        });

        fileViewModel.getResultDB();
    }

}