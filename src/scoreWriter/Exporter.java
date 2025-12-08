package scoreWriter;

import java.util.List;
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
        sb.append("\\relative c' {\n");
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
        }
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

        go.setMidiNumber(midi);

        LilyNote ln = new LilyNote(go);
        sb.append(ln.draw()).append(" ");

        if (go.isSlurStart()) sb.append("(");
        if (go.isSlurEnd()) sb.append(")");
        if (go.isTiedStart()) sb.append("~");
    }

    /** Esporta una barra di misura */
    private void parseBar(GraphicalBar b) {

        if (b.getSymbol().equals(SymbolRegistry.SINGLE_BARLINE)) {
            sb.append(" |\n");
        }
        else if (b.getSymbol().equals(SymbolRegistry.DOUBLE_BARLINE)) {
            sb.append(" \\bar \"||\"\n");
        }
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
