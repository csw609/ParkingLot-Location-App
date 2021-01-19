package com.example.mom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyInfo extends AppCompatActivity
{
    String user_name;
    String user_car;
    String user_disability_type;
    String user_disability_grade;
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
        setContentView(R.layout.myinfochang);

        TextView infoName = findViewById(R.id.infoName);
        TextView infoType = findViewById(R.id.infoType);
        TextView infoGrade = findViewById(R.id.infoGrade);
        TextView infoCar = findViewById(R.id.infoCar);

        infoName.setText(user_name);
        infoType.setText(user_disability_type);
        infoGrade.setText(user_disability_grade);
        infoCar.setText(user_car + "CC");

        Button mapBtn = (Button) findViewById(R.id.mapBtn3);
        Button beneBtn = (Button) findViewById(R.id.beneBtn3);
        Button developerBtn = (Button) findViewById(R.id.developerBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfo.this, MOM02EX01.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                intent.putExtra("task",false);
                startActivity(intent);

            }
        });
        beneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfo.this, Benefit.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                startActivity(intent);

            }
        });
        developerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder feedlg = new AlertDialog.Builder(MyInfo.this);
                feedlg.setTitle("개발자 정보");
                feedlg.setMessage("광운대학교 로봇학부 \n 김다현, 이연주, 최석원");
                feedlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                feedlg.show();
            }
            });

    }
}
