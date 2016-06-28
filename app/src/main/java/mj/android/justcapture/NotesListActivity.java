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
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NotesListActivity extends Activity implements View.OnClickListener {

    public static final int ID_DEFAULT_VALUE = -1;
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






        //TODO write generation of notepadNames

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









        // Получим идентификатор ListView
       // ListView listView = (ListView) findViewById(R.id.listView);
        //устанавливаем массив в ListView
        /*listView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, head_array)); // заменил head_array на noteList
        listView.setTextFilterEnabled(true);*/




        Intent intent = getIntent();

        int id = intent.getIntExtra("id",ID_DEFAULT_VALUE);

        if (id == ID_DEFAULT_VALUE) {

            if (mSettings.contains(APP_PREFERENCES_LAST_NOTEPAD_ID)) {
                // Получаем число из настроек
                lastId = mSettings.getInt(APP_PREFERENCES_LAST_NOTEPAD_ID, 0);
            }

            etNotepadName.setText("notepad"+ (lastId+1));
            //  etNotepadName.setText(notepad.name);
            etNotepadName.selectAll();
            notepad = new NotePad();
            notepad.name = etNotepadName.getText().toString();


        } else {
            notepad = NotepadSelectActivity.notePadsList.get(id);
            etNotepadName.setText(notepad.name);
        }

        notepadName = notepad.name;







        // Проверяем и создаем директорию
        checkAndCreateProgramDir();


        noteListInit();

        adapter = new SimpleAdapter(this, createDataArrayList(), R.layout.note_row,
                new String[] { "name", "category" }, new int[] {
                R.id.tv1, R.id.tv2 });
        listView.setAdapter(adapter);

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

    void noteListInit() {

        noteList = readNotesFromFileSystem();

    }


    ArrayList<Note> readNotesFromFileSystem() {

        File path = new File (Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName +
                File.separator + notepad.name );

       return readNotesFromPath(path);


    }

    ArrayList<Note> readNotesFromPath(File file) {
        if(!file.exists())
            return null;
        if(file.isDirectory())
        {
            for(File notePath : file.listFiles()) {

                // парсим имя заметки и создаем заметку с таким именем
                Note note = new Note( Integer.parseInt(notePath.getName()) );

                for (File descriptionNoteFile: notePath.listFiles() ) {
                    if (descriptionNoteFile.getName().contains("description"))
                        noteDescriptionParse(note, descriptionNoteFile);
                }

               //

                noteList.add(note);
            }
        }
        else {

        }

        return noteList;
    }

    void noteDescriptionParse(Note note, File file) {

        String readFromFileNoteString = null;
        try
        {
            FileReader reader = new FileReader(file);

            char[] buffer = new char[(int)file.length()];
            // считаем файл полностью
            reader.read(buffer);
            readFromFileNoteString = new String(buffer);
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }


        if (readFromFileNoteString != null)
            parseReadFromFileNoteString(note, readFromFileNoteString);




    }

    static void parseReadFromFileNoteString(Note note, String readFromFileNoteString) {

        String[] stringArray = readFromFileNoteString.split("\n" + System.getProperty("line.separator"));
        Log.d ("myLogs", " number of strings parsed from FileNoteString = " + stringArray.length);

        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].startsWith("NoteID = "))
                note.id = Integer.parseInt(stringArray[i].substring(9));

            if (stringArray[i].startsWith("NoteNAME = "))
                note.name = stringArray[i].substring(11);

            if (stringArray[i].startsWith("NoteSKU = "))
                note.sku = stringArray[i].substring(10);

            if (stringArray[i].startsWith("NoteCATEGORY = "))
                note.category = stringArray[i].substring(15);

            if (stringArray[i].startsWith("NoteDESCRIPTION = "))
                note.description = stringArray[i].substring(17);

        }


    }




    /*static ArrayList<NotePad> readNotepadsListFromFile() {
        File f = new File(generateNotePadsFileAndGetFileName().substring(8));

        String readFromFileNotepadsListString = null;
        try
        {
            FileReader reader = new FileReader(f);

            char[] buffer = new char[(int)f.length()];
            // считаем файл полностью
            reader.read(buffer);
            readFromFileNotepadsListString = new String(buffer);
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }


        if (readFromFileNotepadsListString != null)
            return parseReadFromFileNotepadsListString(readFromFileNotepadsListString);


            // TODO: добавить проверку на null
        else return null;
    }*/



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
                Toast.makeText(NotesListActivity.this, "unable to create program dir", Toast.LENGTH_SHORT).show();
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