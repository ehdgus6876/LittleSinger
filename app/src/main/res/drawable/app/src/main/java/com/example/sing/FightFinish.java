package com.example.sing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class FightFinish extends Activity {
    private static final String TAG = "FightFinish";

    Context context = null;
    ProgressDialog progressDialog = null;
    private int isVictory=0; //0:승리, 1:패배, 2: 무승부
    TextView scoreTV,timeTV,isVictoryTV;
    Button mainBtn, againBtn;
    DataReceived dataReceived = null;
    static String getdata="";
    String getwordData[];
    int playerscore;
    static Boolean isFinishData=false;
    Boolean sender = false;
    int score;
    String time;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_finish);
        Log.e(TAG, "onCreate: ");

        context = this;

        scoreTV=(TextView)findViewById(R.id.score);
        timeTV=(TextView)findViewById(R.id.time);
        isVictoryTV =(TextView)findViewById(R.id.isVictory);
        mainBtn=(Button)findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent main = new Intent(getApplicationContext(),MainActivity.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
                startActivity(main);

            }
        });
        againBtn=(Button)findViewById(R.id.againBtn);
        againBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent again = new Intent(getApplicationContext(),DuetPairing.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
                startActivity(again);

            }
        });

        dataReceived = new DataReceived();

        Intent intent = getIntent();
        score=intent.getIntExtra("score",0);
        time =intent.getStringExtra("time");
        sender = intent.getBooleanExtra("sender", false);

        if(sender) {
            IntentFilter intentFilter = new IntentFilter("org.techtown.please.DataReceive");
            registerReceiver(dataReceived, intentFilter);

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Wait");
            progressDialog.setMessage("상대방의 결과를 수신중입니다...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            getdata = intent.getStringExtra("score2");
            result();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void result() {
        // Log.e("fight", getdata + "%%%finish22%%%%%" + time);
        playerscore=Integer.parseInt(getdata);
        if(score>playerscore) //승리
            isVictory=0;
        else if(score==playerscore) //무승부
            isVictory=1;
        else isVictory=2; //패배

        if(isVictory==0) { //승리
            isVictoryTV.setText("");
            isVictoryTV.setBackground(this.getResources().getDrawable( ( R.drawable.victory)));
            scoreTV.setText(score+" 점");
            if(time.equals("finish")){
                timeTV.setText("TIME OUT");
            }
            else {
                timeTV.setText(time + " 초");
            }
        }
        else if(isVictory==2) { //패배
            isVictoryTV.setText("");
            isVictoryTV.setBackground(this.getResources().getDrawable((R.drawable.defeat)));
            scoreTV.setText(score + " 점");
            if (time.equals("finish")) {
                timeTV.setText("TIME OUT");
            } else {
                timeTV.setText(time + " 초");
            }
        }
        else if(isVictory==1){ //무승부
            isVictoryTV.setText("");
            isVictoryTV.setBackground(this.getResources().getDrawable( ( R.drawable.draw)));
            scoreTV.setText(score+" 점");
            if(time.equals("finish")){
                timeTV.setText("TIME OUT");
            }
            else {
                timeTV.setText(time + " 초");
            }
        }

    }

    public class DataReceived extends BroadcastReceiver {
        public DataReceived(){

        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();

            if(actionName.equals("org.techtown.please.DataReceive")) {
                getdata =intent.getStringExtra("finishData");
                Log.e(TAG, "onReceive: " +getdata);
                if(getdata == null) {
                    return;
                }
                progressDialog.dismiss();
                unregisterReceiver(dataReceived);

                result();
            }
        }
    }
}
