package mj.android.justcapture;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.util.Date;


public class NotePad implements Parcelable {
    static int lastId=0;

    int id;
    String name;
    Date dateCreated;
    Date lastModified;

    public NotePad() {
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
            Log.d("myLogs","created " + path);
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
