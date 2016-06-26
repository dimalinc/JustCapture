package mj.android.justcapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NotesListActivity extends Activity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LAST_NOTEPAD_ID = "last_notepad_id";
    protected static SharedPreferences mSettings;

    static String notepadName;

    static int lastId=0;

    NotePad notepad;

    Button addNote, deleteNote,cleanProgramDir, btnSaveNotepad;

    static int noteIndex = 0;

    static final int REQUEST_CODE_NEW_NOTE = 4;

    ArrayList<Note> noteList = new ArrayList<>();
    ArrayList<File> listFiles;

    TextView textView;
    EditText etNotepadName;

    ListAdapter adapter;
    ListView listView;

    //Создаем массив разделов:
    private String head_array[] = {
            "00. Начало",
            "01. Чем кормить кота.",
            "02. Как гладить кота.",
            "03. Как спит кот.",
            "04. Как играть с котом.",
            "05. Как разговаривать с котом",
            "06. Интересные факты из жизни котов.",
            "07. Как назвать кота.",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

       // notepad = (NotePad)getIntent().getExtras().get("newNotepad");


        listView = (ListView) findViewById(R.id.listView);

       /* noteList.add(new Note(01,"name123","category123"));
        noteList.add(new Note(02,"name124","category124"));*/



        adapter = new SimpleAdapter(this, createDataArrayList(), R.layout.note_row,
                new String[] { "name", "category" }, new int[] {
                R.id.tv1, R.id.tv2 });
        listView.setAdapter(adapter);


        //TODO write generation of notepadNames
        notepadName = "notepad1";

        addNote = (Button)findViewById(R.id.buttonAddNote);
        deleteNote = (Button)findViewById(R.id.buttonDeleteNote);
        cleanProgramDir = (Button)findViewById(R.id.buttonCleanProgramDir);
        btnSaveNotepad = (Button)findViewById(R.id.btnSaveNotepad);
        addNote.setOnClickListener(this);
        deleteNote.setOnClickListener(this);
        cleanProgramDir.setOnClickListener(this);
        btnSaveNotepad.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.textView);
        etNotepadName = (EditText)findViewById(R.id.etNotepadName);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(APP_PREFERENCES_LAST_NOTEPAD_ID)) {
            // Получаем число из настроек
            lastId = mSettings.getInt(APP_PREFERENCES_LAST_NOTEPAD_ID, 0);
        }



        etNotepadName.setText("Блокнот_"+ (lastId+1));
      //  etNotepadName.setText(notepad.name);
        etNotepadName.selectAll();


        // Проверяем и создаем директорию
        checkAndCreateProgramDir();



        // Получим идентификатор ListView
       // ListView listView = (ListView) findViewById(R.id.listView);
        //устанавливаем массив в ListView
        /*listView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, head_array)); // заменил head_array на noteList
        listView.setTextFilterEnabled(true);*/





        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(NotesListActivity.this, NoteActivity.class);

                intent.putExtra("head", position);

                //запускаем вторую активность
                startActivity(intent);
            }
        });
    }




    private ArrayList<HashMap<String, Object>> createDataArrayList() {

        // Упаковываем данные
        ArrayList<HashMap<String, Object>> data = new ArrayList<>(
                noteList.size());
        HashMap<String, Object> map;
        for (int i = 0; i < noteList.size(); i++) {
            map = new HashMap<>();
            map.put("name", noteList.get(i).name);
            map.put("category", noteList.get(i).category);
            data.add(map);
        }

        return data;
    }

    private void checkAndCreateProgramDir() {
        File path = new File (Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName);
        if (! path.exists()){
            if (!path.mkdirs())
                Toast.makeText(NotesListActivity.this, "unable to create notepad dir", Toast.LENGTH_SHORT).show();
        }
    }


    void createNewNote() {
        noteIndex++;
        // Проверяем и создаем директорию
        File path = new File (Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName + "/" + notepadName
                + "/" + noteIndex);
        if (! path.exists()){
            if (!path.mkdirs()) {
                Toast.makeText(NotesListActivity.this, "unable to create note dir", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(this, NoteActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_NEW_NOTE) && (resultCode != 0)) {

            Note gotNote = (Note) data.getParcelableExtra("note");

            noteList.add(gotNote);
            Log.d("myLogs","gotNote read from intent id = " + gotNote.id);

            textView.setText(gotNote.name);

            Log.d("myLogs","Note extracted from parcel" );
        }

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

    public ArrayList<File> listFilesWithSubFolders(File dir) {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesWithSubFolders(file));
            else
                files.add(file);
        }
        return files;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddNote:
                createNewNote();
                Log.d("myLogs", "buttonAddNote click ");
                Toast.makeText(NotesListActivity.this,
                        "buttonAddNote click ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonDeleteNote:
                Log.d("myLogs", "buttonDeleteNote click ");
                Toast.makeText(NotesListActivity.this,
                        "buttonDeleteNote click ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonCleanProgramDir:
                deleteFileOrDirRecursievely(new File(Environment.getExternalStorageDirectory(),NoteActivity.programDirectoryName));
                checkAndCreateProgramDir();
                break;
            case R.id.btnSaveNotepad:
                 saveNotePad();
                // finish();
                break;
        }
    }

    void saveNotePad()  {

        notepad = new NotePad();
        notepad.name = etNotepadName.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("notepad", notepad);
        Log.d("myLogs", "notePad returned as result of NotesListActivity");
        setResult(RESULT_OK, intent);
        finish(); // оставить или убрать?
        Log.d("myLogs", "finish() called");


    }

    @Override
    protected void onPause() {
        super.onPause();
       // saveNotePad();


    }

    @Override
    protected void onResume() {
        super.onResume();

        listFiles = listFilesWithSubFolders(new File(Environment.getExternalStorageDirectory(),NoteActivity.programDirectoryName));

        for (File file:listFiles) {
            Log.d("myLogs", file.getAbsolutePath());
        }

        adapter = new SimpleAdapter(this, createDataArrayList(), R.layout.note_row,
                new String[] { "name", "category" }, new int[] {
                R.id.tv1, R.id.tv2 });
        listView.setAdapter(adapter);


        if (mSettings.contains(APP_PREFERENCES_LAST_NOTEPAD_ID)) {
            lastId = mSettings.getInt(APP_PREFERENCES_LAST_NOTEPAD_ID,0);
        }
    }


}