package com.example.sing;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ListFragment extends Fragment {
    ListView list;
    ListViewAdapter adapter;
    DB_Open db_open;
    SQLiteDatabase db = null;
    private int division = 0;
    private int which = 0;
    public static ListFragment newInstance(){
        return new ListFragment();
    }


    @Override
    public void onStart( ) {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,  //채팅이 실행될 떼
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false); //채팅 레이아웃을 지금 화면에 뿌림
        list = v.findViewById(R.id.listing);
        createDB();
        showList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c;
                position=position+1;
                //Log.d("xxx", String.valueOf(position));
                DuetFragment fragment = new DuetFragment();
                c=db.rawQuery("SELECT * FROM sing4 WHERE id="+position,null);
                c.moveToFirst();
                String uri = c.getString(c.getColumnIndex("uri"));
                String lyrics=c.getString(c.getColumnIndex("lyrics"));
                String name=c.getString(c.getColumnIndex("name"));
                Bundle bundle = new Bundle();
                bundle.putString("lyrics",lyrics);
                bundle.putString("name",name);
                bundle.putString("uri",uri);
                bundle.putInt("which",which);
                fragment.setArguments(bundle);
                ((DuetActivity) Objects.requireNonNull(getActivity())).replaceFragment(fragment);
            }
        });
        return v;

    }
    protected  void createDB(){
        db_open=new DB_Open(getActivity());
        db=db_open.getWritableDatabase();
    }
    protected void showList(){
        adapter=new ListViewAdapter();
        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.a),
                "곰 세마리") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.b),
                "엄마돼지 아기돼지") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.c),
                "여름 냇가") ;
        // 네 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.d),
                "코끼리 아저씨") ;
        // 다섯번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.e),
                "작은 동물원") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.f),
                "개구리") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.g),
                "나무를 심자") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.h),
                "내가 먼저 가야해요") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.i),
                "악어 떼") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.j),
                "올챙이와 개구리") ;
        list.setAdapter(adapter);

    }


}