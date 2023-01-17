package com.rezzza.calculatorapp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rezzza.calculatorapp.R;
import com.rezzza.calculatorapp.adapter.ResultAdapter;
import com.rezzza.calculatorapp.model.ResultDom;
import com.rezzza.calculatorapp.viewmodel.DatabaseViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Mochamad Rezza Gumilang
 */

public class DatabaseFragment extends Fragment {
    String TAG = "DatabaseFragment";
    protected DatabaseViewModel databaseViewModel;

    ArrayList<ResultDom> listResult = new ArrayList<>();
    ResultAdapter adapter;


    public DatabaseFragment() {
    }

    public static DatabaseFragment newInstance() {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getContext()).registerReceiver(receiver, new IntentFilter("RESULT"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_database, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ResultAdapter(listResult);
        rcvw_data.setAdapter(adapter);

        initModel();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri uri = intent.getParcelableExtra("data");
            processFileBrowser(uri);
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    private void initModel(){
        databaseViewModel =  new ViewModelProvider(this).get(DatabaseViewModel.class);
        databaseViewModel.initLiveResult().observe(this, resultDB -> {
            ResultDom dom = new ResultDom();
            dom.setId(resultDB.id);
            dom.setNumbA(resultDB.numA);
            dom.setNumbB(resultDB.numB);
            dom.setExpresion(resultDB.expresion);
            dom.setValue(resultDB.value);
            listResult.add(dom);
            adapter.notifyDataSetChanged();
            Log.d(TAG,"RESULT "+ listResult.size());
        });

        listResult.addAll(databaseViewModel.getResultDB());
        adapter.notifyDataSetChanged();
    }


    private void processFileBrowser(Uri contentURI ) {
        try {
            if (getContext() == null){
                showToastError("Please try again");
                return;
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), contentURI);
            databaseViewModel.processImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToastError(String message){
        Toast.makeText(getContext(),message, Toast.LENGTH_LONG).show();
    }
}