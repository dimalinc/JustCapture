package mj.android.justcapture;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Note implements Parcelable {
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
