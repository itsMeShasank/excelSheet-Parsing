package com.example.filechooser;

import java.util.HashMap;

public class facultydetails {
    private HashMap<String,String> map;

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public facultydetails(HashMap<String, String> map) {
        this.map = map;
    }
}
