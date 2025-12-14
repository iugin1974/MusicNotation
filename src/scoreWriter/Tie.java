package scoreWriter;

import graphical.GraphicalNote;
import model.Voice;

public class Tie extends CurvedConnection {

    @Override
    public void assignToNotes(GraphicalNote startNote, GraphicalNote endNote) {
        this.startNote = startNote;
        this.endNote = endNote;

        startNote.setTie(this);
        endNote.setTie(this);

        startNote.tieStart();
        endNote.tieEnd();

        setX(startNote.getX());
        setY(startNote.getY());
        setX1(endNote.getX());
        setY1(endNote.getY());
    }
    
    public boolean isValid(Score score) {

        Voice v1 = score.getVoiceOf(startNote);
        Voice v2 = score.getVoiceOf(endNote);

        // stessa voce
        if (v1 != v2)
            return false;

        // stessa altezza grafica (o pitch)
        if (startNote.getY() != endNote.getY())
            return false;
        // oppure: if (startNote.getPitch() != endNote.getPitch()) return false;

        // devono essere consecutive nella voce
        return score.areNotesConsecutive(startNote, endNote);
    }


    public void detach() {
        startNote.setTie(null);
        endNote.setTie(null);
    }
    
}
