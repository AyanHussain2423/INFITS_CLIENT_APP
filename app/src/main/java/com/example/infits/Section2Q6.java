package com.example.infits;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Section2Q6#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Section2Q6 extends Fragment {

    ImageButton imgBack;
    Button yes, no;
    String yesno=null;

    Button nextbtn;
    TextView backbtn, medtv;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Section2Q6() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Section2Q6.
     */
    // TODO: Rename and change types and number of parameters
    public static Section2Q6 newInstance(String param1, String param2) {
        Section2Q6 fragment = new Section2Q6();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section2_q6, container, false);

        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        imgBack = view.findViewById(R.id.imgback);
        nextbtn = view.findViewById(R.id.nextbtn);
        backbtn = view.findViewById(R.id.backbtn);

        medtv = view.findViewById(R.id.textView77);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                yes.setBackgroundColor(Color.rgb(0,161,45));
                no.setBackgroundColor(Color.rgb(255,255,255));
                yesno = "Yes";
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                no.setBackgroundColor(Color.rgb(183,0,0));
                yes.setBackgroundColor(Color.rgb(255,255,255));
                yesno = "No";
            }
        });


        //String url = "http://192.168.0.114/sectionRead.php";
        String url = "https://infits.in/androidApi/sectionRead.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("Checking", "Checking1");
            System.out.println(DataFromDatabase.clientuserID);
            System.out.println(response);

            JSONObject jsonResponse = null;

            try {
                jsonResponse = new JSONObject(response);
                JSONArray cast = jsonResponse.getJSONArray("answer");
                JSONObject actor = cast.getJSONObject(0);
                String answer = actor.getString("answer");

                if(answer.equals("Yes")) yes.performClick();
                else if(answer.equals("No")) no.performClick();




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("Data", error.toString().trim());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> dataVol = new HashMap<>();
                Log.e("Checking", "Checking");
                dataVol.put("clientuserID", DataFromDatabase.clientuserID);
                dataVol.put("table", "section2Q6");
                return dataVol;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        Volley.newRequestQueue(getActivity()).add(stringRequest);



        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataSectionTwo.ongoingMed = yesno;
                DataSectionTwo.s2q6 = medtv.getText().toString();
                if (yesno.equals(null))
                    Toast.makeText(getContext(), "Select yes/no", Toast.LENGTH_SHORT).show();
                else {
                    ConsultationFragment.psection2 += 1;



                    //Updating Sections Progress
                    //String urlProgress = "http://192.168.0.114/sectionProgressUpdate.php";
                    String urlProgress = "https://infits.in/androidApi/sectionProgressUpdate.php";
                    StringRequest stringRequestPro = new StringRequest(Request.Method.POST, urlProgress, response -> {
                        Log.e("Checking", "Checking1");

                    }, error -> {
                        Log.d("Data", error.toString().trim());
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> dataVol = new HashMap<>();
                            Log.e("Checking", "Checking");
                            dataVol.put("clientuserID", DataFromDatabase.clientuserID);
                            dataVol.put("newAnswer", Integer.toString(ConsultationFragment.psection2));
                            dataVol.put("sectionNo", "section2");


                            return dataVol;
                        }
                    };
                    stringRequestPro.setRetryPolicy(new RetryPolicy() {
                        @Override
                        public int getCurrentTimeout() {
                            return 50000;
                        }

                        @Override
                        public int getCurrentRetryCount() {
                            return 50000;
                        }

                        @Override
                        public void retry(VolleyError error) throws VolleyError {

                        }
                    });
                    Volley.newRequestQueue(getActivity()).add(stringRequestPro);






                    Navigation.findNavController(v).navigate(R.id.action_section2Q6_to_section2Q7);

                    //String url = "http://192.168.0.114/sectionUpdate.php";
                    String url = "https://infits.in/androidApi/sectionUpdate.php";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                        Log.e("Checking", "Checking1");

                    }, error -> {
                        Log.d("Data", error.toString().trim());
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> dataVol = new HashMap<>();
                            Log.e("Checking", "Checking");
                            dataVol.put("clientuserID", DataFromDatabase.clientuserID);
                            dataVol.put("newAnswer", yesno);
                            dataVol.put("table", "section2Q6");


                            return dataVol;
                        }
                    };
                    stringRequest.setRetryPolicy(new RetryPolicy() {
                        @Override
                        public int getCurrentTimeout() {
                            return 50000;
                        }

                        @Override
                        public int getCurrentRetryCount() {
                            return 50000;
                        }

                        @Override
                        public void retry(VolleyError error) throws VolleyError {

                        }
                    });
                    Volley.newRequestQueue(getActivity()).add(stringRequest);
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConsultationFragment.psection2>0)
                    ConsultationFragment.psection2-=1;
                requireActivity().onBackPressed();
            }
        });

        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;

    }
}