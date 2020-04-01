package com.example.sing;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class PracticeActivity extends AppCompatActivity {
    Intent intent, recvIntent;
    SpeechRecognizer mRecognizer;
    TextView textview;
    final int PERMISSION = 1;
    private int scorecnt = 0;
    private int dbs = 0;
    private int totalScore = 0;
    private int scorerec = 0;
    int recintent = 0;
    private int which = 0;
    private int num ;
    private int num2;
    String out_lyrics1 = "";
    String out_lyrics2;
    String recText;
    String lyrics;
    String out ;

    DB_Open db_open;
    SQLiteDatabase db = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        textview = findViewById(R.id.lyrics);
        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        recvIntent = getIntent();
        which = recvIntent.getExtras().getInt("which");
        String name = recvIntent.getExtras().getString("name");

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // intent로 음성인식 패키지 호출 후 언어설정
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        if (which == 0) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");
        }
        if (which == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
        }


        createDB();
        final VideoView videoView = (VideoView) findViewById(R.id.video);

        try {
            Intent intent = getIntent();
            String video = intent.getExtras().getString("uri");

            Uri videofile = Uri.parse(video);
            videoView.setVideoURI(videofile);

        } catch (Exception ex) {
            Log.d(getClass().getName(), "Video failed:" + ex + "");
            ex.printStackTrace();
        }
        ImageButton playbtn = findViewById(R.id.playbtn);
        playbtn.setOnClickListener(v -> {
            videoView.start();
            num = 0;
            num2 = 0;
            out = "";

        });
        ImageButton stopbtn = findViewById(R.id.stopbtn);
        stopbtn.setOnClickListener(v -> {
            videoView.stopPlayback();
            videoView.resume();
            onBackPressed();
        });
        ImageButton pausebtn = findViewById(R.id.pausebtn);
        pausebtn.setOnClickListener(v -> videoView.pause());
        ImageButton listbtn = findViewById(R.id.listbtn);
        listbtn.setOnClickListener(v -> onBackPressed());
        videoView.setOnCompletionListener(mp -> {
            recintent = finalrecScore();
            dbs = dbScore(scorecnt);
            Date currentTime = Calendar.getInstance().getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime);
            Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("dbs", dbs);
            intent.putExtra("finalrec", recintent);
            intent.putExtra("date", date_text);
            intent.putExtra("wrong_lyrics", out_lyrics1);
            intent.putExtra("right_lyrics", out_lyrics2);

            startActivity(intent);
        });
        ImageButton speakbtn = findViewById(R.id.speakbtn);
        speakbtn.setOnClickListener(v -> {
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });
    }

    private final RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            ImageButton speechBtn = findViewById(R.id.speakbtn);
            speechBtn.setBackgroundColor(Color.WHITE);
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            ImageButton speechBtn = findViewById(R.id.speakbtn);

            speechBtn.setImageResource(R.drawable.record2);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            if ((int) rmsdB == 10) {
                scorecnt++;
                Log.d("xx", String.valueOf(scorecnt));
            }
        }


        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            ImageButton speechBtn = findViewById(R.id.speakbtn);

            speechBtn.setImageResource(R.drawable.record);
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String recText_org = matches.get(0); //인식된 음성정보
            recText = recText_org.replace(" ", ""); //인식된 음성정보 공백제거

            Intent intent = getIntent(); //데이터베이스에서 가사 인텐트 객체 받아옴
            String lyrics_org = intent.getExtras().getString("lyrics"); //가사 정보
            lyrics = lyrics_org.replace(" ", ""); //가사정보 공백제거


            for (int i = 0; i < recText.length(); i++) {
                if ((recText.charAt(i)) == (lyrics.charAt(i))) {  //음성정보와 가사와 비교해서 가사 정확도 점수측정
                    scorerec++;
                }
            }
            if(which==0) {
                int i, j;
                SpannableStringBuilder wrong_lyrics;
                SpannableStringBuilder right_lyrics;
                wrong_lyrics = new SpannableStringBuilder(recText_org);
                for (i = 0, j = 0; i < recText_org.length(); i++, j++, num++) {
                    if (recText_org.charAt(i) == ' ') {
                        j--;
                        num--;
                    }
                    if ((recText.charAt(j)) != (lyrics.charAt(num))) {
                        wrong_lyrics.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), (i), (i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }

                }
                textview.append(wrong_lyrics);
                textview.append(" ");

                out = out+wrong_lyrics+" ";

                out_lyrics1= out;
                out_lyrics2 = lyrics_org;
            }
            else if (which == 1){
                SpannableStringBuilder wrong_lyrics ;
                SpannableStringBuilder right_lyrics ;
                wrong_lyrics = new SpannableStringBuilder(recText_org);
                right_lyrics = new SpannableStringBuilder( lyrics_org) ;
                for(int i=0; i<recText_org.length();i++,num2++) {
                    if (( recText_org.charAt(i)) != (lyrics_org.charAt(num2))) {
                        wrong_lyrics.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), i, (i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }

                }

                textview.append(wrong_lyrics);
                textview.append(" ");

                String out = String.valueOf(wrong_lyrics);

                out_lyrics1.concat(out);
                out_lyrics2 = lyrics_org;

            }



        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }


        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };
    protected  void createDB(){
        db_open=new DB_Open(this);
        db=db_open.getWritableDatabase();
    }
    public int dbScore(int scorecnt){
        if(scorecnt>=0&&scorecnt<=30){
            dbs=24;
        }
        else if(scorecnt>30&&scorecnt<=60){
            dbs=47;
        }
        else if(scorecnt>60&&scorecnt<=90){
            dbs=50;
        }
        else if(scorecnt>90&&scorecnt<=125){
            dbs=75;
        }
        else dbs=100;
        Log.d("dbs", String.valueOf(dbs));
        return dbs;
    }
    public int finalrecScore(){
        int finalrec;
        Intent intent = getIntent();
        String reclyrics = intent.getExtras().getString("lyrics");
        reclyrics = reclyrics.replace(" ","");
        //  scorerec = (scorerec / (reclyrics.length()))*100;
        if(scorerec>99)
            finalrec = 100;
        else if (scorerec <100 && scorerec>89)
            finalrec = 90;
        else if (scorerec <90 && scorerec >79)
            finalrec =80 ;
        else if (scorerec <80 && scorerec>69)
            finalrec = 70;
        else
            finalrec = 60;
        Log.d("finalrec", String.valueOf(finalrec));
        return finalrec;
    }

}

