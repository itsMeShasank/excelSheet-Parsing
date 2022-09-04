
        package com.example.filechooser;

        import static android.os.Environment.getExternalStoragePublicDirectory;
        import static android.os.Environment.getExternalStorageState;
        import static android.os.Environment.getStorageDirectory;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.Settings;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.lang.reflect.Field;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.List;
        import java.util.ListIterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;
        import java.util.StringTokenizer;

        import jxl.Sheet;
        import jxl.Workbook;
        import jxl.read.biff.BiffException;


public class MainActivity extends AppCompatActivity {


    Button button,save,cancel;
    public String year = "III - CSE";
    String extension="";
    HashMap<String,ArrayList> subjects = new HashMap<>();
    String[] timings = {"8:05-9:00","9:00-9:55","10:15-11:10","11:10-12:05","12:05-01:00","02:00-02:55","02:55-03:50","03:50-4:40","04:40-05:30"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissionAndBrowseFile();

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //askPermissionAndBrowseFile();
            }
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button = findViewById(R.id.browse);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] mimetypes =
                        { "application/vnd.ms-excel",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        };
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, 100);

            }});
    }

    //@RequiresApi(api = Build.VERSION_CODES.R)
    private void askPermissionAndBrowseFile()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){

                // If you don't have access, launch a new activity to show the user the system's dialog
                // to allow access to the external storage
            }else{
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri content_describer = data.getData();
        String type="";

        InputStream in = null;
        OutputStream out = null;
        try {
            // open the user-picked file for reading:
            try {
                in = getContentResolver().openInputStream(content_describer);
                type=getContentResolver().getType(content_describer);
                Log.d("path1",type);
                if(type.equalsIgnoreCase("application/vnd.ms-excel")){
                    extension=".xls";
                }
                else if(type.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                {
                    extension=".xlsx";
                }
                else{
                    extension=".xls";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // open the output-file:
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TimeTable");
                if (!file.exists()) {
                    file.mkdirs();
                }
                out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString()+"/TimeTable/data"+extension));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // copy the content:
            byte[] buffer = new byte[1024];
            int len=0;
            while (true) {
                try {
                    if (!((len = in.read(buffer)) != -1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.write(buffer, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Contents are copied!
            try {
                getdata();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Map<Integer, List<String>> readJExcel(String fileLocation,String sheetname)
            throws IOException, BiffException {

        Map<Integer, List<String>> data = new HashMap<>();

        Workbook workbook = Workbook.getWorkbook(new File(fileLocation));
        Sheet sheet = workbook.getSheet(sheetname);
        int rows = sheet.getRows();
        int columns = sheet.getColumns();

        for (int i = 0; i < rows; i++) {
            data.put(i, new ArrayList<String>());
            for (int j = 0; j < columns; j++) {
                data.get(i)
                        .add(sheet.getCell(j, i)
                                .getContents());
            }
        }
        return data;
    }

    public  void getdata() throws BiffException, IOException {
        Map<Integer,List<String>>sheet=readJExcel(Environment.getExternalStorageDirectory().toString()+"/TimeTable/data"+extension, year);
        Set<String> days=new HashSet<String>();
        for(String x:new String[]{"Mon","Tue","Wed","Thu","Fri","Sat"})
            days.add(x);
        String curSec="None";
        String year="III";
        String Rooms ="None";
        List<String>roomsList = new ArrayList<>();
        List<String>entireList = new ArrayList<>();

        List<List>entireroomsList = new ArrayList<>();
        for(Integer x :sheet.keySet())
        {
            List<String>curRow=sheet.get(x);
            if(days.contains(curRow.get(0)))
            {
                curRow.remove(3);
                curRow.remove(6);
                String prev="None";
                ListIterator<String> it=curRow.listIterator();
                while(it.hasNext())
                {
                    String curCol=it.next();

                    if(curCol.equals(""))
                        curCol=prev;
                    else
                        prev=curCol;
                    curCol=curCol.replace("\n", " ");
                    it.set(curCol);

                }
                int i;
                for(i=0;i<curRow.size();i++) {
                    String value = curRow.get(i);
                    if(value.contains("(P)") || value.contains("(T)")) {
                        if(value.contains("Hall")) {
                            value = value.substring(0,value.indexOf(")")+1);
                            String roomdata = value.substring(value.indexOf(")")+1,value.length());
                            roomsList.add(roomdata);
                        }
                        if(value.contains("VPTF") || value.contains("VPSF")) {
                            //DBMS (P/T) (VPTF-07)
                            StringTokenizer st = new StringTokenizer(value," ");
                            st.nextToken();
                            st.nextToken();
                            String roomVal = st.nextToken();
                            String roomsdata = roomVal.substring(1, roomVal.length()-1);

                            value = value.substring(0,value.indexOf(")")+1);
                            roomsList.add(roomsdata);
                        }
                        else {
                            value = value.substring(0, value.length());
                            roomsList.add(Rooms);
                        }

                        curRow.set(i, value);

                    }
                    else if(value.contains("SCIRP")) {
                        if(value.contains("VPTF") || value.contains("VPSF") || value.contains("Hall") ||((value.contains("Library"))&&(value.contains("Lab")))) {
                            curRow.set(i, value);
                            //System.out.println(value);
                            String roomdata = null;
                            roomdata = value.substring(value.indexOf("(")+1, value.indexOf(")"));
                            roomsList.add(roomdata);
                            //value = value.substring(0, value.indexOf("("));

                        }
                    }
                    else if(value.contains("VPTF") || value.contains("VPSF") || value.contains("Hall")) {
                        String roomdata = null;
                        roomdata = value.substring(value.indexOf("(")+1, value.indexOf(")"));
                        roomsList.add(roomdata);
                        value = value.substring(0, value.indexOf("("));
                        curRow.set(i, value);
                    }else if(value.contains("Open") || value.contains("Test") || value.contains("IDP")){
                        roomsList.add("refer section ");
                    }else if(value.contains("Library") && value.contains("Lab")) {
                        String roomdata=null;
                        roomdata = value.substring(value.indexOf("(")+1, value.indexOf(")"));
                        roomsList.add(roomdata);
                        value = value.substring(0,value.indexOf("(")-1);
                        curRow.set(i, value);
                    }else if(value.equals("Library")) {
                        String roomdata = value;
                        roomsList.add(roomdata);
                    }else if(value.contains("NTR")) {
                        String roomdata = value.substring(value.indexOf("N"), value.length());
                        roomsList.add(roomdata);
                        value = value.substring(0,value.indexOf("N"));
                        curRow.set(i,value);
                    }else if(value.contains("(CC Lab)")) {
                        roomsList.add(value.substring(value.indexOf("("),value.length()));
                        value = value.substring(0,value.indexOf("("));
                        curRow.set(i,value);
                    }
                    else {
                        roomsList.add(Rooms);
                    }
                }
                curRow.add(0, curSec);
                for(int k=0;k<curRow.size();k++) {
                    entireList.add(curRow.get(k));
                }

                entireroomsList.add(roomsList);
                continue;
            }

            if(!days.contains(curRow.get(0)) && !curRow.contains("Day") && !curRow.get(0).contains("Section") && curRow.get(0)!="") {
                ListIterator<String> itr1 = curRow.listIterator();
                HashMap<String,ArrayList> map = new HashMap<>();
                while(itr1.hasNext()) {
                    String data = itr1.next();
                    if(data.length()!=0) {
                        StringTokenizer st = new StringTokenizer(data,":");
                        while(st.hasMoreTokens()) {
                            String key,value;
                            key = curSec+","+st.nextToken();
                            value = st.nextToken();
                            StringTokenizer st1 = new StringTokenizer(value,",");
                            ArrayList<String> list = new ArrayList<>();
                            while(st1.hasMoreTokens()) {
                                String facultyName = st1.nextToken().trim();
                                facultyName = facultyName.replaceAll("[-+.^:, ]","").toLowerCase(Locale.ROOT);
                                list.add(facultyName);
                            }
                            map.put(key,list);


                            /*for(String name:list){
                                if(namesmap.containsKey(name))
                                    namesmap.get(name).add(key);
                                else{
                                    ArrayList<String> ll = new ArrayList<>();
                                    ll.add(key);
                                    namesmap.put(name,ll);
                                }
                            }*/
                        }
                    }
                }
                for (String key: map.keySet()){
                    subjects.put(key, map.get(key));
                }
            }
            if (curRow.get(0).contains("Section")) {
                String nameSec = curRow.get(0);
                Rooms = curRow.get(0);

                if (!nameSec.contains("VPTF") || !nameSec.contains("VPSF") &&(!curRow.get(0).contains("AI&ML") || !curRow.get(0).contains("CS") || !curRow.get(0).contains("CSBS"))) {
                    StringTokenizer st = new StringTokenizer(nameSec, " ");
                    st.nextToken();
                    st.nextToken();
                    curSec = year + " - " + st.nextToken();
                    Rooms = nameSec.substring(nameSec.indexOf("(") + 1, nameSec.length() - 1);
                } else {
                    StringTokenizer st = new StringTokenizer(nameSec, " ");
                    while (st.hasMoreTokens()) {
                        String in = st.nextToken();

                        if (!in.contains("Section") && !in.contains(":") && !(in.contains("VPTF") || in.contains("VPSF")) && !nameSec.contains("Hall") && !nameSec.contains("NTR")) {
                            curSec = year + " - " + in;
                        }
                        if (in.contains("VPTF") || in.contains("VPSF")) {
                            in = in.substring(1, in.length() - 1);
                            Rooms = in;
                        }else {
                            Rooms = "None";
                        }
                    }
                }
            }
            if(curRow.get(0).contains("AI&ML") || curRow.get(0).contains("CS") || curRow.get(0).contains("CSBS"))
            {
                //II AI&ML Section: A
                //II AI&ML Section: A (VPTF -07)

                String nameSec = curRow.get(0);
                if(nameSec.contains("VPTF") || nameSec.contains("VPSF")) {
                    StringTokenizer st = new StringTokenizer(nameSec, " ");
                    String yy = st.nextToken();
                    String spl = st.nextToken();
                    st.nextToken();
                    st.nextToken();
                    String sec = st.nextToken();
                    curSec = yy+" - "+spl+" - "+sec;
                    String roomVal = st.nextToken();
                    Rooms = roomVal.substring(1, roomVal.indexOf(")"));


                }else {
                    StringTokenizer st = new StringTokenizer(nameSec, " ");
                    String yy = st.nextToken();
                    String spl = st.nextToken();
                    st.nextToken();
                    st.nextToken();
                    String sec = st.nextToken();
                    curSec = yy+" - "+spl+" - "+sec;
                    Rooms = "None";
                }

            }
        }
        //System.out.println(entireList+"\n"+entireroomsList+"\n"+subjects);


        faculty(entireList, entireroomsList.get(0), subjects);
        //student(entireList,entireroomsList.get(0),subjects);

    }

    public void faculty(List<String> entireList, List<String> entireroomsList, HashMap<String, ArrayList> subjects) {

        int columncount = 0;
        for (String key : entireList) {
            if (key.contains("II") || key.contains("IV") || key.contains("III")) {
                columncount = 0;
            } else {
                //student(entireList,entireroomsList,subjects);
                columncount += 1;
            }
        }
        int sub = 2, room = 1, subcount = 0, roomcount = 0, periodnumber = 1;
        String Prsntday = null, section = null;
        ArrayList faculty = null;
        while (sub < entireList.size() && room < entireroomsList.size()) {
            if (sub == 2 && room == 1) {
                section = entireList.get(0);
                Prsntday = entireList.get(1);
            }
            if (subcount == columncount - 1 && roomcount == columncount - 1) {
                subcount = roomcount = 0;
                sub += 2;
                room += 1;
                Prsntday = entireList.get(sub - 1);
                section = entireList.get(sub - 2);
                periodnumber = 1;
            }
            String copy1 = entireList.get(sub);
            if(entireList.get(sub).contains("SCIRP")) {
                String subject1 = copy1.substring(0,copy1.indexOf("("));
                faculty = subjects.get(section.trim() + "," + subject1.trim());
            }else {
                faculty = subjects.get(section.trim() + "," + entireList.get(sub).trim());
            }
            //faculty = subjects.get(section.trim() + "," + entireList.get(sub).trim());
            if (faculty != null) {
                //System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+entireList.get(sub)+" "+entireroomsList.get(room)+", "+faculty);
                //
                int t = 0;
                while (t < faculty.size()) {
                    if(entireList.get(sub).contains("SCIRP")) {
                        String copy = entireList.get(sub);
                        String facultyNameIDP = entireList.get(sub).substring(entireList.get(sub).indexOf(")")+1,entireList.get(sub).length());
                        String subject = copy.substring(0,copy.indexOf("("));
                        if(periodnumber < 8) {
                            System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+subject+", "+entireroomsList.get(room)+", "+facultyNameIDP);
                            facultyFirebase1(timings[periodnumber - 1], periodnumber, section, Prsntday, subject, entireroomsList.get(room).toString(), facultyNameIDP);
                        }
                    }else {
                        if(periodnumber <8){
                            System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+entireList.get(sub)+", "+entireroomsList.get(room)+", "+faculty.get(t));
                            facultyFirebase1(timings[periodnumber - 1], periodnumber, section, Prsntday, entireList.get(sub), entireroomsList.get(room).toString(), faculty.get(t).toString());
                        }
                    }
                    t += 1;
                }
            }else {

                //facultyFirebasefornull(timings[periodnumber - 1], periodnumber, section, Prsntday, entireList.get(sub), entireroomsList.get(room).toString(), "None");
            }
            subcount += 1;
            roomcount += 1;
            sub += 1;
            room += 1;
            periodnumber += 1;
        }
        //student(entireList,entireroomsList,subjects);

    }

    public void facultyFirebase1(String timing, int periodnumber, String section, String prsntday, String subject, String room, String faculty) {
        int t=0;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FacultyDetails");
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("StudentDetails");

        data.child(section).child(prsntday).child(timing).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                students st = dataSnapshot.getValue(students.class);
                if(st.getPer()<8) {
                    if(st.getSub().contains("Lab") && !subject.contains("Lab")) {
                        //Log.e("Labs :",st.getPer()+", "+st.getDay()+", "+st.getSub()+", "+st.getSec()+", "+st.getRoom()+", "+st.getFaculty());
                        String key = section+","+st.getSub();
                        ArrayList<String> faculties = subjects.get(key.trim());
                        System.out.println(key+"   ::  "+faculties);
                        int NoOfFaculites=0;
                        while(NoOfFaculites < faculties.size()) {

                            DatabaseReference daa = FirebaseDatabase.getInstance().getReference("FacultyDetails");
                            daa.child(faculties.get(NoOfFaculites)).child(prsntday).child(year).child(st.getTime()).removeValue();
                            NoOfFaculites+=1;
                        }
                    }
                    if (!st.getSub().equals(subject)) {
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FacultyDetails");
                        String name = st.getFaculty();
                        String result = name.replaceAll("[-+.^:,]", "");
                        databaseReference1.child(result).child(st.getDay()).child(year).child(timing).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    DatabaseReference studentsaving = FirebaseDatabase.getInstance().getReference("StudentDetails");
                                    students newstudent = new students(periodnumber, faculty, section, prsntday, subject, room, timing);
                                    studentsaving.child(section).child(prsntday).child(timing).setValue(newstudent);
                                    System.out.println("faculty ki eyna theruvathae student changed");
                                }
                            }
                        });

                        //working : System.out.println("same kaduu update aendhiii data : "+st.getPer()+" "+st.getFaculty()+" "+st.getSec()+" "+st.getSub()+" "+faculty+" "+subject+periodnumber);
                    }
                    save();
                }

            }

            private void save() {
                if(periodnumber<8) {
                    saveFaculties saveFaculties = new saveFaculties(prsntday, faculty, section, room, subject, timing, periodnumber);
                    String name = saveFaculties.getName();
                    String result = name.replaceAll("[-+.^:,]","");
                    databaseReference.child(result).child(prsntday).child(year).child(timing).setValue(saveFaculties).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                DatabaseReference studentsaving = FirebaseDatabase.getInstance().getReference("StudentDetails");
                                students newstudent = new students(periodnumber,faculty,section,prsntday,subject,room,timing);
                                studentsaving.child(section).child(prsntday).child(timing).setValue(newstudent);
                                //System.out.println("faculty ki eyna theruvathae student changed");
                            }
                        }
                    });
                    //System.out.println("save cheystaaa poo");
                }
            }

            //pmob tues 2 hymavathii
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

   private void student(List<String> entireList, List<String> entireroomsList, HashMap<String, ArrayList> subjects) {
        int columncount=0;
        for(String key : entireList) {
            if(key.contains("II") || key.contains("IV") || key.contains("III")) {
                columncount=0;
            }else {
                columncount+=1;
            }
        }
        String[] timings = {"8:05-9:00","9:00-9:55","10:15-11:10","11:10-12:05","12:05-01:00","02:00-02:55","02:55-03:50","03:50-4:40","04:40-05:30"};
        int sub=2,room=1,subcount=0,roomcount=0,periodnumber=1;
        String Prsntday=null,section=null;
        while(sub < entireList.size() && room < entireroomsList.size()) {
            if(sub == 2 && room == 1) {
                section = entireList.get(0);
                Prsntday = entireList.get(1);
            }
            if(subcount == columncount-1 && roomcount == columncount-1) {
                subcount=roomcount=0;
                sub+=2;
                room+=1;
                Prsntday = entireList.get(sub-1);
                section = entireList.get(sub-2);
                periodnumber=1;
            }
            ArrayList faculty = subjects.get(section.trim()+","+entireList.get(sub).trim());
            if(faculty != null) {
                //System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+entireList.get(sub)+" "+entireroomsList.get(room)+", "+faculty.get(0));
                StudentFirebase(timings[periodnumber-1],periodnumber,section,Prsntday,entireList.get(sub),entireroomsList.get(room).toString(),faculty.get(0).toString());
            }else {
                if(entireList.get(sub).contains("SCIRP")) {
                    String copy = entireList.get(sub);
                    String facultyNameIDP = entireList.get(sub).substring(entireList.get(sub).indexOf(")")+1,entireList.get(sub).length());
                    String subject = copy.substring(0,copy.indexOf("("));
                    //System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+subject+" "+entireroomsList.get(room)+", "+facultyNameIDP);

                        StudentFirebase(timings[periodnumber - 1], periodnumber, section, Prsntday, subject, entireroomsList.get(room).toString(), facultyNameIDP);
                }else {
                        //System.out.println(periodnumber + ", " + timings[periodnumber - 1] + ", " + section + ", " + Prsntday + ", " + entireList.get(sub) + " " + entireroomsList.get(room) + ", " + "null");
                        StudentFirebase(timings[periodnumber - 1], periodnumber, section, Prsntday, entireList.get(sub), entireroomsList.get(room).toString(), "not mentioned");

                }
                //StudentFirebase(timings[periodnumber-1],periodnumber,section,Prsntday,entireList.get(sub),entireroomsList.get(room).toString(),"not mentioned");
            }
            subcount+=1;
            roomcount+=1;
            sub+=1;
            room+=1;
            periodnumber+=1;
        }
    }


    public void StudentFirebase(String time,int period, String section, String prsntday, String subject, String room, String faculty) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("StudentDetails");
        students st = new students(period,faculty,section,prsntday,subject,room,time);

        if(!st.getSub().equals("***")) {
            System.out.println("students : "+time+", "+st.getPer()+", "+st.getDay()+", "+st.getSec()+", "+st.getSub()+", "+st.getRoom()+", "+st.getFaculty());
            databaseReference.child(section).child(prsntday).child(time).setValue(st);
        }
    }


    public void facultyFirebasefornull(String timing, int periodnumber, String section, String prsntday, String subject, String room, String faculty) {

        DatabaseReference data = FirebaseDatabase.getInstance().getReference("StudentDetails");
        DatabaseReference data1 = FirebaseDatabase.getInstance().getReference("FacultyDetails");
        students st = new students(periodnumber,faculty,section,prsntday,subject,room,timing);
        data.child(section).child(prsntday).child(timing).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("json : ",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


/*
Faculties faculties;
    public void facultyData(students st, String timing) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FacultyDetails");
        ArrayList<String> facultydetails = new ArrayList<String>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    //Log.e("data : ",snap.getValue().toString());
                    facultydetails.add(snap.getKey());
                }
                for(String id : facultydetails) {
                    facultysave(id,st,timing);
                }
            }
            public void facultysave(String id, students st, String timing) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FacultyDetails");
                databaseReference1.child(id).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.child("Id").getValue(String.class);
                        String name = dataSnapshot.child("Name").getValue(String.class);
                        String subject = dataSnapshot.child("subject").getValue(String.class);
                        faculties = new Faculties(id,name,subject);
                        if(faculties.getId() == id) {
                            saveFaculties saveFaculties = new saveFaculties(faculties.getId(),st.getPer(),st.getSec(),st.getRoom(),st.getSub(),timing);
                            //databaseReference1.child(st.getDay()).child(st.getTime()).setValue(saveFaculties);
                            Log.e("student details : ",st.getPer()+", "+st.getTime()+", "+st.getRoom()+", "+st.getSec()+", "+st.getSub()+", "+st.getFaculty());
                            Log.e("Faculty details : ",saveFaculties.getPeriod()+", "+saveFaculties.getTime()+", "+saveFaculties.getRoom()+", "+saveFaculties.getSectionId()+", "+saveFaculties.getShortVal()+", "+saveFaculties.getFacultyId());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
 */
/*
public void FacultyFirebase(int i, int period, String section, String prsntday, String subject, String room, String faculty, int periodcount) {
        ArrayList<String> facultyDetails = new ArrayList<>();
        final Faculties[] faculties = new Faculties[1];
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("FacultyDetails");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("snap : ",dataSnapshot.getValue().toString());
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    //Log.e("ss : ",snap.getKey());
                    facultyDetails.add(snap.getKey());
                }
                //System.out.println("hello : "+facultyDetails);
                ff(facultyDetails);
            }
            private void ff(ArrayList<String> facultyDetails) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FacultyDetails");
                databaseReference1.child(facultyDetails.get(0)).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Id = dataSnapshot.child("Id").getValue(String.class);
                        String Name = dataSnapshot.child("Name").getValue(String.class);
                        String sub = dataSnapshot.child("subject").getValue(String.class);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
 */


/*
private void FacultyFirebase(ArrayList<String> list, List<String> entireList, List entireroomsList,HashMap<String,ArrayList> subjects) {
        int columncount=0;
        for(String key : entireList) {
            if(key.contains("II")) {
                columncount=0;
            }else {
                columncount+=1;
            }
        }
        String[] timings = {"8:10-9:00","9:00-9:50","10:05-10:55","10:55-11:45","11:45-12:35","1:30-2:20","2:20-3:10","3:10-4:00","4:00-4:50"};
        int sub=2,room=1,subcount=0,roomcount=0,periodnumber=1;
        String Prsntday=null,section=null;
        while(sub < entireList.size() && room < entireroomsList.size()) {
            if(sub == 2 && room == 1) {
                section = entireList.get(0);
                Prsntday = entireList.get(1);
            }
            if(subcount == columncount-1 && roomcount == columncount-1) {
                subcount=roomcount=0;
                sub+=2;
                room+=1;
                Prsntday = entireList.get(sub-1);
                section = entireList.get(sub-2);
                periodnumber=1;
            }
            int t=0;
            ArrayList faculty = subjects.get(section.trim()+","+entireList.get(sub).trim());
            //System.out.println(subjects.get(section.trim()+","+entireList.get(sub).trim()));
            if(faculty != null && faculty.size() > 1) {
                while(t  < faculty.size()) {
                    int l=0;
                    System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+entireList.get(sub)+" "+entireroomsList.get(room)+", "+faculty.get(t));
                    t++;
                }
            }else if(faculty != null && faculty.size() == 1) {
                System.out.println(periodnumber+", "+timings[periodnumber-1]+", "+section+", "+Prsntday+", "+entireList.get(sub)+" "+entireroomsList.get(room)+", "+faculty.get(0));
            }
            subcount+=1;
            roomcount+=1;
            sub+=1;
            room+=1;
            periodnumber+=1;
        }
    }
 */
