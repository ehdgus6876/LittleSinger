package com.example.sing;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FightMainListAdapter extends BaseAdapter {
    private static final String TAG = "FightMainListAdapter";
    private TextView word_tv;
    private Display dis;
    private ArrayList<FightMainListItem> items = new ArrayList<>();

    public FightMainListAdapter(Display dis){
        this.dis = dis;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public String getWord(int position){
        return items.get(position).getWord();
    }
    public void addItem(String word){
        FightMainListItem item = new FightMainListItem(word);
        items.add(item);
    }
    public void deleteItem(int position){
        items.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_fightmain, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        word_tv = (TextView) convertView.findViewById(R.id.word_tv) ;

        FightMainListItem listViewItem = items.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        word_tv.setText(listViewItem.getWord());
        if(items.size()==8) {
            printWord(this.dis, items);
        }
        return convertView;
    }

    public void printWord(Display dis,ArrayList<FightMainListItem> items){ //랜덤으로 단어를 화면에 뿌리는 함수

        Log.e(TAG, "printWord: " );

        int width = (int)(dis.getWidth());

        for(int i=0;i<items.size();i++) {
            int x = (int) (Math.random() * width) + 1;

            //textview가 써지기전에 글자의 높이와 너비 가져옴
            word_tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            int text_width = word_tv.getMeasuredWidth();
            int text_height = word_tv.getMeasuredHeight();
            //영어단어가 화면밖으로 나가지 않게
            while (true) {

                if ((x>0)&&(x + text_width < width)) {
                    break;
                } else {
                    x = (int) (Math.random() * width) + 1;


                }
            }

            word_tv.setX(x);

        }
    }
}
