package com.example.infits;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.abhinav.progress_view.ProgressData;
import com.abhinav.progress_view.ProgressView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class CalorieTrackerFragment extends Fragment {
    TextView carbsTV,fiberTV,proteinTV,fatTV;
    ProgressView progressView1,progressView2,progressView3,progressView4;
    ProgressView carbPB,fibrePB,proteinPB,fatPB;


    CircularProgressIndicator circularProgressIndicatorCC,circularProgressIndicatorCB;

    View bottomSheetN;
    ImageView imgBack;

    ////int calgoal;
    // int calConsumed;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    CardView calorieConsumed,calorieBurnt,addBreakfast,addLunch,addSnacks,addDinner,setgoalmicro;
    private String mParam2;

    private int calConsumed;
    private int calgoal;
    private int CCGoal,CBGoal;

    private float CARBGoal,FIBERGoal,PROTEINGoal,FATGoal;


    public  CalorieTrackerFragment(){
        // Required Empty Constructor.
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalorieTrackerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalorieTrackerFragment newInstance(String param1, String param2) {
        CalorieTrackerFragment fragment = new CalorieTrackerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);

        //String url = String.format("%scalorieTracker.php", DataFromDatabase.ipConfig);
        String url = "https://infits.in/androidApi/calorieTracker.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("CalTracker Data Bro", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject("Data");

                        JSONObject goalsObject = dataObject.getJSONObject("Goals");
                        String calorieconsumegoal = goalsObject.getString("CalorieConsumeGoal");
                        String calorieburngoal = goalsObject.getString("CalorieBurnGoal");
                        String carbgoal = goalsObject.getString("CarbsGoal");
                        String fibergoal = goalsObject.getString("FiberGoal");
                        String proteingoal = goalsObject.getString("ProteinGoal");
                        String fatgoal = goalsObject.getString("FatsGoal");

                        String calorieBurnt = dataObject.getString("CalorieBurnt");

                        JSONObject valuesObject = dataObject.getJSONObject("Values");
                        String calories = valuesObject.getString("Calories");
                        String carbs = valuesObject.getString("carbs");
                        String fiber = valuesObject.getString("fiber");
                        String protein = valuesObject.getString("protein");
                        String fat = valuesObject.getString("fat");

                        //All prasing
                        calConsumed = Integer.parseInt(calories);
                        CCGoal = Integer.parseInt(calorieconsumegoal);
                        CBGoal = Integer.parseInt(calorieburngoal);

                        circularProgressIndicatorCC.setProgress(calConsumed, CCGoal);
                        circularProgressIndicatorCB.setProgress(760,CBGoal);
                        ProgressData progressData1 = new ProgressData("view1", Float.parseFloat(carbs), Float.parseFloat(carbgoal), R.color.progressGreenColor);
                        int caval = (Integer.parseInt(carbgoal)-Integer.parseInt(carbs));
                        carbsTV.setText(caval + "g ");
                        carbPB.setData(progressData1);
                        ProgressData progressData2 = new ProgressData("view1",Float.parseFloat(fiber),Float.parseFloat(fibergoal),R.color.progressRedColor);
                        int fival = (Integer.parseInt(fibergoal)-Integer.parseInt(fiber));
                        fiberTV.setText(fival + "g ");
                        fibrePB.setData(progressData2);

                        ProgressData progressData3 = new ProgressData("view1",Float.parseFloat(protein),Float.parseFloat(proteingoal),R.color.progressPurpleColor);
                        int prval = (Integer.parseInt(proteingoal)-Integer.parseInt(protein));
                        proteinTV.setText(prval + "g ");
                        proteinPB.setData(progressData3);

                        ProgressData progressData4 = new ProgressData("view1",Float.parseFloat(fat),Float.parseFloat(fatgoal),R.color.progressBlueColor);
                        int faval = (Integer.parseInt(fatgoal)-Integer.parseInt(fat));
                        fatTV.setText(faval + "g ");
                        fatPB.setData(progressData4);


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

        hooks(view);
        //ProgressData progressData1 = new ProgressData("view1",60f,100f,R.color.progressGreenColor);
        // progressView1.setData(progressData1);

        //ProgressData progressData2 = new ProgressData("view1",60f,100f,R.color.progressRedColor);
        // progressView2.setData(progressData2);

        // ProgressData progressData3 = new ProgressData("view1",60f,100f,R.color.progressPurpleColor);
        //progressView3.setData(progressData3);

        // ProgressData progressData4 = new ProgressData("view1",60f,100f,R.color.progressBlueColor);
        // progressView4.setData(progressData4);

        //circularProgressIndicatorCC.setProgress(calConsumed,calgoal);
        // circularProgressIndicatorCB.setProgress(7000,10000);

        BottomSheetBehavior.from(bottomSheetN).setPeekHeight(350);
        BottomSheetBehavior.from(bottomSheetN).setState(BottomSheetBehavior.STATE_COLLAPSED);

        System.out.println(calConsumed);
        System.out.println(calgoal);

        calorieConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_to_calorieConsumedFragment);
            }
        });
        calorieBurnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_to_calorieBurntFragment);
            }
        });

        addBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_tocalorieAddBreakFastFragment); // To be continued...
            }
        });
        addLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_tocalorieAddLunchFragment); // To be continued...
            }
        });
        addSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_tocalorieAddSnacksFragment); // To be continued...
            }
        });
        addDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_tocalorieAddDinnerFragment);// To be continued...
            }
        });

        setgoalmicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_to_caloriesetgoalFragment);
            }
        });

//        dailyMeals = new ArrayList<>();
//        goalValue = 0f;
//        calorieValue = 0f;
//        proteinValue = 0f;
//        fibreValue = 0f;
//        carbValue = 0f;
//        fatValue = 0f;
//
//        getDailyMeals();

//        log.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_to_foodDetailsFragment));


//        reminder.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_calorieTrackerFragment_to_calorieReminderFragment));

        return view;
    }

    private void hooks(View view) {
//        log = view.findViewById(R.id.log);
        imgBack = view.findViewById(R.id.imgback);
//        reminder = view.findViewById(R.id.reminder);
//        dailyMealsRecycle = view.findViewById(R.id.dailyMealsRecycle);
//        kcalEatenTV = view.findViewById(R.id.kcalEaten);
//        kcalLeftTV = view.findViewById(R.id.kcalLeft);
//        kcalPB = view.findViewById(R.id.calorie_progress_bar);
//
        carbsTV = view.findViewById(R.id.carbsvallefttext);
        fiberTV = view.findViewById(R.id.fibervallefttext);
        proteinTV = view.findViewById(R.id.proteinvallefttext);
        fatTV = view.findViewById(R.id.fatvallefttext);
        carbPB = view.findViewById(R.id.progressView1);
        fibrePB = view.findViewById(R.id.progressView2);
        proteinPB = view.findViewById(R.id.progressView3);
        fatPB = view.findViewById(R.id.progressView4);
        progressView1 = view.findViewById(R.id.progressView1);
        progressView2 = view.findViewById(R.id.progressView2);
        progressView3 = view.findViewById(R.id.progressView3);
        progressView4 = view.findViewById(R.id.progressView4);
        circularProgressIndicatorCC = view.findViewById(R.id.circular_progressCC);
        circularProgressIndicatorCB = view.findViewById(R.id.circular_progressCB);
        bottomSheetN = view.findViewById(R.id.bottomSheetN);
        addBreakfast = view.findViewById(R.id.add_breakfast);
        addLunch = view.findViewById(R.id.add_lunch);
        addSnacks = view.findViewById(R.id.add_snack);
        addDinner = view.findViewById(R.id.add_dinner);
        calorieConsumed=view.findViewById(R.id.calorieConsumed);
        calorieBurnt=view.findViewById(R.id.calorieBurnt);
        setgoalmicro = view.findViewById(R.id.setgoalformicro);
        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }
}
