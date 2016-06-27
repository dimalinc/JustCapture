package mj.android.justcapture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NotepadSelectActivity extends Activity implements View.OnClickListener{


    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NOTEPADS_LIST = "notepadsList";
    static SharedPreferences mSettings;

    final static int REQUEST_CODE_NEW_NOTEPAD = 5;

    final String noteListSerializeFilename = NoteActivity.programDirectoryName + File.separator + NotepadSelectActivity.APP_PREFERENCES_NOTEPADS_LIST+".ser";

    ListAdapter adapter;
    ListView listView;

    public static ArrayList<NotePad> notePadsList= new ArrayList<>();

    /*static {
        try {
            notePadsList = NotePad.getData();
            Log.d("myLogs", "notePadsList loaded from preferences");
        } catch (org.json.JSONException e) {e.printStackTrace();}
    }*/
        public static ArrayList<NotePad> notePadsList2 = new ArrayList<>(); //



    Button btnAddNotepad, btnDeleteAllNotepads;

    NotePad newNotePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_select);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        btnAddNotepad = (Button)findViewById(R.id.buttonAddNotepad);
        btnDeleteAllNotepads = (Button)findViewById(R.id.buttonDeleteAlNotepads);
        btnAddNotepad.setOnClickListener(this);
        btnDeleteAllNotepads.setOnClickListener(this);


        listView = (ListView) findViewById(R.id.listView);


       // notePadsList.add(new NotePad());
       // notePadsList.add(new NotePad());

        notePadsListInit();

        adapterInit();

        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(NotepadSelectActivity.this, NotesListActivity.class);

                intent.putExtra("id", position);

                //запускаем вторую активность
                // startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_NEW_NOTEPAD);

            }
        });
    }

    void addNotepad() {

        Intent intent = new Intent(this, NotesListActivity.class);

        // newNotePad = new NotePad();
        // intent.putExtra("newNotepad",newNotePad);

        startActivityForResult(intent, REQUEST_CODE_NEW_NOTEPAD);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonAddNotepad:
                addNotepad();
                break;
            case R.id.buttonDeleteAlNotepads:
                deleteAlNotepads();
                break;
        }
    }

    void notePadsListInit() {

        notePadsList = NotePad.readNotepadsListFromFile();

    }


    void adapterInit() {
        adapter = new SimpleAdapter(this, createDataArrayList(), R.layout.notepad_row,
                new String[] { "name", "date" }, new int[] {
                R.id.tvNotepadName, R.id.tvNotepadDate });
        listView.setAdapter(adapter);
    }


    private ArrayList<HashMap<String, Object>> createDataArrayList() {

        // Упаковываем данные

        ArrayList<HashMap<String, Object>> data = new ArrayList<>(
                notePadsList.size());
        HashMap<String, Object> map;
        for (int i = 0; i < notePadsList.size(); i++) {
            map = new HashMap<>();
            map.put("name", notePadsList.get(i).name);

            data.add(map);
        }

        return data;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);


        if ((requestCode == REQUEST_CODE_NEW_NOTEPAD) && (resultCode == RESULT_OK)) {

            NotePad gotNotePad = (NotePad) data.getParcelableExtra("notepad");

            boolean oldNotepad = false;
            for (NotePad notePad: notePadsList) {
                if (notePad.id == gotNotePad.id) {
                    oldNotepad = true;
                    break;
                }
            }

            if (!oldNotepad)
            notePadsList.add(gotNotePad);

            // сохранение списка блокнотов

            NotePad.saveNotePadsToFile(notePadsList);
            notePadsList = NotePad.readNotepadsListFromFile();


           // serializeNotepadList(notePadsList);

           // NotePad.saveData(notePadsList);






            /*if (notePadsList.equals(notePadsList2)) Log.d("myLogs", "notePadsList.saved equals notepadlist.loaded");
            if (notePadsList.size()==(notePadsList2.size())) Log.d("myLogs", "notePadsList.saved.size equals notepadlist.loaded.size");
            for (int i = 0; i <notePadsList.size() ; i++) {
                Log.d("myLogs", notePadsList.get(i).name);
            }*/

           /* for (int i = 0; i <notePadsList.size() ; i++) {
                Log.d("myLogs", notePadsList2.get(i).name);
            }*/

            Log.d("myLogs", "gotNotePad read from intent name = " + gotNotePad.name);



           // textView.setText(gotNotePad.name);

            Log.d("myLogs","NotePad extracted from parcel" );

            adapterInit();
        }

    }

    public void serializeNotepadList(ArrayList<NotePad> incomingNotepadList) {

       /* NotepadList notepadList = new NotepadList();
        notepadList.notePadsList = incomingNotepadList;*/

        ObjectOutputStream out = null;
        try {

            File file = new File (Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName +
                    File.separator + NotepadSelectActivity.APP_PREFERENCES_NOTEPADS_LIST+".ser" );
            if (!file.exists()){
                if (!file.createNewFile())
                    Toast.makeText(NotepadSelectActivity.this, "unable to create notepadList file", Toast.LENGTH_SHORT).show();
            }
            out = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));

            for (NotePad incomingNotePad: incomingNotepadList) {
                out.writeObject(incomingNotePad.toJSON());
            }



            out.close();

            Toast.makeText(NotepadSelectActivity.this, "notepadsList written to" + noteListSerializeFilename, Toast.LENGTH_LONG).show();


        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

    }

    public  void deserialize() {

        ObjectInputStream in = null;
        NotepadList restObj = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(noteListSerializeFilename)));
            restObj = (NotepadList)in.readObject();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }


        notePadsList = restObj.notePadsList;

    }

    void deleteAlNotepads() {
        // TODO Доработать, чтобы удалялись только данные блокнотов, а не файлы в корневой паке приложения - вроде сделано, проверить
        //deleteFileOrDirRecursievely(new File(Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName));
        File rootDir = new File(Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName);
        for (File file:rootDir.listFiles()) {
            if ( (file.isDirectory()) || ( file.getName().contains(NotePad.NOTEPADS_LIST_FILE_NAME) ) )
                deleteFileOrDirRecursievely(file);


        }

        NotePad.lastId = 0;

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_NOTEPADS_LIST, 0);
        editor.apply();

        notePadsList = new ArrayList<>();
        adapterInit();
    }

    public void deleteFileOrDirRecursievely(File file)
    {
        if(!file.exists())
            return;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                deleteFileOrDirRecursievely(f);
            file.delete();
        }
        else {
            file.delete();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_NOTEPADS_LIST)) {
            NotePad.lastId = mSettings.getInt(APP_PREFERENCES_NOTEPADS_LIST,0);
        }
    }
}
