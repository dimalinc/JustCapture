package mj.android.justcapture;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class NotepadSelectActivity extends Activity implements View.OnClickListener{

    final static int REQUEST_CODE_NEW_NOTEPAD = 5;

    ListAdapter adapter;
    ListView listView;

    ArrayList<NotePad> notePadsList = new ArrayList<>();

    Button btnAddNotepad, btnDeleteAllNotepads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_select);

        btnAddNotepad = (Button)findViewById(R.id.buttonAddNotepad);
        btnDeleteAllNotepads = (Button)findViewById(R.id.buttonDeleteAlNotepads);
        btnAddNotepad.setOnClickListener(this);
        btnDeleteAllNotepads.setOnClickListener(this);


        listView = (ListView) findViewById(R.id.listView);

       // notePadsList.add(new NotePad());
       // notePadsList.add(new NotePad());

        adapterInit();

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
        NotePad notepad = new NotePad();

        Intent intent = new Intent(this, NotesListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_NOTEPAD);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_NEW_NOTEPAD) && (resultCode != 0)) {

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

}
