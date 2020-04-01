package com.example.sing;

import android.graphics.drawable.Drawable;

public class ScoreListviewItem {
    private String titleStr ;
    private String scorea;
    private String datea;



    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setScore(String score) {scorea=score;}
    public void setDatea(String date) {datea=date;}
    public String getTitle() {
        return this.titleStr ;
    }
    public String getScore() {return this.scorea ;}
    public String getDatea() {return this.datea ;}
}
