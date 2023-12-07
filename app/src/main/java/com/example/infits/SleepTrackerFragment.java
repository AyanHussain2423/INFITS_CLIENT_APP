package com.example.infits;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SleepTrackerFragment extends Fragment {

    String sleep;
    String hours, minutes, secs;

    Button setalarm, startcycle, endcycle;
    ImageView imgback, reminder;
    TextView texttime, tvDuration;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String timerTime;

    private int seconds;
    private boolean running;
    private boolean wasRunning;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SleepTrackerFragment() {

    }

    public static SleepTrackerFragment newInstance(String param1, String param2) {
        SleepTrackerFragment fragment = new SleepTrackerFragment();
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(getArguments() != null && getArguments().getBoolean("notification") /* coming from notification */) {
                    startActivity(new Intent(getActivity(),DashBoardMain.class));
                    requireActivity().finish();
                } else {
                    Navigation.findNavController(requireActivity(), R.id.trackernav).navigate(R.id.action_sleepTrackerFragment_to_dashBoardFragment);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_tracker, container, false);

        RecyclerView pastActivity = view.findViewById(R.id.past_activity);

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> datas = new ArrayList<>();

        //String url = String.format("%spastActivitySleep.php",DataFromDatabase.ipConfig);
        String url = "https://infits.in/androidApi/pastActivitySleep.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, response -> {
            try {
                Log.d("response123",response.toString());
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("sleep");
                for (int i = 0;i<=jsonArray.length();i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    String data = object.getString("hrsSlept");
                    String date = object.getString("date");
                    dates.add(date);
                    datas.add(data);
                    System.out.println(datas.get(i));
                    System.out.println(dates.get(i));
                }
                AdapterForPastActivity ad = new AdapterForPastActivity(getContext(),dates,datas, Color.parseColor("#9C74F5"));
                pastActivity.setLayoutManager(new LinearLayoutManager(getContext()));
                pastActivity.setAdapter(ad);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {
            Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Error",error.toString());
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("clientuserID",DataFromDatabase.clientuserID);
                return data;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);

        PowerManager powerManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            powerManager = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);
        }
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        imgback = view.findViewById(R.id.imgback);
        setalarm = view.findViewById(R.id.setalarm);
        startcycle = view.findViewById(R.id.startcycle);
        endcycle = view.findViewById(R.id.endcycle);
        texttime = view.findViewById(R.id.texttime);
        tvDuration = view.findViewById(R.id.tvDuration);
        reminder = view.findViewById(R.id.reminder);

        tvDuration.setVisibility(View.INVISIBLE);

        if(getArguments() != null && getArguments().getBoolean("notification")) {
            texttime.setText(getArguments().getString("sleepTime"));
            tvDuration.setVisibility(View.VISIBLE);
            tvDuration.setText("You slept for " + getArguments().getString("sleepTime"));
        }

        if (foregroundServiceRunning()){
            endcycle.setVisibility(View.VISIBLE);
            startcycle.setVisibility(View.GONE);
        }

        if (!foregroundServiceRunning()){
            endcycle.setVisibility(View.GONE);
            startcycle.setVisibility(View.VISIBLE);
        }

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter("com.example.infits.sleep"));

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm");
        String time = simpleDateFormat.format(calendar.getTime());

        if(savedInstanceState != null) {
            savedInstanceState.getInt("seconds");
            savedInstanceState.getBoolean("running");
            savedInstanceState.getBoolean("wasRunning");
        }

//        runTimer();


        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments() != null && getArguments().getBoolean("notification") /* coming from notification */) {
                    startActivity(new Intent(getActivity(),DashBoardMain.class));
                    requireActivity().finish();
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_sleepTrackerFragment_to_dashBoardFragment);
                }
            }
        });

        reminder.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_sleepTrackerFragment_to_sleepReminderFragment));

        setalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mClockIntent);
            }
        });


        startcycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                SimpleDateFormat sleepTime = new SimpleDateFormat("yyyy-MM-dd H:m:s");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref",Context.MODE_PRIVATE);

                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("sleepTime", sleepTime.format(date));
                myEdit.commit();
                calendar.add(Calendar.HOUR_OF_DAY, 8);
                tvDuration.setVisibility(View.VISIBLE);
                SimpleDateFormat sim = new SimpleDateFormat("HH:mm");
                tvDuration.setText("Optimal time to wake up is "+sim.format(calendar.getTime()));
                startcycle.setVisibility(View.GONE);
                endcycle.setVisibility(View.VISIBLE);
                if (!foregroundServiceRunning()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent intent = new Intent(getActivity(), StopWatchService.class);
                        getActivity().startForegroundService(new Intent(getActivity(), StopWatchService.class));
                    }
                    sleep = "";
                    getActivity().registerReceiver(broadcastReceiver, new IntentFilter("com.example.infits.sleep"));
                }
            }
        });

        endcycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = sleep;
                endcycle.setVisibility(View.GONE);
                startcycle.setVisibility(View.VISIBLE);
                Intent i = new Intent(getActivity(),StopWatchService.class);
                i.putExtra("status",true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getActivity().getApplicationContext().stopService(new Intent(getActivity(),StopWatchService.class));
                }
                getActivity().unregisterReceiver(broadcastReceiver);
                tvDuration.setText("You slept for " +sleep);

                updateInAppNotifications(hours, minutes, secs);

                SharedPreferences notificationPrefs = requireActivity().getSharedPreferences("notificationDetails",MODE_PRIVATE);
                boolean sleepNotificationPermission = notificationPrefs.getBoolean("sleepSwitch", false);

                if(sleepNotificationPermission) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.d("sleep", "permitted");
                        NotificationChannel channel = new NotificationChannel(
                                "SleepChannelId",
                                "Sleep Tracker",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        channel.setLightColor(Color.BLUE);
                        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

                        NotificationManager manager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.createNotificationChannel(channel);

                        String sleptFor = "You slept for " + hours + " hours, " + minutes + " minutes, " + secs + " seconds.";


                        Intent intent = new Intent(requireContext(), SplashScreen.class);
                        intent.putExtra("notification", "sleep");
                        intent.putExtra("hours", hours);
                        intent.putExtra("minutes", minutes);
                        intent.putExtra("secs", secs);

                        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "SleepChannelId");
                        Notification goalReachedNotification = builder.setOngoing(false)
                                .setSmallIcon(R.mipmap.logo)
                                .setContentTitle("Sleep Tracker")
                                .setContentText(sleptFor)
                                .setChannelId("SleepChannelId")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();

                        manager.notify(2, goalReachedNotification);
                    }
                }

                //String url=String.format("%ssleepTracker.php",DataFromDatabase.ipConfig);
                String url = "https://infits.in/androidApi/sleepTracker.php";
                StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
                    if (response.equals("updated")){
                        Toast.makeText(getActivity(), "Good Morning", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        System.out.println(response);
                        Toast.makeText(getActivity(), "Not working", Toast.LENGTH_SHORT).show();
                    }
                },error -> {
                    Toast.makeText(requireContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                })
                {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

                        String sleepTime = sh.getString("sleepTime", "");

                        Date date = new Date();
                        String pat = "dd-MM-yyyy H:m:s";
                        SimpleDateFormat sdf = new SimpleDateFormat(pat);
                        Map<String,String> data = new HashMap<>();
                        data.put("userID",DataFromDatabase.clientuserID);
                        data.put("sleeptime",sleepTime);
                        data.put("waketime", sdf.format(date));
                        data.put("timeslept",time);
                        System.out.println("hi"+time);
                        data.put("goal", "8");
                        data.put("hrsslept", hours);
                        data.put("minsslept", minutes);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                        LocalDateTime now = LocalDateTime.now();
                        data.put("date",dtf.format(now));
                        Log.d("sleep", "userId: " + DataFromDatabase.clientuserID);
                        Log.d("sleep", "sleepTime: " + sleepTime);
                        Log.d("sleep", "wakeTime: " + sdf.format(date));
                        Log.d("sleep", "timeSlept: " + time);
                        Log.d("sleep", "goal: " + 8);
                        Log.d("sleep", "hrsSlept: " + hours);
                        Log.d("sleep", "minSlept: " + minutes);
                        Log.d("sleep",dtf.format(now));
                        return data;
                    }
                };
                Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
                sleep = "";

                SharedPreferences sleepPrefs = requireActivity().getSharedPreferences("sleepPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sleepPrefs.edit();

                editor.putString("hours", hours);
                editor.putString("minutes", minutes);

                editor.apply();
            }
        });
        return view;
    }

    private void updateInAppNotifications(String hours, String minutes, String secs) {
        SharedPreferences inAppPrefs = requireActivity().getSharedPreferences("inAppNotification", MODE_PRIVATE);
        SharedPreferences.Editor inAppEditor = inAppPrefs.edit();
        inAppEditor.putBoolean("newNotification", true);
        inAppEditor.apply();

        //String inAppUrl = String.format("%sinAppNotifications.php", DataFromDatabase.ipConfig);
        String inAppUrl = "https://infits.in/androidApi/inAppNotifications.php";

        String type = "sleep";
        String text = "You slept for " + hours + " hours, " + minutes + " minutes, " + secs + " seconds.";

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.format(date);

        StringRequest inAppRequest = new StringRequest(
                Request.Method.POST,
                inAppUrl,
                response -> {
                    if (response.equals("inserted")) Log.d("SleepTrackerFragment", "success");
                    else Log.d("SleepTrackerFragment", "failure");
                },
                error -> Log.e("SleepTrackerFragment",error.toString())
        ) {
            @NotNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();

                data.put("clientID", DataFromDatabase.clientuserID);
                data.put("type", type);
                data.put("text", text);
                data.put("date", String.valueOf(date));

                return data;
            }
        };
        Volley.newRequestQueue(requireContext()).add(inAppRequest);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            sleep = intent.getStringExtra("sleep");
            hours = sleep.substring(0, 2);
            minutes = sleep.substring(3, 5);
            secs = sleep.substring(6);
            Log.i("StepTracker","Countdown seconds remaining:" + sleep);
            texttime.setText(sleep);
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (StopWatchService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

}