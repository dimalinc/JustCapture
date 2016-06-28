package mj.android.justcapture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class NotePad  /*extends NotePadSerializeable */implements Parcelable{

    public static final String NOTEPADS_LIST_FILE_NAME = "notePadsList.fil";
    public static final String STRING_SEPARATOR_IN_FILE = "_-_";
    public static final int PARSING_ARRAY_STRING_LENGTH = 4;


    public static final String NOTEPAD_PREF_KEY = "NotePad";

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LAST_NOTEPAD_ID = "last_notepad_id";

    private SharedPreferences mSettings;

    static int lastId=NotesListActivity.lastId;

    int id;
    String name;
    long dateCreated;
    long dateModified;

    static String generateNotePadsFileAndGetFileName() {
        // Проверяем доступность SD карты
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;

        // Проверяем и создаем директорию
        File path = new File(Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName );
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return null;
            }
        }


        // Создаем имя файла
        File newFile = new File(path.getPath() + File.separator + NOTEPADS_LIST_FILE_NAME);

        // создаем файл
        if (!newFile.exists())
            try {
                if (newFile.createNewFile()) {
                    Log.d("myLogs", newFile.getName() + " has been created");
                } else {
                    Log.d("myLogs", newFile.getName() + " already exists");
                }
            } catch (IOException e) {e.printStackTrace();}
        Log.d("myLogs", "notePadsList filename generated " + Uri.fromFile(newFile).toString());

        return Uri.fromFile(newFile).toString();

    }

    static void saveNotePadsToFile(ArrayList<NotePad> incomingNotepadsList) {

        try {

            //FileWriter writer = new FileWriter(generateNotePadsFileAndGetFileName(), false);


            OutputStream os = new FileOutputStream(generateNotePadsFileAndGetFileName().substring(8));


            for (NotePad notePad:incomingNotepadsList) {
                String notePadString = "id=" + notePad.id + STRING_SEPARATOR_IN_FILE +
                        "name=" + notePad.name + STRING_SEPARATOR_IN_FILE +
                        "dateCreated=" + notePad.dateCreated + STRING_SEPARATOR_IN_FILE +
                        "dateModified=" + notePad.dateModified + STRING_SEPARATOR_IN_FILE
                        + System.getProperty("line.separator")
                       ;


                os.write(notePadString.getBytes());

            }


            os.close();
        } catch (IOException e) {
            Log.d ("myLogs","Exception when writing notePadsList to file");
            e.printStackTrace();}
    }

    static ArrayList<NotePad> readNotepadsListFromFile() {
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
    }

    public static ArrayList<NotePad> parseReadFromFileNotepadsListString(String incoming) {
        ArrayList<NotePad> notepadsListParsedFromString = new ArrayList<>();

        String[] stringArray = incoming.split(System.getProperty("line.separator"));
        Log.d ("myLogs", " number of strings parsed from notePadsListFile = " + stringArray.length);

        for (String string:stringArray) {
            String[] array = string.split(STRING_SEPARATOR_IN_FILE);
            Log.d("myLogs", "array of strings, parsed from one string in notepadsListFile length = " + array.length);
            if (array.length == PARSING_ARRAY_STRING_LENGTH) {


                NotePad notePad = new NotePad(Integer.parseInt(array[0].substring( array[0].indexOf("=")+1 )),
                        array[1].substring(array[1].indexOf("=")+1),
                        Long.parseLong(array[2].substring(array[2].indexOf("=")+1)),
                        Long.parseLong(array[3].substring(array[3].indexOf("=")+1)));
                notepadsListParsedFromString.add(notePad);
                Log.d("myLogs"," one more notepad parsed from string. Notepad id = " + notePad.id);
            }
        }


        return notepadsListParsedFromString;
    }




    static ArrayList<NotePad> loadNotePadListFromFile() {
        ArrayList<NotePad> notePadsArrayList = new ArrayList<>();



        return notePadsArrayList;
    }

    static void saveData(ArrayList<NotePad> incomingNotepadsList) {
        JSONArray jsonArray = new JSONArray();
        for (NotePad c : incomingNotepadsList) {
            jsonArray.put(c.toJSON());
        }
        //SAVE

        SharedPreferences.Editor editor = NotepadSelectActivity.mSettings.edit();
        editor.putString(NOTEPAD_PREF_KEY, jsonArray.toString());
        editor.apply();


    }

    static ArrayList<NotePad> getData() throws JSONException {

        //String string = Preferences.get().getString(NOTEPAD_PREF_KEY, null);
        String string = NotepadSelectActivity.mSettings.getString(NOTEPAD_PREF_KEY, "");
        Log.d("myLogs","String read from prefs = " + string);
        ArrayList<NotePad> notePadsList = new ArrayList<NotePad>();
        if ((string == null) || (string.length()==0)) {
            notePadsList.add(new NotePad());
            return notePadsList;
        }

        JSONArray array = new JSONArray(string);
        Log.d("myLogs","JSONArray length = " + array.length());

        for (int i = 0; i < array.length(); i++) {
            notePadsList.add(NotePad.create(array.getJSONObject(i)));
        }
        return notePadsList;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("dateCreated", dateCreated);
            jsonObject.put("dateModified", dateModified);
            Log.d("myLogs", "NotePad written to JSON");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static NotePad create(JSONObject json) {
        try {
            NotePad notePad = new NotePad();
            notePad.id = json.getInt("id");
            notePad.name = json.getString("name");
            notePad.dateCreated = json.getLong("dateCreated");
            notePad.dateModified = json.getLong("dateModified");
            Log.d("myLogs","NotePad created from JSON");
            return notePad;
        } catch (JSONException e) {
            Log.d("myLogs","EXCEPTION when creating NotePad from JSON - returning new blank Notepad");
            e.printStackTrace();
            return new NotePad();
        }
    }

    public NotePad(int id, String name, long dateCreated, long dateModified) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

    public NotePad() {

        mSettings = NotesListActivity.mSettings;

        if (mSettings.contains(APP_PREFERENCES_LAST_NOTEPAD_ID)) {
            // Получаем число из настроек
            lastId = mSettings.getInt(APP_PREFERENCES_LAST_NOTEPAD_ID, 0);
        }

            id = ++lastId;

            notepadCreateDir(id);
    }

    void notepadCreateDir(int id) {
        File path = new File (Environment.getExternalStorageDirectory(), NoteActivity.programDirectoryName +
                File.separator + "notepad" +  id);
        if (! path.exists()){
            if (!path.mkdirs()) {
                Log.d("myLogs", "couldn't create " + path);
                return;
            }
            Log.d("myLogs", "created " + path);

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(NotesListActivity.APP_PREFERENCES_LAST_NOTEPAD_ID, id);
            editor.apply();

        }

    }

    public static final Parcelable.Creator<NotePad> CREATOR = new Parcelable.Creator<NotePad>() {
        // распаковываем объект из Parcel
        public NotePad createFromParcel(Parcel in) {
            Log.d("myLogs", "create_NotePad_FromParcel");
            return new NotePad(in);
        }

        @Override
        public NotePad[] newArray(int size) {
            return new NotePad[size];
        }
    };

    private NotePad (Parcel parcel) {
        Log.d("myLogs","Note(Parcel parcel)");
        id = parcel.readInt();
        name = parcel.readString();
        // TODO разобраться как прочитать массив из PARCEL readArray
        // photosList = parcel.readArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("myLog", "writingNoteToParcel");
        dest.writeInt(id);
        dest.writeString(name);

        //TODO writeDate

    }
}
