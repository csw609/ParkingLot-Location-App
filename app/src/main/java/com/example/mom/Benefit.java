package com.example.mom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Benefit extends AppCompatActivity
{
    String user_name;
    String user_car;
    String user_disability_type;
    String user_disability_grade;
    Button bene1,bene2,bene3,bene4,bene5,bene6,bene7;

    private ArrayList<ParkingData> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user_name = intent.getStringExtra("name");
        user_disability_type = intent.getStringExtra("type");
        user_disability_grade = intent.getStringExtra("grade");
        user_car = intent.getStringExtra("car");
        mArrayList = (ArrayList<ParkingData>) intent.getSerializableExtra("array");

        setContentView(R.layout.benefitchang);

        Button mapBtn = (Button) findViewById(R.id.mapBtn2);
        Button myInfoBtn = (Button) findViewById(R.id.myInfoBtn2);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Benefit.this, MOM02EX01.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                intent.putExtra("task",false);
                startActivity(intent);

            }
        });
        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Benefit.this, MyInfo.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                startActivity(intent);

            }
        });

        bene1 = (Button) findViewById(R.id.bene1);
        bene2 = (Button) findViewById(R.id.bene2);
        bene3 = (Button) findViewById(R.id.bene3);
        bene4 = (Button) findViewById(R.id.bene4);
        bene5 = (Button) findViewById(R.id.bene5);
        bene6 = (Button) findViewById(R.id.bene6);
        bene7 = (Button) findViewById(R.id.bene7);

        final String webSite ="https://www.gov.kr/portal/service/serviceInfo/131200000004";
        final String webSite2 ="http://www.bokjiro.go.kr/welInfo/retrieveGvmtWelInfo.do?searchIntClId=01&welInfSno=48";
        final String webSite3 ="http://www.mohw.go.kr/react/policy/index.jsp?PAR_MENU_ID=06&MENU_ID=06370107&PAGE=7&topTitle=%EA%B3%B5%EA%B3%B5%EC%9A%94%EA%B8%88%20%EA%B0%90%EB%A9%B4";
        final String webSite4 ="http://www.mohw.go.kr/react/policy/index.jsp?PAR_MENU_ID=06&MENU_ID=06370107&PAGE=7&topTitle=%EA%B3%B5%EA%B3%B5%EC%9A%94%EA%B8%88%20%EA%B0%90%EB%A9%B4";
        final String webSite5 ="https://www.gov.kr/portal/service/serviceInfo/148000000005";
        final String webSite6 ="http://www.mohw.go.kr/react/policy/index.jsp?PAR_MENU_ID=06&MENU_ID=06370104&PAGE=4&topTitle=%EC%9D%98%EB%A3%8C%EC%A7%80%EC%9B%90";
        final String webSite7 ="http://www.mohw.go.kr/react/policy/index.jsp?PAR_MENU_ID=06&MENU_ID=06370108&PAGE=8&topTitle=";

        bene1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
                startActivity(intent);

            }
        });

        bene2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite2));
                startActivity(intent);

            }
        });
        bene3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite3));
                startActivity(intent);

            }
        });

        bene4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite4));
                startActivity(intent);

            }
        });

        bene5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite5));
                startActivity(intent);

            }
        });

        bene6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite6));
                startActivity(intent);

            }
        });

        bene7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite7));
                startActivity(intent);

            }
        });
    }
}
