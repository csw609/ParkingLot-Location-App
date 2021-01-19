package com.example.mom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
//    private static String IP_ADDRESS = "192.168.43.240";
    private static String IP_ADDRESS = "192.168.0.184";
    private static String TAG = "phptest";
    Button typebtn, gradebtn, startbtn;
    String[] typeArray = new String[]{"지체", "뇌병변", "시각", "청각", "신장", "심장", "호흡기", "간", "장루/요루", "지적", "자폐성", "정신"};
    String[] gradeArray = new String[]{"1급", "2급", "3급", "4급", "5급", "6급"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typebtn = (Button) findViewById(R.id.type);
        typebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder typedlg = new AlertDialog.Builder(MainActivity.this);
                typedlg.setTitle("종류를 선택하시오.");
                typedlg.setItems(typeArray,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                typebtn.setText(typeArray[which]);
                            }
                        });
                typedlg.show();
            }
        });

        gradebtn = (Button) findViewById(R.id.grade);
        gradebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder gradedlg = new AlertDialog.Builder(MainActivity.this);
                gradedlg.setTitle("급수를 선택하시오.");
                gradedlg.setItems(gradeArray,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gradebtn.setText(gradeArray[which]);
                            }
                        });
                gradedlg.show();
                Log.i(this.getClass().getName(), "---------------------------------------------");
                Log.i(this.getClass().getName(), "---------------------------------------------");
                Log.i(this.getClass().getName(), "---------------------------------------------");
            }
        });

        startbtn = (Button) findViewById(R.id.start);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Nametext = findViewById(R.id.name);
                EditText CC = findViewById(R.id.displacement);

                String user_name = Nametext.getText().toString();
                String user_car = CC.getText().toString();
                String user_disability_type = typebtn.getText().toString();
                String user_disability_grade = gradebtn.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert_users.php", user_name, user_disability_type, user_disability_grade, user_car);

                Intent intent = new Intent(MainActivity.this, MOM02EX01.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                startActivity(intent);

            }
        });


    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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

            String user_name = (String) params[1];
            String user_disability_type = (String) params[2];
            String user_disability_grade = (String) params[3];
            String user_car = (String) params[4];


            String serverURL = (String) params[0];
            String postParameters = "user_name=" + user_name + "&user_disability_type=" + user_disability_type + "&user_disability_grade=" + user_disability_grade + "&user_car=" + user_car;


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


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
