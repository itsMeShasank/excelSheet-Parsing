package com.example.filechooser;

public class students {

    private int per;
    private String sec,day,faculty,sub,room,time;
    public students () {

    }
    public students(int per,String faculty,String sec,String day,String sub,String room,String time) {

        this.per = per;
        this.faculty = faculty;
        this.day = day;
        this.room = room;
        this.sec = sec;
        this.sub = sub;
        this.time = time;
    }

    public int getPer() {
        return per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
