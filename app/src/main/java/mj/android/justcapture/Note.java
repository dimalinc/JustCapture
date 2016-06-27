package mj.android.justcapture;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Note implements Parcelable {

    public static final String NOTES_LIST_FILE_NAME = "description.fil";
    public static final String STRING_SEPARATOR_IN_FILE = "_-_";
    public static final int PARSING_ARRAY_STRING_LENGTH = 4;

    int id;
    String sku;
    String name;
    String category;
    String description;
    ArrayList<String> photosList;
    Date dateCreated;
    Date lastModified;

    public Note(int id) {
        this.id = id;
    }

    public Note (int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }


    static void saveNotesListToFile(ArrayList<NotePad> incomingNotepadsList) {

       /*  try {

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

            */

    }

    static ArrayList<Note> readNotesListFromFile() {
      /*  File f = new File(generateNoteFileAndGetFileName().substring(8));

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
            return parseReadFromFileNotesListString(readFromFileNotepadsListString);


            // TODO: добавить проверку на null
        else

         */

         return null;

    }

    public static ArrayList<Note> parseReadFromFileNotesListString(String incoming) {
        ArrayList<Note> notepadsListParsedFromString = new ArrayList<>();

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
               // notepadsListParsedFromString.add(notePad);
                Log.d("myLogs"," one more notepad parsed from string. Notepad id = " + notePad.id);
            }


        }


        return notepadsListParsedFromString;
    }


    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        // распаковываем объект из Parcel
        public Note createFromParcel(Parcel in) {
            Log.d("myLogs", "createFromParcel");
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private Note (Parcel parcel) {
        Log.d("myLogs","Note(Parcel parcel)");
        id = parcel.readInt();
        sku = parcel.readString();
        name = parcel.readString();
        category = parcel.readString();
        description = parcel.readString();
     // TODO разобраться как прочитать массив из PARCEL readArray
       // photosList = parcel.readArray();
    }

        public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("myLog", "writingNoteToParcel");
        dest.writeInt(id);
        dest.writeString(sku);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(description);
//        dest.writeArray(photosList.toArray());

        //TODO writeDate

    }
}
