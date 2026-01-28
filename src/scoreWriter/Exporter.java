package scoreWriter;

import java.util.List;

import Measure.Bar;
import Measure.TimeSignature;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import musicLily.LilyBar;
import musicLily.LilyNote;
import musicLily.LilyRest;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.ParsedStaff;
import notation.Score;
import notation.ScoreParser;
import notation.Slur;
import notation.Tie;

public class Exporter {

	private final StringBuilder sb = new StringBuilder();
	private Clef currentClef;
	private String[] numbers = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
			"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen",
			"Twenty" };
	private Score score;
	private KeySignature ks;

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
				List<MusicObject> voiceObjs = staff.voices.get(voiceIndex);

				// Se la voce non contiene né note né pause → non esportare
				if (!hasNotesOrRests(voiceObjs)) {
					continue;
				}

				parseVoice(staffIndex, voiceIndex, voiceObjs);

				// Nel loop stiamo iterando sui parsedStaves, dove ogni ParsedStaff combina
				// le informazioni “staff-wide” (voce 0) con le note delle voci successive.
				// Di conseguenza, la prima voce musicale reale diventa voiceIndex 0 in questo
				// contesto,
				// mentre le lyrics originali sono memorizzate a partire da voice 1.
				// Per recuperarle correttamente, dobbiamo quindi usare voiceIndex + 1.
				for (int j = 0; j < score.getStanzasNumber(staffIndex, voiceIndex + 1); j++) {
					List<String> l = score.getLyricsFor(staffIndex, voiceIndex + 1, j);
					exportLyrics(l, staffIndex, voiceIndex, j);
				}
				sb.append("\n");
				currentClef = null;
			}
		}

		createScoreBlock(parsedStaves);
	}

	/** Inizio della voce in LilyPond */
	private void parseVoice(int currentStaff, int currentVoice, List<MusicObject> voiceObjs) {
		ks = null;
		currentClef = null;

		sb.append(getNumber(currentStaff) + getNumber(currentVoice) + " = ");
		sb.append("\\relative {\n");
		parseObjects(voiceObjs);
		sb.append("}\n");
	}

	/** Analizza tutti gli oggetti della voce */
	private void parseObjects(List<MusicObject> objs) {

		for (MusicObject go : objs) {

			if (go instanceof Clef) {
				parseClef((Clef) go);
			} else if (go instanceof Note) {
				parseNote((Note) go);
			} else if (go instanceof Rest) {
				parseRest((Rest) go);
			} else if (go instanceof Bar) {
				parseBar((Bar) go);
			} else if (go instanceof TimeSignature) {
				parseTimeSignature((TimeSignature) go);
			} else if (go instanceof KeySignature) {
				parseKeySignature((KeySignature) go);
			}
		}
	}

	private void parseKeySignature(KeySignature ks) {

		this.ks = ks;

		String[] majorKeysSharp = { "c", "g", "d", "a", "e", "h", "fis", "cis" };
		String[] minorKeysSharp = { "a", "e", "h", "fis", "cis", "gis", "dis", "ais" };

		String[] majorKeysFlat = { "c", "f", "b", "es", "as", "des", "ges", "ces" };
		String[] minorKeysFlat = { "a", "d", "g", "c", "f", "bes", "ees", "aes" };

		int n = ks.getNumberOfAlterations();
		int type = ks.getTypeOfAlterations(); // 1 = #, -1 = b

		String key;

		if (n == 0) {
			key = (ks.getModus() == musicEvent.Modus.MAJOR_SCALE) ? "c" : "a";
		} else if (type == 1) { // diesis
			key = (ks.getModus() == musicEvent.Modus.MAJOR_SCALE) ? majorKeysSharp[n] : minorKeysSharp[n];
		} else { // bemolle
			key = (ks.getModus() == musicEvent.Modus.MAJOR_SCALE) ? majorKeysFlat[n] : minorKeysFlat[n];
		}

		String Modus;

		switch (ks.getModus()) {
		case MAJOR_SCALE:
			Modus = "\\major";
			break;
		case MINOR_SCALE:
			Modus = "\\minor";
			break;
		// case DORIAN: Modus = "\\dorian"; break;
		// case PHRYGIAN: Modus = "\\phrygian"; break;
		// case LYDIAN: Modus = "\\lydian"; break;
		// case MIXOLYDIAN: Modus = "\\mixolydian"; break;
		// case LOCRIAN: Modus = "\\locrian"; break;
		default:
			Modus = "\\major";
		}

		sb.append("\\key ").append(key).append(" ").append(Modus).append("\n");
	}

	private void parseTimeSignature(TimeSignature ts) {
		int n = ts.getNumerator();
		int d = ts.getDenominator();
		sb.append("\\time ");
		sb.append(n + "/" + d);
		sb.append("\n");

	}

	/** Esporta una clef LilyPond */
	private void parseClef(Clef clef) {
		this.currentClef = clef;

		switch (clef.getType()) {
		case TREBLE -> sb.append("\\clef treble");
		case TREBLE_8 -> sb.append("\\clef \"treble_8\"");
		case BASS -> sb.append("\\clef bass");
		default -> throw new IllegalArgumentException("Unexpected value: " + clef.getType());
		}
		sb.append("\n");
	}

	/** Esporta una nota */
	private void parseNote(Note note) {

		boolean success = MidiCalculator.setMidiNumberAndAlteration(note, currentClef, ks);
		if (!success) {
			return; // chiave mancante
		}

		LilyNote ln = new LilyNote(note);
		sb.append(ln.draw()).append(" ");

		for (CurvedConnection c : score.getCurveList()) {
			if (note == c.getStart() && c instanceof Tie) {
				sb.append("~");
			}
			if (note == c.getStart() && c instanceof Slur) {
				sb.append("(");
			}
			if (note == c.getEnd() && c instanceof Slur) {
				sb.append(")");
			}
			}
	}

	private void parseRest(Rest rest) {
		LilyRest lr = new LilyRest(rest);
		sb.append(lr.draw() + " ");
	}

	/** Esporta una barra di misura */
	private void parseBar(Bar bar) {
		LilyBar lb = new LilyBar(bar.getBar());
		sb.append(lb.draw());
		sb.append("\n");
	}

	/** Controlla se una voce contiene almeno una nota o pausa */
	private boolean hasNotesOrRests(List<MusicObject> voice) {
		for (MusicObject obj : voice) {
			if (obj instanceof Note || obj instanceof Rest) {
				return true;
			}
		}
		return false;
	}

	private void exportLyrics(List<String> list, int staffIndex, int voiceIndex, int stanza) {
		if (list == null || list.isEmpty()) {
			return;
		}

		// Nome della variabile LilyPond basato su staff/voice/stanza
		String name = "Lyric" + getNumber(staffIndex) + getNumber(voiceIndex) + getNumber(stanza);

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
				sb.append(getVoiceNumber(voiceIndex));
				sb.append(" ");
				sb.append(getStaffName(staffIndex, voiceIndex));
				sb.append(" }");
				sb.append(" >>\n");
				appendLyricsToStaff(staffIndex, voiceIndex);
			}

			sb.append("  >>\n");
			// qui vengono inserite le lyrics se presenti
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

	private String getVoiceNumber(int voiceIndex) {
		switch (voiceIndex) {
			case 0: return "\\voiceOne";
			case 1: return "\\voiceTwo";
		}
		return "";
	}

	private void appendLyricsToStaff(int staffIndex, int voiceIndex) {

	    int stanzas = score.getStanzasNumber(staffIndex, voiceIndex + 1);
	    if (stanzas == 0) {
			return;
		}

	    String voiceName =
	        "voice" + getNumber(staffIndex) + getNumber(voiceIndex);

	    for (int stanza = 0; stanza < stanzas; stanza++) {

	        String lyricVar =
	            "Lyric" + getNumber(staffIndex)
	                   + getNumber(voiceIndex)
	                   + getNumber(stanza);

	        sb.append("    \\new Lyrics \\lyricsto \"")
	          .append(voiceName)
	          .append("\" { \\")
	          .append(lyricVar)
	          .append(" }\n");
	    }
	}

	private String getNumber(int n) {
		return numbers[n];
	}

	public void printScore() {
		System.out.println(sb.toString());
	}
}
