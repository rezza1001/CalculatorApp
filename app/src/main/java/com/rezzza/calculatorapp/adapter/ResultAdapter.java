package com.rezzza.calculatorapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rezzza.calculatorapp.R;
import com.rezzza.calculatorapp.model.ResultDom;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.AdapterView>{

    private final ArrayList<ResultDom> mList;

    public ResultAdapter(ArrayList<ResultDom> list){
        this.mList = list;
    }


    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_result, parent, false);
        return new AdapterView(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final ResultDom data = mList.get(position);
        holder.txvw_numA.setText(data.getNumbA()+"");
        holder.txvw_numB.setText(data.getNumbB()+"");
        holder.txvw_exp.setText(data.getExpresion());
        holder.txvw_value.setText(data.getValue()+"");
        if ((position + 1 ) == mList.size()){
            holder.card_body.setCardBackgroundColor(Color.parseColor("#cfd8dc"));
        }
        else {
            holder.card_body.setCardBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class AdapterView extends RecyclerView.ViewHolder{
        TextView txvw_numA,txvw_exp,txvw_numB,txvw_value,txvw_no;
        CardView card_body;

        public AdapterView(@NonNull View itemView) {
            super(itemView);
            txvw_numA = itemView.findViewById(R.id.txvw_numA);
            txvw_exp = itemView.findViewById(R.id.txvw_exp);
            txvw_numB = itemView.findViewById(R.id.txvw_numB);
            txvw_value = itemView.findViewById(R.id.txvw_value);
            txvw_no = itemView.findViewById(R.id.txvw_no);
            card_body = itemView.findViewById(R.id.card_body);
        }
    }
}
