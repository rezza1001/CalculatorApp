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
import com.rezzza.calculatorapp.database.table.ResultDB;
import com.rezzza.calculatorapp.model.ResultDom;

import java.util.ArrayList;

public class DatabaseViewModel extends AndroidViewModel {
    String TAG = "MainViewModel";

    @SuppressLint("StaticFieldLeak")
    Context mContext;

    private MutableLiveData<ResultDB> liveResult;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);

        mContext = application.getApplicationContext();

    }

    public void processImage(Bitmap bitmap){
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


    public MutableLiveData<ResultDB> initLiveResult() {
        if (liveResult == null) {
            liveResult = new MutableLiveData<>();
        }
        return liveResult;
    }

    public ArrayList<ResultDom> getResultDB(){
        ArrayList<ResultDom> list = new ArrayList<>();
        ResultDB db = new ResultDB();
        for (ResultDB res : db.getData(mContext)){
            ResultDom dom = new ResultDom();
            dom.setId(res.id);
            dom.setNumbA(res.numA);
            dom.setNumbB(res.numB);
            dom.setExpresion(res.expresion);
            dom.setValue(res.value);
            list.add(dom);
        }
        return list;
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

        ResultDB db = new ResultDB();
        db.numA = Integer.parseInt(numA.toString());
        db.numB = Integer.parseInt(numB.toString());
        db.expresion = exp;
        db.id   =  db.getNextID(mContext);
        switch (exp) {
            case "+":
                db.value = db.numA + db.numB;
                break;
            case "-":
                db.value = db.numA - db.numB;
                break;
            case "*":
                db.value = db.numA * db.numB;
                break;
            case "/":
                db.value = db.numA / db.numB;
                break;
        }
        db.insert(mContext);
        liveResult.postValue(db);
    }


    private void showToastError(String message){
        Toast.makeText(getApplication().getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }


}
