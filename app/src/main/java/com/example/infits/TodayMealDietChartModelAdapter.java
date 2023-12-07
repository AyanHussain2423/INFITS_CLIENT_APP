package com.example.infits;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class TodayMealDietChartModelAdapter extends RecyclerView.Adapter<TodayMealDietChartModelAdapter.ViewHolder> {
    private final ArrayList<TodayMealDietChartModel> viewModelsListFiltered;
    private Activity activity;

    public TodayMealDietChartModelAdapter(Activity activity,ArrayList<TodayMealDietChartModel> viewModelsListFiltered) {
        this.activity=activity;
        this.viewModelsListFiltered = viewModelsListFiltered;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.diet_chart_today_meals_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mealName.setText((viewModelsListFiltered.get(position).getDietChartMealName()));
        holder.day.setText((viewModelsListFiltered.get(position).getDay()));
        holder.time.setText((viewModelsListFiltered.get(position).getTime()));

    }
    @Override
    public int getItemCount() {
        return viewModelsListFiltered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mealName,day,time;
        TextView calories,protiens,fats,carbs;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.dietChartMealName);
            day = itemView.findViewById(R.id.txtDayName);
            time = itemView.findViewById(R.id.txtTimeDietChart);
            calories = itemView.findViewById(R.id.textView7);
            protiens = itemView.findViewById(R.id.textView28);
            fats = itemView.findViewById(R.id.textView29);
            carbs = itemView.findViewById(R.id.textView30);


        }
    }

}


