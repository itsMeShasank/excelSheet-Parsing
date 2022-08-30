package com.example.filechooser;

public class saveFaculties {
    private String day;
    private String name;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    private String sectionId;
    private String room;
    private String shortVal;
    private String time;
    private int period;
    //private String facultyId;
    /*public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }*/

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getShortVal() {
        return shortVal;
    }

    public void setShortVal(String shortVal) {
        this.shortVal = shortVal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public saveFaculties(String day, String name, String sectionId, String room, String shortVal, String time, int period) {
        this.day = day;
        this.name = name;
        //this.facultyId = facultyId;
        this.sectionId = sectionId;
        this.room = room;
        this.shortVal = shortVal;
        this.time = time;
        this.period = period;
    }
}
