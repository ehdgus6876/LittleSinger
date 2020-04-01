package com.example.sing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    final CharSequence[] oItems = {"한글", "English"};
    private int division=0; //연습모드,촬영모드,듀엣모드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);



        ImageButton btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                division=1;
                showDialog();
            }
        });
        ImageButton btn2=findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                division=2;
                showDialog();
            }
        });
        ImageButton btn3=findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DuetActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btn4=findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ScoreMonitorActivity.class);
                startActivity(intent);
            }
        });

    }
    public void showDialog(){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        oDialog.setTitle("노래 버전을 선택하시오")
                .setItems(oItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0 && division==1){
                            Toast.makeText(getApplicationContext(),"한글을 선택하셨어요!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),ListActivity.class);
                            intent.putExtra("which",which);
                            intent.putExtra("division",division);
                            startActivity(intent);
                        }
                        else if(which==0 && division ==2){
                            Toast.makeText(getApplicationContext(),"한글을 선택하셨어요!",Toast.LENGTH_LONG).show();
                            Intent intent2 = new Intent(getApplicationContext(),ListActivity.class);
                            intent2.putExtra("which",which);
                            intent2.putExtra("division",division);
                            startActivity(intent2);
                        }
                        else if(which==1&&division==1){
                            Toast.makeText(getApplicationContext(),"영어를 선택하셨어요!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),ListActivity_eng.class);
                            intent.putExtra("which",which);
                            intent.putExtra("division",division);
                            startActivity(intent);
                        }
                        else if(which==1&&division==2){
                            Toast.makeText(getApplicationContext(),"영어를 선택하셨어요!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),ListActivity_eng.class);
                            intent.putExtra("which",which);
                            intent.putExtra("division",division);
                            startActivity(intent);
                        }

                    }
                })
                .setCancelable(false)
                .show();
    }
}
