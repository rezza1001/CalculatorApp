package com.rezzza.calculatorapp.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.rezzza.calculatorapp.model.ResultDom;
import com.rezzza.calculatorapp.tools.FileProcessing;
import com.rezzza.calculatorapp.tools.MyFileReader;

import java.io.File;
import java.util.ArrayList;

public class FileViewModel extends AndroidViewModel {
    String TAG = "MainViewModel";

    @SuppressLint("StaticFieldLeak")
    Context mContext;

    public static final String FOLDER = "DB";
    public static final String fileName = "calculator.txt";

    private MutableLiveData<ResultDom> liveResult;

    public FileViewModel(@NonNull Application application) {
        super(application);

        mContext = application.getApplicationContext();
        FileProcessing.createFolder(mContext,FileProcessing.ROOT+"/"+FOLDER);

    }

    public void processImage(Bitmap bitmap){
        if (bitmap == null){
            Log.e(TAG,"processImage bitmap null");
            return;
        }
        Log.d(TAG,"processImage ");
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

        task.addOnSuccessListener(firebaseVisionText -> {
            String text = firebaseVisionText.getText();
            Log.d(TAG,"process "+ text);
            for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                Log.d(TAG,"blok "+ block.getText());
            }
            process(text);
        });

        task.addOnFailureListener(e -> {
            Log.e(TAG,e.getMessage());
            showToastError(e.getMessage());
            e.printStackTrace();
        });
    }


    public MutableLiveData<ResultDom> initLiveResult() {
        if (liveResult == null) {
            liveResult = new MutableLiveData<>();
        }
        return liveResult;
    }

    public void getResultDB(){
        ArrayList<ResultDom> list = new ArrayList<>();

        MyFileReader fileReader = new MyFileReader(mContext);
        String folder = FileProcessing.getMainPath(mContext).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+FOLDER+"/"+fileName;
        if (! new File(folder).exists()){
            return ;
        }
        fileReader.readFile(folder);
        fileReader.setOnReadListener(new MyFileReader.OnReadListener() {
            @Override
            public void onLiveRead(String data) {
                ResultDom dom = new ResultDom();
                dom.unpack(data);
                list.add(dom);
            }

            @Override
            public void onFinish(String start, String data, String end) {
                if (onLoadListener != null){
                    onLoadListener.onLoad(list);
                }
            }
        });
    }

    private void process(String text){
        text = text.replaceAll(" ","");
        Log.d(TAG,"process "+ text);
        StringBuilder numA = new StringBuilder();
        StringBuilder numB = new StringBuilder();
        String exp = "";
        for (int i=0; i<text.length(); i++){
            char x = text.charAt(i);
            try {
                int a = Integer.parseInt(String.valueOf(x));
                if (exp.isEmpty()){
                    numA.append(a);
                }
                else {
                    numB.append(a);
                }
            }catch (Exception e){
                Log.d(TAG,x +" is not number");
                if (String.valueOf(x).equalsIgnoreCase("l") ||
                        String.valueOf(x).equalsIgnoreCase("|")||
                        String.valueOf(x).equalsIgnoreCase("!")||
                        String.valueOf(x).equalsIgnoreCase("i"))
                {
                    exp = "/";
                }
                else if (String.valueOf(x).equalsIgnoreCase("X")){
                    exp = "*";
                }
                else {
                    if (String.valueOf(x).matches("[+\\-*/]") && exp.isEmpty()){
                        exp = String.valueOf(x);
                    }
                }

            }
        }
        if (numA.toString().isEmpty() || numB.toString().isEmpty()){
            showToastError("The image you selected is unreadable. Please select a clearer image");
            return;
        }


        ResultDom db = new ResultDom();
        db.setNumbA(Integer.parseInt(numA.toString()));
        db.setNumbB(Integer.parseInt(numB.toString()));
        db.setExpresion(exp);
        double val = 0;
        switch (exp) {
            case "+":
                val = db.getNumbA() + db.getNumbB();
                break;
            case "-":
                val = db.getNumbA() - db.getNumbB();
                break;
            case "*":
                val = db.getNumbA() * db.getNumbB();
                break;
            case "/":
                val = db.getNumbA() / db.getNumbB();
                break;
        }
        db.setValue(val);

        FileProcessing.WriteFileToLog(mContext,FOLDER,fileName,db.pack());
        liveResult.postValue(db);
    }


    private void showToastError(String message){
        Toast.makeText(getApplication().getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    private OnLoadListener onLoadListener;
    public void setOnLoadListener(OnLoadListener onLoadListener){
        this.onLoadListener = onLoadListener;
    }
    public interface OnLoadListener{
        void onLoad(ArrayList<ResultDom> dom);
    }


}
