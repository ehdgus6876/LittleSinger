package com.example.sing;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class FightMain extends Activity {
    private static final String TAG = "FightMain";
    private static final int MILLISINFUTURE = 10000;
    private static final int COUNT_DOWN_INTERVAL = 1000; //1초에 한번씩 10번 실행
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    // var
    private int score=0;
    private int count = 10;
    private ArrayList<String> items = new ArrayList<>(); // 대전할 단어들

    // component
    private TextView tv,scoreView,timeView;
    private ListView listView;

    //others
    private CountDownTimer countDownTimer;
    private DataReceived dataReceived = null;
    private Intent i;
    private SpeechRecognizer mRecognizer;
    private FightMainListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_main);
        Log.e(TAG, "onCreate: " );
        //폰의 화면 크기가져오기
        Display dis = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        dataReceived = new DataReceived();

        IntentFilter intentFilter = new IntentFilter("org.techtown.please.DataReceive");
        registerReceiver(dataReceived, intentFilter);

        if (ContextCompat.checkSelfPermission(this, //오디오 접근권한
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        //i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); //한국어
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); //영어
        //i.putExtra(RecognizerIntent.EXTRA_PROMPT,"말해주세요.");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this); //인스턴스를 얻기위해
        mRecognizer.setRecognitionListener(recognitionListener); //콜백리스너 등록 (RecognitionListener 인터페이스를 구현하는 인스턴스를 하나 전달)
        // mRecognizer.startListening(i);

        tv=(TextView)findViewById(R.id.textGameResult);
        scoreView= (TextView)findViewById(R.id.score);
        timeView=(TextView)findViewById(R.id.time);
        listView = (ListView) findViewById(R.id.fight_main_listview);


        Intent intent = getIntent();
        items = intent.getStringArrayListExtra("words"); //받은 단어 리스트
        adapter = new FightMainListAdapter(dis);
        for(int i=0;i<items.size();i++){
            adapter.addItem(items.get(i));
        }
        listView.setAdapter(adapter);

        Log.e("단어 개수 ", items.size()+"데이터 받음(메인)");

        score=0;
        countDownTimer();
        countDownTimer.start();

    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) { //들리는 소리 크기가 변경되었을 때 호출
            // TODO Auto-generated method stub
        }

        @Override
        public void onResults(Bundle results) { //음성인식이 끝나고 결과가 나왔을 때 호출
            // / TODO Auto-generated method stub
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            //new mThreadTrans().start(); //뜻 가져오는 api요청 스레드 실행

            tv.setText(rs[0]); //첫 번재로 인식된 언어를 받아옴.(for문을 돌려 전체를 받아올 수도 있음)
            if(rs[0].equalsIgnoreCase(items.get(0))){
                adapter.deleteItem(0);
                score++;
                scoreView.setText("점수: "+score);
            }
            else if(rs[0].equalsIgnoreCase(items.get(1))){
                adapter.deleteItem(1);
                score++;
                scoreView.setText("점수: "+score);
            }
            else if(rs[0].equalsIgnoreCase(items.get(2))){
                adapter.deleteItem(2);
                score++;
                scoreView.setText("점수: "+score);
            }else if(rs[0].equalsIgnoreCase(items.get(3))){
                adapter.deleteItem(3);
                score++;
                scoreView.setText("점수: "+score);
            }else if(rs[0].equalsIgnoreCase(items.get(4))){
                adapter.deleteItem(4);
                score++;
                scoreView.setText("점수: "+score);
            }else if(rs[0].equalsIgnoreCase(items.get(5))){
                adapter.deleteItem(5);
                score++;
                scoreView.setText("점수: "+score);
            }else if(rs[0].equalsIgnoreCase(items.get(6))){
                adapter.deleteItem(6);
                score++;
                scoreView.setText("점수: "+score);
            }else if(rs[0].equalsIgnoreCase(items.get(7))){
                adapter.deleteItem(7);
                score++;
                scoreView.setText("점수: "+score);
            }

            if(score==8){
                //데이터 주고 받는 부분이 있어야함
                unregisterReceiver(dataReceived);
                Intent finish = new Intent(getApplicationContext(),FightFinish.class);  //끝난 화면으로 넘어감
                finish.putExtra("score",score);
                finish.putExtra("time",timeView.getText().toString());
                finish.putExtra("sender", true);
                //Log.e("fight", score+"%%%11%%%%%%"+timeView.getText().toString());
                int resultScore =score;
                ConnectManagementService.sendWord(resultScore+"@");
                startActivity(finish);
                onDestroy();

            }
            //  mRecognizer.startListening(i);
        }

        @Override
        public void onReadyForSpeech(Bundle params) { //. 말을 들을 준비가 되었다는 콜백
            // TODO Auto-generated method stub
            // tv.setText("영어단어를 말하세요.");
        }

        @Override
        public void onPartialResults(Bundle partialResults) { //부분적인 결과를 잡아냈을 때 호출
            // TODO Auto-generated method stub
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(int error) { //에러가 발생했을 때
            // TODO Auto-generated method stub
            tv.setText(Integer.toString(error)); //6: 아무음성도 듣지 못했을 때, 7: 적당한 결과를 찾지 못했을 때
        }

        @Override
        public void onEndOfSpeech() { //음성이 끝났을 때 호출 (성공시 -> onResults, 실패시 -> onError)
            // TODO Auto-generated method stub
        }

        @Override
        public void onBufferReceived(byte[] buffer) { //새로운 소리가 들어왔을 때 호출
            // TODO Auto-generated method stub
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
            tv.setText("음성인식 중");
        }

    };
    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                timeView.setText("Time: "+String.valueOf(count)+"sec");
                count --;
                onClear();
            }
            public void onFinish() {
                timeView.setText(String.valueOf("Finish"));

                //데이터 주고 받는 부분이 있어야함
                unregisterReceiver(dataReceived);
                Intent finish = new Intent(getApplicationContext(),FightFinish.class);  //끝난 화면으로 넘어감
                finish.putExtra("score",score);
                finish.putExtra("time",timeView.getText().toString());
                finish.putExtra("sender", true);
                //Log.e("fight", score+"%%%11%%%%%%"+timeView.getText().toString());
                String resultScore = String.valueOf(score);
                ConnectManagementService.sendWord(resultScore);
                startActivity(finish);
                onDestroy();

            }

            public void onClear(){
                if(score==8){
                    //데이터 주고 받는 부분이 있어야함
                    unregisterReceiver(dataReceived);
                    Intent finish = new Intent(getApplicationContext(),FightFinish.class);  //끝난 화면으로 넘어감
                    finish.putExtra("score",score);
                    finish.putExtra("time",timeView.getText().toString());
                    finish.putExtra("sender", true);
                    //Log.e("fight", score+"%%%11%%%%%%"+timeView.getText().toString());
                    String resultScore = String.valueOf(score);
                    ConnectManagementService.sendWord(resultScore);
                    startActivity(finish);
                    onDestroy();

                }
            }
        };
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            countDownTimer.cancel();
        } catch (Exception e) {}
        countDownTimer=null;
    }

    public class DataReceived extends BroadcastReceiver {
        public DataReceived(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();

            if(actionName.equals("org.techtown.please.DataReceive")) {
                //데이터 주고 받는 부분이 있어야함
                unregisterReceiver(dataReceived);
                Intent finish = new Intent(getApplicationContext(),FightFinish.class);  //끝난 화면으로 넘어감
                finish.putExtra("score",score);
                finish.putExtra("time",timeView.getText().toString());
                finish.putExtra("sender", false);
                finish.putExtra("score2", intent.getStringExtra("finishData"));
                //Log.e("fight", score+"%%%11%%%%%%"+timeView.getText().toString());
                String resultScore = String.valueOf(score);
                ConnectManagementService.sendWord(resultScore);
                startActivity(finish);
                onDestroy();
            }
        }
    }
}
