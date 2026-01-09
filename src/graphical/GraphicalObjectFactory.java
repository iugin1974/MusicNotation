package graphical;

import Measure.Bar;
import Measure.TimeSignature;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
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
        else {
            throw new IllegalArgumentException(
                "Unsupported MusicObject: " + obj.getClass()
            );
        }

        g.init(gScore, gStaff, x, y);
        return g;
    }
}
