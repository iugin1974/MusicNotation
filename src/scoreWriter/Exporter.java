package scoreWriter;

import java.util.List;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalBar;
import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalTimeSignature;
import musicEvent.Note;
import musicLily.LilyBar;
import musicLily.LilyNote;

public class Exporter {

    private final StringBuilder sb = new StringBuilder();
    private GraphicalClef clef;

    /** Esporta tutti gli staves e voci in LilyPond */
    public void export(List<ParsedStaff> parsed) {

        for (ParsedStaff staff : parsed) {

            for (List<GraphicalObject> voiceObjs : staff.voices) {

                // Se la voce non contiene né note né pause → non esportare
                if (!hasNotesOrRests(voiceObjs)) 
                    continue;

                createVoiceHeader(staff, voiceObjs);
                parseObjects(voiceObjs, staff);
                exportLyrics(voiceObjs);

                sb.append("\n}\n\n");
                clef = null;
            }
        }
    }

    /** Inizio della voce in LilyPond */
    private void createVoiceHeader(ParsedStaff staff, List<GraphicalObject> voiceObjs) {
        sb.append("\\relative {\n");
    }

    /** Analizza tutti gli oggetti della voce */
    private void parseObjects(List<GraphicalObject> objs, ParsedStaff staff) {

        for (GraphicalObject go : objs) {

            if (go instanceof GraphicalClef) {
                parseClef((GraphicalClef) go);
            }
            else if (go instanceof GraphicalNote) {
                parseNote((GraphicalNote) go);
            }
            else if (go instanceof GraphicalBar) {
                parseBar((GraphicalBar) go);
            }
            else if (go instanceof GraphicalTimeSignature) {
            	parseTimeSignature((GraphicalTimeSignature) go);
            }
        }
    }

    private void parseTimeSignature(GraphicalTimeSignature go) {
		TimeSignature ts = go.getTimeSignature();
		int n = ts.getNumerator();
		int d = ts.getDenominator();
		sb.append("\\time ");
		sb.append(n + "/" + d);
		sb.append("\n");
		
	}

	/** Esporta una clef LilyPond */
    private void parseClef(GraphicalClef go) {

        clef = go;

        if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE) {
            sb.append("\\clef \"treble\"");
        }
        else if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE_8) {
            sb.append("\\clef \"treble_8\"");
        }
        else if (go.getSymbol() == SymbolRegistry.CLEF_BASS) {
            sb.append("\\clef \"bass\"");
        }

        sb.append("\n");
    }

    /** Esporta una nota */
    private void parseNote(GraphicalNote go) {

        int midi = MidiCalculator.calculateMidiNumber(go, clef);
        if (midi == -1) return; // chiave mancante
        Note n = go.getNote();
        n.setMidiNumber(midi);

        LilyNote ln = new LilyNote(n);
        sb.append(ln.draw()).append(" ");

        if (go.isSlurStart()) sb.append("(");
        if (go.isSlurEnd()) sb.append(")");
        if (go.isTiedStart()) sb.append("~");
    }

    /** Esporta una barra di misura */
    private void parseBar(GraphicalBar b) {
    Bar bar = b.getBar();
    LilyBar lb = new LilyBar(bar.getBar());
    sb.append(lb.draw());
    }

    /** Controlla se una voce contiene almeno una nota o pausa */
    private boolean hasNotesOrRests(List<GraphicalObject> voice) {
        for (GraphicalObject obj : voice) {
            if (obj instanceof GraphicalNote ||
                obj instanceof GraphicalRest) {
                return true;
            }
        }
        return false;
    }

    public void exportLyrics(List<GraphicalObject> voiceObjs) {
    	
    }
    
    public void printScore() {
        System.out.println(sb.toString());
    }
}
