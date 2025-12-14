package scoreWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalBar;
import graphical.GraphicalClef;
import graphical.GraphicalKeySignature;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalTimeSignature;
import model.KeySignature;
import model.KeySignature.Mode;
import model.Lyric;
import musicEvent.Note;
import musicLily.LilyBar;
import musicLily.LilyNote;

public class Exporter {

	private final StringBuilder sb = new StringBuilder();
	private GraphicalClef clef;
	private String[] numbers = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
			"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen",
			"Twenty" };
	private Score score;

	/** Esporta tutti gli staves e voci in LilyPond */
	public void export(Score score) {
		this.score = score;
		ScoreParser parser = new ScoreParser(score);
		List<ParsedStaff> parsedStaves = parser.parse();
		for (ParsedStaff parsedStaff : parsedStaves) {
			if (!parsedStaff.startsWithClef()) {
				System.out.println("Manca la chiave in uno staff");
				return; // TODO -> questo va nel controller
			}
		}
				
		for (int staffIndex = 0; staffIndex < parsedStaves.size(); staffIndex++) {
			ParsedStaff staff = parsedStaves.get(staffIndex);

			for (int voiceIndex = 0; voiceIndex < staff.voices.size(); voiceIndex++) {
				List<GraphicalObject> voiceObjs = staff.voices.get(voiceIndex);

				// Se la voce non contiene né note né pause → non esportare
				if (!hasNotesOrRests(voiceObjs))
					continue;

			//	parseVoice(staffIndex, voiceIndex, voiceObjs);	
				
				// Nel loop stiamo iterando sui parsedStaves, dove ogni ParsedStaff combina
				// le informazioni “staff-wide” (voce 0) con le note delle voci successive.
				// Di conseguenza, la prima voce musicale reale diventa voiceIndex 0 in questo contesto,
				// mentre le lyrics originali sono memorizzate a partire da voice 1.
				// Per recuperarle correttamente, dobbiamo quindi usare voiceIndex + 1.
				for (int j = 0; j < score.getStanzasNumber(staffIndex, voiceIndex + 1); j++) {
				List<String> l = score.getLyricsFor(staffIndex, voiceIndex + 1, j);
				exportLyrics(l, staffIndex, 1, j + 1);
				}
				sb.append("\n");
				clef = null;
			}
		}

		//createScoreBlock(parsedStaves);
	}

	/** Inizio della voce in LilyPond */
	private void parseVoice(int currentStaff, int currentVoice, List<GraphicalObject> voiceObjs) {
		sb.append(getNumber(currentStaff) + getNumber(currentVoice) + " = ");
		sb.append("\\relative {\n");
		parseObjects(voiceObjs);
		sb.append("}\n");
	}

	/** Analizza tutti gli oggetti della voce */
	private void parseObjects(List<GraphicalObject> objs) {

		for (GraphicalObject go : objs) {

			if (go instanceof GraphicalClef) {
				parseClef((GraphicalClef) go);
			} else if (go instanceof GraphicalNote) {
				parseNote((GraphicalNote) go);
			} else if (go instanceof GraphicalBar) {
				parseBar((GraphicalBar) go);
			} else if (go instanceof GraphicalTimeSignature) {
				parseTimeSignature((GraphicalTimeSignature) go);
			} else if (go instanceof GraphicalKeySignature) {
				parseKeySignature((GraphicalKeySignature)go);
			}
		}
	}

	private void parseKeySignature(GraphicalKeySignature go) {

	    KeySignature ks = go.getKeySignature();

	    String[] majorKeysSharp = {"c", "g", "d", "a", "e", "h", "fis", "cis"};
	    String[] minorKeysSharp = {"a", "e", "h", "fis", "cis", "gis", "dis", "ais"};

	    String[] majorKeysFlat  = {"c", "f", "b", "es", "as", "des", "ges", "ces"};
	    String[] minorKeysFlat  = {"a", "d", "g", "c", "f", "bes", "ees", "aes"};

	    int n = ks.getNumberOfAlterations();
	    int type = ks.getTypeOfAlterations(); // 1 = #, -1 = b

	    String key;

	    if (n == 0) {
	        key = (ks.getMode() == Mode.MAJOR) ? "c" : "a";
	    }
	    else if (type == 1) { // diesis
	        key = (ks.getMode() == Mode.MAJOR)
	                ? majorKeysSharp[n]
	                : minorKeysSharp[n];
	    }
	    else { // bemolle
	        key = (ks.getMode() == Mode.MAJOR)
	                ? majorKeysFlat[n]
	                : minorKeysFlat[n];
	    }

	    String mode;

	    switch (ks.getMode()) {
	        case MAJOR:  mode = "\\major";  break;
	        case MINOR:  mode = "\\minor";  break;
	        case DORIAN: mode = "\\dorian"; break;
	     //   case PHRYGIAN: mode = "\\phrygian"; break;
	     //   case LYDIAN: mode = "\\lydian"; break;
	     //   case MIXOLYDIAN: mode = "\\mixolydian"; break;
	     //   case LOCRIAN: mode = "\\locrian"; break;
	        default: mode = "\\major";
	    }

	    sb.append("\\key ").append(key).append(" ").append(mode).append("\n");
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
		} else if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE_8) {
			sb.append("\\clef \"treble_8\"");
		} else if (go.getSymbol() == SymbolRegistry.CLEF_BASS) {
			sb.append("\\clef \"bass\"");
		}

		sb.append("\n");
	}

	/** Esporta una nota */
	private void parseNote(GraphicalNote go) {

		int midi = MidiCalculator.calculateMidiNumber(go, clef);
		if (midi == -1)
			return; // chiave mancante
		Note n = go.getNote();
		n.setMidiNumber(midi);

		LilyNote ln = new LilyNote(n);
		sb.append(ln.draw()).append(" ");

		if (go.isSlurStart())
			sb.append("(");
		if (go.isSlurEnd())
			sb.append(")");
		if (go.isTiedStart())
			sb.append("~");
	}

	/** Esporta una barra di misura */
	private void parseBar(GraphicalBar b) {
		Bar bar = b.getBar();
		LilyBar lb = new LilyBar(bar.getBar());
		sb.append(lb.draw());
		sb.append("\n");
	}

	/** Controlla se una voce contiene almeno una nota o pausa */
	private boolean hasNotesOrRests(List<GraphicalObject> voice) {
		for (GraphicalObject obj : voice) {
			if (obj instanceof GraphicalNote || obj instanceof GraphicalRest) {
				return true;
			}
		}
		return false;
	}

	private void exportLyrics(List<String> list, int staffIndex, int voiceIndex, int stanza) {
	    if (list == null || list.isEmpty()) return;

	    // Nome della variabile LilyPond basato su staff/voice/stanza
	    String name = "Lyric" + getNumber(staffIndex) 
	                             + getNumber(voiceIndex) 
	                             + getNumber(stanza);

	    sb.append(name).append(" = \\lyricmode {\n");

	    for (String s : list) {
	        if (s != null && !s.isEmpty()) {
	            sb.append(s).append(" ");
	        }
	    }

	    sb.append("\n}\n\n");
	}



	private void createScoreBlock(List<ParsedStaff> parsed) {

		sb.append("\\score {\n");
		sb.append("<<\n");

		for (int staffIndex = 0; staffIndex < parsed.size(); staffIndex++) {

			ParsedStaff staff = parsed.get(staffIndex);

			sb.append("  \\new Staff <<\n");

			for (int voiceIndex = 0; voiceIndex < staff.voices.size(); voiceIndex++) {
				sb.append("    << ");
				sb.append(beginVoice(staffIndex, voiceIndex));
				sb.append(" ");
				sb.append(getStaffName(staffIndex, voiceIndex));
				sb.append(" }");
				// qui verrà chiamato il tuo exporter di voice
				sb.append(" >>\n");
			}

			sb.append("  >>\n");
		}

		sb.append(">>\n");
		sb.append("}\n");
	}

	private String getStaffName(int staffIndex, int voiceIndex) {
		return "\\" + getNumber(staffIndex) + getNumber(voiceIndex);
	}

	private String beginVoice(int staffIndex, int voiceIndex) {
		return "\\new Voice = \"voice" + getNumber(staffIndex) + getNumber(voiceIndex) + "\" {";
	}

	private String getNumber(int n) {
		return numbers[n];
	}

	public void printScore() {
		System.out.println(sb.toString());
	}
}
