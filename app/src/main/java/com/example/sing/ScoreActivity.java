package com.example.sing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends AppCompatActivity {

    private int totalscore=0;
    private int dbs=0;
    private int record=0;
    private String lyrics_org;
    private String recText_org;
    private String lyrics;
    private String recText;
    TextView score;
    TextView rec;
    TextView dbsview;
    TextView lyrics1;
    TextView lyrics2;
    String date;
    DB_Open db_open;
    SQLiteDatabase db=null;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Intent intent = getIntent();
        score = findViewById(R.id.score);
        rec = findViewById((R.id.rec));
        dbsview = findViewById(R.id.db);
        lyrics1 = findViewById(R.id.right_lyrics);
        lyrics2 = findViewById(R.id.wrong_lyrics);
        createDB();
        date=intent.getExtras().getString("date");
        dbs = intent.getExtras().getInt("dbs");
        record = intent.getExtras().getInt("finalrec");
        name=intent.getExtras().getString("name");
        totalscore = (dbs / 2) + (record / 2);
        lyrics_org = intent.getExtras().getString("right_lyrics");
        recText_org = intent.getExtras().getString("wrong_lyrics");
        lyrics = lyrics_org.replace(" ","");
        recText = recText_org.replace(" ","");

        score.setText(String.valueOf(totalscore));
        rec.setText(String.valueOf(record / 2));
        dbsview.setText(String.valueOf(dbs / 2));
        db.execSQL("INSERT INTO score1 (name,score,date) VALUES('"+name+"','"+totalscore+"','"+date+"')");

        score.setText(String.valueOf(totalscore));
        rec.setText(String.valueOf(record / 2));
        dbsview.setText(String.valueOf(dbs / 2));

        SpannableStringBuilder wrong_lyrics ;
        SpannableStringBuilder right_lyrics ;


        if(recText_org==null){
            Toast.makeText(getApplicationContext(),"노래를 부르지않았어요!",Toast.LENGTH_SHORT).show();
            Intent back=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(back);
        }
        else {
            wrong_lyrics = new SpannableStringBuilder(recText_org);
            right_lyrics = new SpannableStringBuilder(lyrics_org) ;

            for(int i=0,j=0; i<recText_org.length();i++,j++) {
                if (recText_org.charAt(i) == ' ') {
                    j--;}
                if ((recText.charAt(j)) != (lyrics.charAt(j))) {
                    wrong_lyrics.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), (i), (i+ 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }
            for(int i=0,j=0; i<recText_org.length()&&j<recText.length();i++,j++) {
                if (lyrics_org.charAt(i) == ' ') {
                    j--;}
                if ((recText.charAt(j)) != (lyrics.charAt(j))) {
                    right_lyrics.setSpan(new ForegroundColorSpan(Color.parseColor("#1DDB16")), (i), (i+ 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }

            lyrics2.setText(wrong_lyrics);
            lyrics1.setText(right_lyrics);
        }




        Button score_btn=findViewById(R.id.btn_score);
        score_btn.setOnClickListener(v->{
            Intent intent1=new Intent(getApplicationContext(),ScoreMonitorActivity.class);
            startActivity(intent1);
        });
        Button main_btn=findViewById(R.id.btn_main);
        main_btn.setOnClickListener(v->{
            Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent2);
        });

    }
    protected  void createDB(){
        db_open=new DB_Open(this);
        db=db_open.getWritableDatabase();
    }
}
