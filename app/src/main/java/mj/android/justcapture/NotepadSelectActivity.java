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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class NotepadSelectActivity extends Activity implements View.OnClickListener{

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LAST_NOTEPAD_ID = "last_notepad_id";
    private SharedPreferences mSettings;

    final static int REQUEST_CODE_NEW_NOTEPAD = 5;

    ListAdapter adapter;
    ListView listView;

    ArrayList<NotePad> notePadsList = new ArrayList<>();

    Button btnAddNotepad, btnDeleteAllNotepads;


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

        adapterInit();

        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(NotepadSelectActivity.this, NotesListActivity.class);

                intent.putExtra("head", position);

                //запускаем вторую активность
                startActivity(intent);
            }
        });
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

    void addNotepad() {

        Intent intent = new Intent(this, NotesListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_NOTEPAD);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);


        if ((requestCode == REQUEST_CODE_NEW_NOTEPAD) && (resultCode == RESULT_OK)) {

            NotePad gotNotePad = (NotePad) data.getParcelableExtra("notepad");

            notePadsList.add(gotNotePad);
            Log.d("myLogs", "gotNotePad read from intent name = " + gotNotePad.name);

           // textView.setText(gotNotePad.name);

            Log.d("myLogs","NotePad extracted from parcel" );

            adapterInit();
        }

    }

    void deleteAlNotepads() {
        // TODO Доработать, чтобы удалялись только данные блокнотов, а не файлы в корневой паке приложения - вроде сделано, проверить
        //deleteFileOrDirRecursievely(new File(Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName));
        File rootDir = new File(Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName);
        for (File file:rootDir.listFiles()) {
            if (file.isDirectory())
                deleteFileOrDirRecursievely(file);
        }

        NotePad.lastId = 0;

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LAST_NOTEPAD_ID, 0);
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

    void saveNotepadsToFile() {


    }

    @Override
    protected void onPause() {
        super.onPause();


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_LAST_NOTEPAD_ID)) {
            NotePad.lastId = mSettings.getInt(APP_PREFERENCES_LAST_NOTEPAD_ID,0);
        }
    }
}
