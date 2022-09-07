package com.example.filechooser;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class globalApplication extends Application {

    public static HashMap<String, ArrayList<String>> namesmap = new HashMap<>();

}
