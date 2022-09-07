package com.example.filechooser;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FacultyDetailsObject implements Parcelable {

    String name,id;
    ArrayList<String> list;



    public FacultyDetailsObject() {
    }

    public FacultyDetailsObject(String name, String id, ArrayList<String> list) {
        this.name = name;
        this.id = id;
        this.list = list;
    }

    protected FacultyDetailsObject(Parcel in) {
        name = in.readString();
        id = in.readString();
        list = in.createStringArrayList();
    }

    public static final Creator<FacultyDetailsObject> CREATOR = new Creator<FacultyDetailsObject>() {
        @Override
        public FacultyDetailsObject createFromParcel(Parcel in) {
            return new FacultyDetailsObject(in);
        }

        @Override
        public FacultyDetailsObject[] newArray(int size) {
            return new FacultyDetailsObject[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeStringList(list);
    }
}
