package com.odt.kandani.Database;

import java.security.Policy;

public class Reminder {
    private int mID;
    private String mTitle;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mVoice;
    private String mPhoto;
    private String mActive;
    private String mType;


    public Reminder(int ID, String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Voice, String Photo, String Active, String Type){
        mID = ID;
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mVoice =  Voice;
        mPhoto = Photo;
        mActive = Active;
        mType = Type;
    }

    public Reminder(String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Voice, String Photo, String Active, String Type){
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mVoice =  Voice;
        mPhoto = Photo;
        mActive = Active;
        mType = Type;
    }

    public Reminder(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String repeatNo) {
        mRepeatNo = repeatNo;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String repeat) {
        mRepeat = repeat;
    }

    public String getVoice() { return mVoice; }

    public void setVoice(String voice) {
        mVoice = voice;
    }

    public String getPhoto() { return mPhoto; }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }

    public String getType() {
        return mType;
    }

    public void setType(String Type) {
        mType = Type;
    }
}
