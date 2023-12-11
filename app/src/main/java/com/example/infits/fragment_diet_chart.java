package com.example.infits;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.progress_view.ProgressData;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tenclouds.gaugeseekbar.GaugeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_diet_chart extends Fragment {
    GaugeSeekBar progressBar,progressBarProtein,progressBarCarbs,progressBarFats;

    ImageView back,gaugeSeekMiddleIcon;
    Button btn_meal_check;

    //for calories protein carbs fats click listener
    AppCompatButton btn_calories,btn_proteins,btn_carbs,btn_fats;
    TextView gaugeSeekMiddleText,gaugeSeekMiddleTextValue,gaugeSeekMiddleTextUnit;

    //for spinner
    Spinner dailySpinner,btn_meal;

    //for progress bar
    ProgressBar processBar;

    TodayMealDietChartModel todayMealDietChartModel;
    TodayMealDietChartModelAdapter todayMealDietChartModelAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    TextView textView7,textView28,textView29,textView30;
    public static ArrayList<TodayMealDietChartModel> viewListModels = new ArrayList<>();


    public fragment_diet_chart () {
        // Required empty public constructor
    }



    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView ( LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate ( R.layout.fragment_diet_chart , container , false );
        hooks(view);
        getmicronutrientdata();
        //set daily weekly and yearly in spinner
        ArrayAdapter<CharSequence> dailyAdapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.spinner_diet_daily,R.layout.diet_spinner_dropdown_item );
        dailyAdapter.setDropDownViewResource(R.layout.diet_spinner_layout);
        dailySpinner.setAdapter(dailyAdapter);

        //set breakfast snacks lunch Dinner
        ArrayAdapter<CharSequence> breakfastAdapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.spinner_diet_breakfast,R.layout.diet_spinner_dropdown_item );
        breakfastAdapter.setDropDownViewResource(R.layout.diet_spinner_layout);
        btn_meal.setAdapter(breakfastAdapter);



        //for click selected item
        btn_meal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (btn_meal.getSelectedItem().equals("Breakfast")) {
                    viewListModels.clear();
                    todayMealDietChartModelAdapter.notifyDataSetChanged();
                    reqWithoutRecipeNameCall("breakfast","breakfast_morning","breakfast_after","7AM to 8AM","After Breakfast");
                } else if (btn_meal.getSelectedItem().equals("Snacks")) {
                    viewListModels.clear();
                    todayMealDietChartModelAdapter.notifyDataSetChanged();
                    reqWithoutRecipeNameCall("snacks","High Tea and Snacks","Snacks time");
                }else if (btn_meal.getSelectedItem().equals("Lunch")) {
                    viewListModels.clear();
                    todayMealDietChartModelAdapter.notifyDataSetChanged();
                    reqWithoutRecipeNameCall("lunch","afternoon","01:00 PM");
                }else {
                    viewListModels.clear();
                    todayMealDietChartModelAdapter.notifyDataSetChanged();
                    reqWithoutRecipeNameCall("dinner","night","late_night","7PM to 9PM","After Dinner");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //for calorie button click listener
        btn_calories.setOnClickListener(view1 -> {
            btn_proteins.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_calories.setBackground(getResources().getDrawable(R.drawable.btn_bg_calorie));
            btn_carbs.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_fats.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            gaugeSeekMiddleIcon.setBackground(getResources().getDrawable(R.drawable.fire_diet_chart));
            gaugeSeekMiddleText.setText("Calories");
            gaugeSeekMiddleTextUnit.setText("kcal");

            String url = "https://infits.in/androidApi/calorieTracker.php";
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("CalTracker Data Bro", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject("Data");

                            JSONObject Valueobject = dataObject.getJSONObject("Values");
                            String calories = Valueobject.getString("Calories");
                            gaugeSeekMiddleTextValue.setText(calories);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, error -> Log.e("CalTracker Data Bro", error.toString())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    LocalDateTime now = LocalDateTime.now();// gets the current date and time
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                    data.put("clientID", DataFromDatabase.clientuserID);
                    data.put("today",dtf.format(now));
                    return data;
                }
            };
            Volley.newRequestQueue(requireContext()).add(request);
            request.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            progressBarCarbs.setVisibility(View.INVISIBLE);
            progressBarProtein.setVisibility(View.INVISIBLE);
            progressBarFats.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        });

        //for protein button click listener
        btn_proteins.setOnClickListener(view1 -> {
            btn_proteins.setBackground(getResources().getDrawable(R.drawable.btn_bg_protein));
            btn_calories.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_carbs.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_fats.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            gaugeSeekMiddleIcon.setBackground(getResources().getDrawable(R.drawable.fish));
            gaugeSeekMiddleText.setText("Protein");
            gaugeSeekMiddleTextUnit.setText("g");
            String url = "https://infits.in/androidApi/calorieTracker.php";
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("CalTracker Data Bro", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject("Data");

                            JSONObject Valueobject = dataObject.getJSONObject("Values");
                            String protein = Valueobject.getString("protein");
                            gaugeSeekMiddleTextValue.setText(protein);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, error -> Log.e("CalTracker Data Bro", error.toString())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    LocalDateTime now = LocalDateTime.now();// gets the current date and time
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                    data.put("clientID", DataFromDatabase.clientuserID);
                    data.put("today",dtf.format(now));
                    return data;
                }
            };
            Volley.newRequestQueue(requireContext()).add(request);
            request.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            progressBar.setVisibility(View.INVISIBLE);
            progressBarCarbs.setVisibility(View.INVISIBLE);
            progressBarFats.setVisibility(View.INVISIBLE);
            progressBarProtein.setVisibility(View.VISIBLE);

        });

        //for carbs click listener
        btn_carbs.setOnClickListener(view1 -> {
            btn_proteins.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_calories.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_carbs.setBackground(getResources().getDrawable(R.drawable.btn_bg_carbs));
            btn_fats.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            gaugeSeekMiddleIcon.setBackground(getResources().getDrawable(R.drawable.carbs_diet_chart));
            gaugeSeekMiddleText.setText("Carbs");
            gaugeSeekMiddleTextUnit.setText("g");

            String url = "https://infits.in/androidApi/calorieTracker.php";
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("CalTracker Data Bro", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject("Data");

                            JSONObject Valueobject = dataObject.getJSONObject("Values");
                            String carbs = Valueobject.getString("carbs");
                            gaugeSeekMiddleTextValue.setText(carbs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, error -> Log.e("CalTracker Data Bro", error.toString())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    LocalDateTime now = LocalDateTime.now();// gets the current date and time
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                    data.put("clientID", DataFromDatabase.clientuserID);
                    data.put("today",dtf.format(now));
                    return data;
                }
            };
            Volley.newRequestQueue(requireContext()).add(request);
            request.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            progressBar.setVisibility(View.INVISIBLE);
            progressBarProtein.setVisibility(View.INVISIBLE);
            progressBarFats.setVisibility(View.INVISIBLE);
            progressBarCarbs.setVisibility(View.VISIBLE);
        });

        //for fats click listener
        btn_fats.setOnClickListener(view1 -> {
            btn_proteins.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_calories.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_carbs.setBackground(getResources().getDrawable(R.drawable.protien_bg));
            btn_fats.setBackground(getResources().getDrawable(R.drawable.btn_bg_fats));
            gaugeSeekMiddleIcon.setBackground(getResources().getDrawable(R.drawable.fats));
            gaugeSeekMiddleText.setText("Fats");
            gaugeSeekMiddleTextUnit.setText("g");
            gaugeSeekMiddleTextValue.setText("200");
            String url = "https://infits.in/androidApi/calorieTracker.php";
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("CalTracker Data Bro", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject("Data");
                            Log.d("fats",String.valueOf(dataObject));
                            JSONObject Valueobject = dataObject.getJSONObject("Values");
                            String fats = Valueobject.getString("fat");
                            gaugeSeekMiddleTextValue.setText(fats);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, error -> Log.e("CalTracker Data Bro", error.toString())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    LocalDateTime now = LocalDateTime.now();// gets the current date and time
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                    data.put("clientID", DataFromDatabase.clientuserID);
                    data.put("today",dtf.format(now));
                    return data;
                }
            };
            Volley.newRequestQueue(requireContext()).add(request);
            request.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            progressBar.setVisibility(View.INVISIBLE);
            progressBarProtein.setVisibility(View.INVISIBLE);
            progressBarCarbs.setVisibility(View.INVISIBLE);
            progressBarFats.setVisibility(View.VISIBLE);
        });

        back.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragment_diet_chart_to_dashBoardFragment));

        progressBar.setProgress(.75f);
        ///  btn_meal_check.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragment_diet_chart_to_stepReminderFragment));

        btn_meal_check.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragment_diet_chart_to_diet_fourth2));

        return view;
    }

    private void getmicronutrientdata() {
        Log.d("getmicro nutrients","function called");
        String url = "https://infits.in/androidApi/calorieTracker.php"; // main database api
        //String url ="http://192.168.0.105//infits/calorieTracker.php"; // localhost

        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        Log.d("vollll", requestQueue.toString());
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Tracker macro", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject dataObject = jsonObject.getJSONObject("Data"); // calling the object from api
                        JSONObject goalsObject = dataObject.getJSONObject("Goals"); //goals has all goals of item protien fibers fats etc

                        String calorieconsumegoal = goalsObject.getString("CalorieConsumeGoal"); // calorie consume goal value is retrieved
                        String carbgoal = goalsObject.getString("CarbsGoal");
                        String proteingoal = goalsObject.getString("ProteinGoal");
                        String fatgoal = goalsObject.getString("FatsGoal");

                        Float carb = Float.valueOf(goalsObject.getString("CarbsGoal"));
                        Float a = carb/carb*100;
                        Log.d("hhh", String.valueOf(a));

                        Log.d("Calories Consumed", calorieconsumegoal);
                        textView7.setText(calorieconsumegoal +" kcal"); // setting value in textview
                        textView28.setText(proteingoal + " g");
                        textView29.setText(carbgoal + " g");
                        textView30.setText(fatgoal + " g");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.e("Traget Macro", error.toString())) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                LocalDateTime now = LocalDateTime.now();// gets the current date and time
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                data.put("clientID", DataFromDatabase.clientuserID);
                data.put("today",dtf.format(now));
                return data;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void hooks(View view) {
        btn_meal_check=view.findViewById (R.id.button6);
        back=view.findViewById(R.id.imageView8DietChart);
        progressBar = view.findViewById(R.id.progressBarDietChart);
        dailySpinner = view.findViewById(R.id.diet_btn_daily);
        btn_meal=view.findViewById(R.id.button8);
        btn_calories = view.findViewById(R.id.button);
        btn_proteins = view.findViewById(R.id.button3);
        btn_carbs = view.findViewById(R.id.button4DietChart);
        btn_fats = view.findViewById(R.id.button5DietChart);
        gaugeSeekMiddleIcon = view.findViewById(R.id.view);
        gaugeSeekMiddleText = view.findViewById(R.id.textView4DietChart);
        gaugeSeekMiddleTextValue = view.findViewById(R.id.textView5DietChart);
        gaugeSeekMiddleTextUnit = view.findViewById(R.id.textView6DietChart);
        progressBarProtein = view.findViewById(R.id.progressBarProteinDietChart);
        progressBarCarbs = view.findViewById(R.id.progressBarCarbsDietChart);
        progressBarFats = view.findViewById(R.id.progressBarFatsDietChart);
        processBar = view.findViewById(R.id.dietChartProgress);
        recyclerView = view.findViewById(R.id.dietChartRecylerView);
        linearLayoutManager= new LinearLayoutManager(requireActivity());
        textView7 = view.findViewById(R.id.textView7);
        textView28 = view.findViewById(R.id.textView28);
        textView29 = view.findViewById(R.id.textView29);
        textView30 = view.findViewById(R.id.textView30);
        recyclerView.setLayoutManager(linearLayoutManager);
        todayMealDietChartModelAdapter = new TodayMealDietChartModelAdapter(requireActivity(),viewListModels);
        recyclerView.setAdapter(todayMealDietChartModelAdapter);
        viewListModels.clear();
    }
    private void reqWithoutRecipeNameCall(String key1,String key2,String key3,String time1,String time2){
        processBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, String.format("%sgetDietChart.php",DataFromDatabase.ipConfig) ,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        processBar.setVisibility(View.GONE);
                        try {
                            JSONObject data_response = new JSONObject(response);
                            String data = data_response.getString("data");
                            JSONArray jsonArrayData = new JSONArray(data);
                            if (!jsonArrayData.isNull(0)) {


                                JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);
                                //for day_name for e.g sunday monday tuesday etc..
                                Format fdn = new SimpleDateFormat("EEEE");
                                JSONObject day_name = new JSONObject(jsonObjectData.getString(fdn.format(new Date()).toLowerCase()));
                                JSONObject key1_data = new JSONObject(day_name.getString(key1));
                                JSONArray key2_data = new JSONArray(key1_data.getString(key2));
                                JSONArray key3_data = new JSONArray(key1_data.getString(key3));
                                for (int i = 0; i < key2_data.length(); i++) {
                                    todayMealDietChartModel = new TodayMealDietChartModel(getRecipeName(key2_data.getString(i)), fdn.format(new Date()), time1);
                                    viewListModels.add(todayMealDietChartModel);
                                }
                                for (int i = 0; i < key3_data.length(); i++) {
                                    todayMealDietChartModel = new TodayMealDietChartModel(getRecipeName(key3_data.getString(i)), fdn.format(new Date()), time2);
                                    viewListModels.add(todayMealDietChartModel);
                                }
                                todayMealDietChartModelAdapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(requireActivity(),"Data is empty", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            processBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    processBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(),"Connectivity Issue", Toast.LENGTH_SHORT).show();
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("dietitian_id",DataFromDatabase.dietitian_id);
                data.put("client_id",DataFromDatabase.client_id);
                data.put("type","1");
                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void reqWithRecipeNameCall(String key1,String key2,String key3,String time1,String time2){
        processBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, String.format("%sgetDietChart.php",DataFromDatabase.ipConfig) ,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        processBar.setVisibility(View.GONE);
                        try {
                            JSONObject data_response = new JSONObject(response);
                            String data = data_response.getString("data");
                            JSONArray jsonArrayData = new JSONArray(data);
                            JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);
                            //for day_name for e.g sunday monday tuesday etc..
                            Format fdn = new SimpleDateFormat("EEEE");
                            JSONObject day_name = new JSONObject(jsonObjectData.getString(fdn.format(new Date()).toLowerCase()));
                            JSONObject key1_data = new JSONObject(day_name.getString(key1));
                            JSONArray key2_data = new JSONArray(key1_data.getString(key2));
                            JSONArray key3_data = new JSONArray(key1_data.getString(key3));
                            reqToGetRecipeName(key2_data,fdn.format(new Date()),time1,false);
                            reqToGetRecipeName(key3_data,fdn.format(new Date()),time2,true);
                            todayMealDietChartModelAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            processBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    processBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(),"Connectivity Issue", Toast.LENGTH_SHORT).show();
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("dietitian_id",DataFromDatabase.dietitian_id);
                data.put("client_id",DataFromDatabase.client_id);
                data.put("type","1");
                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void reqWithRecipeNameCall(String meal_name,String key,String time){
        processBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, String.format("%sgetDietChart.php",DataFromDatabase.ipConfig) ,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        processBar.setVisibility(View.GONE);
                        try {
                            JSONObject data_response = new JSONObject(response);
                            String data = data_response.getString("data");
                            JSONArray jsonArrayData = new JSONArray(data);
                            JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);
                            //for day_name for e.g sunday monday tuesday etc..
                            Format fdn = new SimpleDateFormat("EEEE");
                            JSONObject day_name = new JSONObject(jsonObjectData.getString(fdn.format(new Date()).toLowerCase()));
                            JSONObject meal_name_data = new JSONObject(day_name.getString(meal_name));
                            JSONArray key_data = new JSONArray(meal_name_data.getString(key));
                            reqToGetRecipeName(key_data,fdn.format(new Date()),time,true);
                            todayMealDietChartModelAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            processBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    processBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(),"Connectivity Issue", Toast.LENGTH_SHORT).show();
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("dietitian_id",DataFromDatabase.dietitian_id);
                data.put("client_id","1");
                data.put("type","1");
                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void reqWithoutRecipeNameCall(String meal_name,String key,String time){
        processBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, String.format("%sgetDietChart.php",DataFromDatabase.ipConfig) ,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        processBar.setVisibility(View.GONE);
                        try {
                            JSONObject data_response = new JSONObject(response);
                            String data = data_response.getString("data");
                            JSONArray jsonArrayData = new JSONArray(data);
                            if (jsonArrayData.isNull(0)){
                                Toast.makeText(requireActivity(),"Data is empty",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);
                            //for day_name for e.g sunday monday tuesday etc..
                            Format fdn = new SimpleDateFormat("EEEE");
                            JSONObject day_name = new JSONObject(jsonObjectData.getString(fdn.format(new Date()).toLowerCase()));
                            JSONObject meal_name_data = new JSONObject(day_name.getString(meal_name));
                            JSONArray key_data = new JSONArray(meal_name_data.getString(key));
                            for (int i = 0; i < key_data.length(); i++) {
                                todayMealDietChartModel = new TodayMealDietChartModel(getRecipeName(key_data.getString(i)),fdn.format(new Date()),time);
                                viewListModels.add(todayMealDietChartModel);
                            }
                            recyclerView.setAdapter(todayMealDietChartModelAdapter);
                            todayMealDietChartModelAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            processBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    processBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(),"Connectivity Issue", Toast.LENGTH_SHORT).show();
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("dietitian_id",DataFromDatabase.dietitian_id);
                data.put("client_id",DataFromDatabase.client_id);
                data.put("type","1");
                return data;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void reqToGetRecipeName(JSONArray key_data,String day,String time,Boolean flag) {
        processBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, String.format("%sgetDietChart.php",DataFromDatabase.ipConfig) ,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        processBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArrayData = new JSONArray(response);
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                todayMealDietChartModel = new TodayMealDietChartModel(jsonObjectData.getString("drecipe_name"),day,time);
                                viewListModels.add(todayMealDietChartModel);
                            }
                            if (flag)
                                todayMealDietChartModelAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            processBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    processBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(),"Connectivity Issue", Toast.LENGTH_SHORT).show();
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("dietitian_id",DataFromDatabase.dietitian_id);
                data.put("client_id",DataFromDatabase.client_id);
                data.put("type","2");
                data.put("recipe_id[]",key_data.toString().replace("[","").replace("]",""));
                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public static String getRecipeName(String n){
        switch (n){
            case "1": return "Spiced Jerusalem Artichokes";
            case "2": return "Crispy Hot Wings";
            case "3": return "Pea and Ham Soup with Chilli and Cumin";
            case "4": return "Fennel and Seafood Salad";
            case "5": return "Mutton Rara";
            case "6": return "Black Lentil and Split Chickpea Dhal";
            case "7": return "Aloo Bangun";
            case "8": return "Kharee";
            case "9": return "Seafood Curry";
            case "10": return "Jeera Chaul";
            case "11": return "Chicken Dopiaza";
            case "12": return "Aloo Muttar";
            case "13": return "Falia di Sabjee";
            case "14": return "Dhokla";
            case "15": return "Prawn Poori";
            case "16": return "Mango Lassi";
            case "17": return "Spiced Rice";
            case "18": return "Cockle Curry";
            case "19": return "Lamb Karahi Curry";
            case "20": return "Avocado Yoghurt";
            case "21": return "Chicken Tikka";
            case "22": return "Mustard Raita";
            case "23": return "Luchi";
            case "24": return "Garlic Naan";
            case "25": return "Chicken Pops";
            case "26": return "Healthy Chicken Korma";
            case "27": return "Brown Lentil Dhal";
            case "28": return "Turnip Chana Dhal";
            case "29": return "Piri Piri Sauce";
            case "30": return "Dhum Aloo";
            case "31": return "Red Chilli Sauce";
            case "32": return "Coriander Fish";
            case "33": return "Chettinad Chicken";
            case "34": return "Coastal Squid Curry";
            case "35": return "Sooke Moong Dhal";
            case "36": return "Lamb Burgers";
            case "37": return "Makki Di Roti";
            case "38": return "Spicy Lemon Roasted Potatoes";
            case "39": return "Ghee";
            case "40": return "Creamy Mushroom Curry";
            case "41": return "Thari Walee Lamb";
            case "42": return "Hot Fiery Chicken Wings";
            case "43": return "Aloo Gobi";
            case "44": return "Gol Guppa";
            case "45": return "Kachumber Salad";
            case "46": return "Nimbu ka Achaar (Lemon Pickle)";
            case "47": return "Achaari Bangun";
            case "48": return "Nariyal Chicken";
            case "49": return "Cucumber Raita";
            case "50": return "Punjabi Style Stuffed Karela";
            case "51": return "Spiced Fish Pie";
            case "52": return "South Indian Crab Curry";
            case "53": return "Amchoor Stuffed Bangun";
            case "54": return "Rajasthani Muttar Kachori";
            case "55": return "Saag Paneer";
            case "56": return "Tamarind Rice";
            case "57": return "Tomato Base Restaurant Sauce";
            case "58": return "Dal Bhat";
            case "59": return "Mint Chutney";
            case "60": return "Chapli Kebabs";
            case "61": return "Indian Summer Greens";
            case "62": return "Chicken Pathia";
            case "63": return "Lemon Rice";
            case "64": return "Chicken Salad with Pink Grapefruit and Cumin Dress";
            case "65": return "Cabbage Thoran";
            case "66": return "Jeera Pani";
            case "67": return "Maacher Jhol";
            case "68": return "Fennel and Tomato Shorba";
            case "69": return "Muttar Paneer";
            case "70": return "Tandoori Prawn Skewers";
            case "71": return "Seekh Kebabs";
            case "72": return "Chicken Chaat";
            case "73": return "Pumpkin Curry Recipe";
            case "74": return "Bloody Mary Indian style";
            case "75": return "Fried Okra";
            case "76": return "Jeera Raita";
            case "77": return "Murgh Makhani";
            case "78": return "Bonda's";
            case "79": return "Quick Keralan Pandi Curry";
            case "80": return "Indo-Chinese Ribs";
            case "81": return "Asparagus and Pea Soup with Parsnip Crisps";
            case "82": return "Cardamom Banana Lassi";
            case "83": return "Chicken Burrito";
            case "84": return "Punjabi Tomato Relish";
            case "85": return "Pitta Bread with Carom Seeds";
            case "86": return "Coorg Pandi Curry";
            case "87": return "Raajma";
            case "88": return "Ginger chutney";
            case "89": return "Spicy Lamb Chops";
            case "90": return "Mint and Coriander Chutney";
            case "91": return "Lamb with Shallots";
            case "92": return "Parantha";
            case "93": return "Anda di Purjee (Bhurji)";
            case "94": return "Easy Chicken Curry";
            case "95": return "Homemade Paneer";
            case "96": return "Vegetable Biryani";
            case "97": return "Fish Kebabs";
            case "98": return "Coconut and Vanilla Ice-cream";
            case "99": return "Hot Sticky Wings";
            case "100": return "Spicy Courgette and Pea Fritter";
            case "101": return "Tandoori Paneer";
            case "102": return "Tangy Fried Okra";
            case "103": return "Indian Duck Breast with Tamarind Jam";
            case "104": return "Crab and Prawn Cakes";
            case "105": return "Mango Bellini";
            case "106": return "Mint Raita";
            case "107": return "Boonthi Raita";
            case "108": return "Chicken Jalfrezi";
            case "109": return "Lamb Kofta";
            case "110": return "Corn and Pomegranate Kachumber";
            case "111": return "Keema Burrito";
            case "112": return "Pistachio chicken";
            case "113": return "Patra Ni Machi";
            case "114": return "Roti";
            case "115": return "Asparagus with Indian Spices";
            case "116": return "Paneer Pakora";
            case "117": return "Tomato Kachumber Salad";
            case "118": return "Prawn Pakora";
            case "119": return "Courgette Chutney";
            case "120": return "Squid Pakora";
            case "121": return "Dirty Mango Lassi";
            case "122": return "Punjabi Piyaz";
            case "123": return "Sooka Masala Lamb";
            case "124": return "Mixed Vegetable Sabjee";
            case "125": return "Lamb Bhuna";
            case "126": return "Shammi Kebabs";
            case "127": return "Samosa";
            case "128": return "Goan Fish Curry";
            case "129": return "Gobi Masallum";
            case "130": return "Smoked Bangun Partha";
            case "131": return "Aloo Tikki";
            case "132": return "Indian Scampi with Masala Chips";
            case "133": return "Thari Wala Chicken";
            case "134": return "Spiced Potato Stuffed Peppers";
            case "135": return "Amla and Beetroot Tikki";
            case "136": return "Masala Prawns";
            case "137": return "Tandoori Chicken";
            case "138": return "Lamb Kebabs";
            case "139": return "Sweet Potato and Spinach Curry";
            case "140": return "Beetroot and Coconut Samosa";
            case "141": return "Mango Kachumber";
            case "142": return "Pumpkin Halwa";
            case "143": return "Strawberry Lassi";
            case "144": return "Pilchard Curry";
            case "145": return "Pigeon Curry";
            case "146": return "Gurkha Chicken Curry";
            case "147": return "Chicken Balti";
            case "148": return "Sindhi Masala Fish";
            case "149": return "Chicken Pakora";
            case "150": return "Courgette and Onion Pakora";
            case "151": return "Mint and Coriander Dip";
            case "152": return "Vegetable Pakora";
            case "153": return "Hot and Spicy BBQ Ribs";
            case "154": return "Melon Lassi";
            case "155": return "Hot Nariyal Machchi";
            case "156": return "Roasties with a Sesame Crunch";
            case "157": return "Morel Mushroom Pilau";
            case "158": return "Patoora or Bhatura";
            case "159": return "Poori";
            case "160": return "Imlee Chutney";
            case "161": return "Saag Walee Roti";
            case "162": return "Spiced Tomato Chutney";
            case "163": return "Mushy Pea Curry";
            case "164": return "Saunth ki Chutney";
            case "165": return "Curried Butternut Squash Soup";
            case "166": return "Plain Aloo Tikki";
            case "167": return "Karahi Paneer";
            case "168": return "Mushroom Biryani";
            case "169": return "Chicken Tikka Masala";
            case "170": return "Hariyali Chicken";
            case "171": return "Masala Machchi";
            case "172": return "Pudla";
            case "173": return "Bombay Aloo";
            case "174": return "Spicy Potato Wedges";
            case "175": return "Hot Coconut Vegetables";
            case "176": return "Avocado Salsa";
            case "177": return "Aloo Tama Bodi";
            case "178": return "Fish Amritsari";
            case "179": return "Athrak Soup";
            case "180": return "Xacuti Chicken";
            case "181": return "Stuffed Okra in a Yoghurt Sauce";
            case "182": return "Tandoori Fish";
            case "183": return "Amazing Nut Roast";
            case "184": return "Aloo Parantha";
            case "185": return "Red Lentil Dhal";
            case "186": return "Keralan Fish Curry";
            case "187": return "Jeera Aloo";
            case "188": return "Broccoli Tikki";
            case "189": return "Homemade Yoghurt";
            case "190": return "Monkfish Curry";
            case "191": return "Keema";
            case "192": return "Saffron Potatoes";
            case "193": return "Mango Chutney";
            case "194": return "Kucha Gosht ki Biryani";
            case "195": return "Sholay";
            case "196": return "Fish Pakora";
            case "197": return "Lamb Madras";
            case "198": return "Laal Maas";
            case "199": return "Lamb Pitta Toastie";
            case "200": return "Kale and Chickpea Curry";
            case "201": return "Mackerel Fry";
            case "202": return "Saag Aloo";
            case "203": return "Plain Basmati Rice";
            case "204": return "Radish and Lemon Salad";
            case "205": return "Dhal Makhani";
            case "206": return "Indian Slaw";
            case "207": return "Vegetable Pilau";
            case "208": return "Soya curry";
            case "209": return "Paneer with Mixed Vegetables";
            case "210": return "Moong Masoor di Dhal";
            case "211": return "Spiced Cauliflower Rice";
            case "212": return "Lamb Dhansak";
            case "213": return "Zambezi Baked Sea Bream";
            case "214": return "Marrow Curry";
            case "215": return "Green Chilli Sauce";
            case "216": return "Sarson Ka Saag";
            case "217": return "Sprouts with Cumin and Mustard";
            case "218": return "Kedgeree";
            case "219": return "Tamarind Ribs";
            case "220": return "Tangy Tamarind Prawns";
            case "221": return "Halibut with Indian spices";
            case "222": return "Pomegranate Raita";
            case "223": return "Paneer Makhani";
            case "224": return "Tandoori Style Pigeon Breast";
            case "225": return "Fennel Laccha Paratha";
            case "226": return "Lamb Jalfrezi";
            case "227": return "Sausage and Courgette Curry";
            case "228": return "Kofta Bites";
            case "229": return "Crispy Paneer Fingers";
            case "230": return "Amla Chutney";
            default: return "Recipe name";
        }
    }
}