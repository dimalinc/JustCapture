package mj.android.justcapture;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class NotepadList implements Serializable {
    public ArrayList<NotePad> notePadsList;
    final String noteListSerializeFilename = NoteActivity.programDirectoryName + File.separator + NotepadSelectActivity.APP_PREFERENCES_NOTEPADS_LIST+".ser";


    public void serializeNotepadList(ArrayList<NotePad> incomingNotepadList) {

        NotepadList notepadList = new NotepadList();
        notepadList.notePadsList = incomingNotepadList;

        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(noteListSerializeFilename)));
            out.writeObject(notepadList);
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
        NotepadSelectActivity.notePadsList = notePadsList;

    }
}
