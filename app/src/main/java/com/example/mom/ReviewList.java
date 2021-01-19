package com.example.mom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewList extends AppCompatActivity {

    private static String TAG2 = "phpquerytest";

    private static final String TAG_JSON="Parking review";
    private static final String TAG_PARKING_REVIEWID = "parking_reviewID";
    private static final String TAG_PARKING_REVIEW = "parking_review";


    ArrayList<HashMap<String,String>> mArrayList;
    ListView mListViewList;
    String mJsonString;

    private static String IP_ADDRESS = "211.217.28.193";
//    private static String IP_ADDRESS = "192.168.43.240";
    private static String TAG = "phptest";
    private String parking_num="1";
    public int currentIndex;
    public float rate;

    Button reviewSet;
    EditText reviewEDT;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("Index",1);
        rate = intent.getFloatExtra("rate",0);
        setContentView(R.layout.reviewlist);

        mListViewList = (ListView) findViewById(R.id.reviewView);


        GetData task2 = new GetData();
        parking_num = Integer.toString(currentIndex);
        task2.execute(parking_num);

        mArrayList = new ArrayList<>();



        reviewEDT = (EditText) findViewById(R.id.reviewEDT);

        reviewSet = (Button) findViewById(R.id.reviewSend);
        reviewSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(reviewEDT.getWindowToken(), 0);
                EditText reviewText = findViewById(R.id.reviewEDT);
                String reviewString = reviewText.getText().toString();
                reviewText.setText("");
                String review_insert = reviewString;
                RatingBar review_rating = findViewById(R.id.review_rating);
                float userRate = review_rating.getRating();
                rate = (rate * mArrayList.size() + userRate) / (mArrayList.size() + 1);
                String newRate = String.format("%.1f",rate);
                Log.i(this.getClass().getName(),newRate);
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert_parking_review.php",parking_num ,review_insert, newRate);

            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewList.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);


        }


        @Override
        protected String doInBackground(String... params) {

            String parking_review = (String) params[2];
            String parking_num = (String) params[1];
            String parking_averageRating=(String) params[3];

            String serverURL = (String) params[0];

            String postParameters = "parking_num="+parking_num+"&parking_review=" + parking_review + "&parking_averageRating="+parking_averageRating;



            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();

                Log.i(this.getClass().getName(),"-------------------------" + sb.toString());
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }





    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewList.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {

//                    mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String parking_num = params[0];
//            String parking_num=params[1];
//            String postParameters = "parking_num"+parking_num;
//           String parking_num=params[1];
            String serverURL="http://211.217.28.193/query_parking_review.php";
            String postParameters = "parking_num="+parking_num;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG2, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String parking_reviewID = item.getString(TAG_PARKING_REVIEWID);
                String parking_review = item.getString(TAG_PARKING_REVIEW);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_PARKING_REVIEWID,parking_reviewID);
                hashMap.put(TAG_PARKING_REVIEW,parking_review);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    ReviewList.this,mArrayList,R.layout.item_list,
                    new String[]{TAG_PARKING_REVIEWID,TAG_PARKING_REVIEW},
                    new int[]{R.id.textView_list_id,R.id.textView_list_name}
            );

            mListViewList.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}