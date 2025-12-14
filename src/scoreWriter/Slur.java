package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.GraphicalNote;
import graphical.GraphicalObject;
import model.Voice;

public class Slur extends CurvedConnection {

    @Override
    public void assignToNotes(GraphicalNote startNote, GraphicalNote endNote) {
        this.startNote = startNote;
        this.endNote = endNote;

        startNote.setSlur(this);
        endNote.setSlur(this);

        startNote.slurStart();
        endNote.slurEnd();

        setX(startNote.getX());
        setY(startNote.getY());
        setX1(endNote.getX());
        setY1(endNote.getY());
    }
    
    public List<GraphicalNote> getNotesUnderSlur(Score score) {
        List<GraphicalNote> notes = new ArrayList<>();

        Voice voice = score.getVoiceOf(startNote);
        if (voice == null) 
            return notes;

        List<GraphicalObject> objs = voice.getObjects();

        boolean inside = false;

        for (GraphicalObject obj : objs) {

            if (obj == startNote) {
                inside = true;
                notes.add(startNote);
                continue;
            }

            if (!inside)
                continue;

            if (obj instanceof GraphicalNote note) {
                notes.add(note);

                if (note == endNote)
                    break;
            }
        }

        return notes;
    }

}
