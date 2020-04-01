package com.example.sing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class DuetWait extends Activity {
    private static final String TAG = "DuetWait";

    Context context = null;
    ProgressDialog progressDialog = null;
    DataReceived dataReceived = null;

    static boolean isSelected=false; //상대방이 듀엣모드 동의하는 지 확인
    static String word = "";

    public void onCreate(Bundle savedINstanceState) {
        super.onCreate(savedINstanceState);
        setContentView(R.layout.fight_wait);
        Log.e(TAG, "onCreate: " );
        context = this;
        dataReceived = new DataReceived();
        Intent intent1 = getIntent();

        if(isSelected){
            Log.e(TAG, "isSelected 두번째"+ isSelected );
            Intent duetstart = new Intent(getApplicationContext(),FightMain.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
            duetstart.putExtra("word", word);
            duetstart.putStringArrayListExtra("words",intent1.getStringArrayListExtra("words"));
            startActivity(duetstart);
        }
        else {
            isSelected = true;
            Log.e(TAG, "isSelected 첫번째"+ isSelected );
            IntentFilter intentFilter = new IntentFilter("org.techtown.please.DataReceive");
            registerReceiver(dataReceived, intentFilter);

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Wait");
            progressDialog.setMessage("상대방이 아직 듀엣모드를 동의하지 않았습니다...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
    public class DataReceived extends BroadcastReceiver {
        public DataReceived(){
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();
            String check = intent.getStringExtra("data");
            Log.e(TAG, "onReceive: "+actionName );
            //connectmanagementservice 에서 sendBroadcast 한거 수신함
            if(actionName.equals("org.techtown.please.DataReceive")) {
                unregisterReceiver(dataReceived);
                Log.e(TAG, "(isSelected 첫번째) if문안 정상****** "+ check );
                progressDialog.dismiss();

                Intent duetstart = new Intent(getApplicationContext(),FightMain.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
                Log.e(TAG, "duetwait intent 정상적@@@@@@@@@");
                duetstart.putExtra("words", intent.getStringArrayListExtra("words"));
                startActivity(duetstart);
                Log.e(TAG, "duetwait startActivity 정상적@@@@@@@@@");
            }
        }
    }
}
