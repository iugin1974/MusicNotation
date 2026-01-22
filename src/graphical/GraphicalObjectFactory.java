package graphical;

import Measure.Bar;
import Measure.TimeSignature;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;

public final class GraphicalObjectFactory {

    private GraphicalObjectFactory() {}

    public static GraphicalObject create(
            MusicObject obj,
            GraphicalScore gScore,
            GraphicalStaff gStaff,
            int x,
            int y
    ) {
        GraphicalObject g;

        if (obj instanceof Bar) {
            g = new GraphicalBar((Bar) obj);
        }
        else if (obj instanceof Note) {
            g = new GraphicalNote((Note) obj);
        }
        else if (obj instanceof Rest) {
            g = new GraphicalRest((Rest) obj);
        }
        else if (obj instanceof Clef) {
            g = new GraphicalClef((Clef) obj);
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
        g.init(gScore, gStaff, x, y);
        }
        return g;
    }
}
