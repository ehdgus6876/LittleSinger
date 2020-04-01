package com.example.sing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity_eng extends AppCompatActivity {

    ListView list;
    ListViewAdapter adapter;
    DB_Open db_open;
    SQLiteDatabase db=null;
    private int division=0;
    private int which=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list = findViewById(R.id.listing);
        createDB();
        showList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c;
                position=position+1;
                //Log.d("xxx", String.valueOf(position));
                Intent div=getIntent();

                division=div.getExtras().getInt("division");
                which=div.getExtras().getInt("which");
                if(division==1){
                    Intent intent = new Intent(getApplicationContext(),PracticeActivity.class);
                    c=db.rawQuery("SELECT * FROM sing5eng WHERE id="+position,null);
                    c.moveToFirst();
                    String uri = c.getString(c.getColumnIndex("uri"));
                    String lyrics=c.getString(c.getColumnIndex("lyrics"));
                    intent.putExtra("lyrics",lyrics);
                    intent.putExtra("uri",uri);
                    intent.putExtra("which",which);
                    Log.d("uri",uri);
                    Log.d("uri",lyrics);
                    startActivity(intent);
                }
                else if(division==2){
                    Intent intent2=new Intent(getApplicationContext(),RecordingActivity2.class);
                    c=db.rawQuery("SELECT * FROM sing5eng WHERE id="+position,null);
                    c.moveToFirst();
                    String uri = c.getString(c.getColumnIndex("uri"));
                    intent2.putExtra("uri",uri);
                    startActivity(intent2);
                }
            }
        });
    }
    protected  void createDB(){
        db_open=new DB_Open(this);
        db=db_open.getWritableDatabase();
    }
    protected void showList(){
        adapter=new ListViewAdapter();
        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.a1),
                "Finger Family") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.b1),
                "The Three Bear") ;
        // 세 번째 아이템 추가.`
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.c1),
                "Head and Shoulders Knees and Toes") ;
        // 네 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.d1),
                "How Are You My Friend") ;
        // 다섯번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.e1),
                "If You are Happy");
        // 여섯번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.f1),
                "Jingle Bell") ;
        // 일곱번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.g1),
                "LA CUCARACHA") ;
        // 여덟번째 아이템 추가.`
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.h1),
                "Rain, Rain, Go Away") ;
        // 아홉번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.i1),
                "Santa Claus is Coming to Town") ;
        // 열번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.j1),
                "Twinkle, Twinkle, Little Star");
        list.setAdapter(adapter);
    }


}
