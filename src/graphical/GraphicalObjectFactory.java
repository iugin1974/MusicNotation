package graphical;

import Measure.Bar;
import Measure.TimeSignature;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Score;
import notation.StaffMapper;

public final class GraphicalObjectFactory {

    private GraphicalObjectFactory() {}

    public static GraphicalObject create(
            MusicObject obj,
            GraphicalScore gScore,
            GraphicalStaff gStaff,
            int x
    ) {
        GraphicalObject g;
        if (obj instanceof Bar) {
            g = new GraphicalBar((Bar) obj);
            g.setY(gStaff.getYPosOfLine(0));
        }
        else if (obj instanceof Note) {
        	Note note = (Note)obj;
        	int pitch = note.getMidiNumber();
        	int staffIndex = gScore.getStaffIndex(gStaff);
        	Score score = gScore.getScore();
        	Clef clef = score.getPreviousObjectOfType(staffIndex, note.getTick(), Clef.class);
        	int line = StaffMapper.midiToStaffPosition(pitch, clef);
        	int y = gStaff.getYPos(line);
            g = new GraphicalNote((Note) obj);
            g.setY(y);
            
        }
        else if (obj instanceof Rest) {
        	Rest rest = (Rest)obj;
            g = new GraphicalRest(rest);
            if (rest.getDuration() == 0)
            g.setY(gStaff.getYPosOfLine(3));
            else
            	g.setY(gStaff.getYPosOfLine(2));
        }
        else if (obj instanceof Clef) {
        	Clef clef = (Clef)obj;
            g = new GraphicalClef(clef);
            int line = clef.getPosInStaff();
            g.setY(gStaff.getYPosOfLine(line));
        }
        else if (obj instanceof KeySignature) {
            g = new GraphicalKeySignature((KeySignature) obj);
        }
        else if (obj instanceof TimeSignature) {
            g = new GraphicalTimeSignature((TimeSignature) obj);
        }
        else if (obj instanceof CurvedConnection) {
        	g = new GraphicalCurvedConnection(gScore, (CurvedConnection)obj);
        }
        else {
            throw new IllegalArgumentException(
                "Unsupported MusicObject: " + obj.getClass()
            );
        }

     // CurvedConnection (Slur/Tie) calcola le proprie coordinate in base alle note collegate,
     // quindi non bisogna passare x e y; per tutti gli altri oggetti grafici
     // (Note, Rest, Bar, TimeSignature, ecc.) usiamo le coordinate cliccate dall’utente.
        if (obj instanceof CurvedConnection) {
        	g.init(gScore, gStaff);
        } else {
        g.init(gScore, gStaff, x, g.getY());
        }
        return g;
    }
}
