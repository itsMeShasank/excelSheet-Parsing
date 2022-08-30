package com.example.filechooser;

public class Faculties {
    private String Id,Name,Subjects;

    public Faculties(String id, String name, String subjects) {
        this.Id = id;
        this.Name = name;
        this.Subjects = subjects;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSubjects() {
        return Subjects;
    }

    public void setSubjects(String subjects) {
        Subjects = subjects;
    }
}
