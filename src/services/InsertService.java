package services;

import Measure.Bar;
import graphical.GraphicalClef;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.MusicalSymbol;
import graphical.MusicalSymbol.Type;
import musicEvent.Note;
import musicEvent.Rest;
import notation.Clef;
import notation.KeySignature;
import notation.Score;
import scoreWriter.MidiPitch;
import scoreWriter.StaffMapper;
import scoreWriter.SymbolRegistry;

public class InsertService {

	private final Score score;
	private final GraphicalScore graphicalScore;
	private final ClefChangeService clefChangeService;

	public InsertService(Score score, GraphicalScore graphicalScore, ClefChangeService clefChangeService) {
		this.score = score;
		this.graphicalScore = graphicalScore;
		this.clefChangeService = clefChangeService;
	}

	private void insertBar(MusicalSymbol objectToInsert, GraphicalStaff s, int x, boolean applyOnAllStaves) {
		if (applyOnAllStaves) {
			for (int staffIndex = 0; staffIndex < score.getStaffCount(); staffIndex++) {
				Bar bar = getBar(objectToInsert);
				bar.setTick(x);
				score.addObject(bar, staffIndex, 0);
			}
		} else {
			Bar bar = getBar(objectToInsert);
			bar.setTick(x);
			int staffIndex = graphicalScore.getStaffIndex(s);
			score.addObject(bar, staffIndex, 0);
		}
	}

	private void insertClef(MusicalSymbol clefSymbol, GraphicalStaff s, int x) {
		Clef c = createClef(clefSymbol);
		if (c == null) {
			return;
		}
		c.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		score.addObject(c, staffIndex, 0);
		GraphicalClef gClef = (GraphicalClef) graphicalScore.getGraphicalObject(c);
		clefChangeService.commitClefChange(gClef);
	}

	public void insertFromMidi(int duration, int pitch, int x, int currentVoice, GraphicalStaff s) {
		int staffIndex = graphicalScore.getStaffIndex(s);

		Clef clef = score.getLastClef(staffIndex, x);
		if (clef == null) {
			return; // oppure eccezione
		}
		KeySignature key = score.getKeySignature(staffIndex, x);

		int staffPosition = StaffMapper.midiToStaffPosition(pitch, clef);
		MidiPitch midiPitch = StaffMapper.staffPositionToMidi(staffPosition, clef, key);
		insertNote(duration, midiPitch, staffPosition, x, currentVoice, s);
	}

	private void insertFromMouse(int duration, GraphicalStaff s, int x, int y, int currentVoice) {
		int staffPosition = s.getPosInStaff(y);
		int staffIndex = graphicalScore.getStaffIndex(s);
		Clef clef = score.getLastClef(staffIndex, x);
		KeySignature key = score.getKeySignature(staffIndex, x);
		MidiPitch pitch = StaffMapper.staffPositionToMidi(staffPosition, clef, key);
		// TODO pitch può essere null. Controllo
		insertNote(duration, pitch, staffPosition, x, currentVoice, s);
	}

	private void insertNote(int duration, MidiPitch pitch, int staffPosition, int x, int currentVoice,
			GraphicalStaff s) {
		Note n = createNote(duration, pitch);
		n.setTick(x);
		System.out.println("Insert note at " + staffPosition + ". staff position. Midi = " + pitch.getMidiNumber()
				+ ", alterations: " + pitch.getAlteration());
		n.setStaffPosition(staffPosition);
		int staffIndex = graphicalScore.getStaffIndex(s);
		score.addObject(n, staffIndex, currentVoice);
	}

	public void insertObject
	(MusicalSymbol objectToInsert, GraphicalStaff s, int x, int y, int currentVoice, boolean applyOnAllStaves) {
		if (s == null) {
			return;
		}
		if (objectToInsert.getType() == Type.NOTE) {
			insertFromMouse(objectToInsert.getDuration(), s, x, y, currentVoice);
		} else if (objectToInsert.getType() == Type.REST) {
			insertRest(objectToInsert.getDuration(), s, x, y, currentVoice);
		} else if (objectToInsert.getType() == Type.BARLINE) {
			insertBar(objectToInsert, s, x, applyOnAllStaves);
		} else if (objectToInsert.getType() == Type.CLEF) {
			insertClef(objectToInsert, s, x);
		}
	}

	private void insertRest(int duration, GraphicalStaff s, int x, int y, int currentVoice) {
		Rest r = createRest(duration);
		r.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		score.addObject(r, staffIndex, currentVoice);
	}

	private Clef createClef(MusicalSymbol symbol) {
		if (symbol.equals(SymbolRegistry.CLEF_TREBLE)) {
			return Clef.treble();
		}
		if (symbol.equals(SymbolRegistry.CLEF_TREBLE_8)) {
			return Clef.treble8();
		}

		if (symbol.equals(SymbolRegistry.CLEF_BASS)) {
			return Clef.bass();
		}

		/*
		if (symbol.equals(SymbolRegistry.CLEF_ALTO))
			return Clef.ALTO;

		if (symbol.equals(SymbolRegistry.CLEF_TENOR))
			return Clef.TENOR;

		if (symbol.equals(SymbolRegistry.CLEF_SOPRANO))
			return Clef.SOPRANO;

		if (symbol.equals(SymbolRegistry.CLEF_MEZZO_SOPRANO))
			return Clef.MEZZO_SOPRANO;

		if (symbol.equals(SymbolRegistry.CLEF_BARITONE))
			return Clef.BARITONE;

		if (symbol.equals(SymbolRegistry.CLEF_PERCUSSION))
			return Clef.PERCUSSION;
*/
		return null;
	}

	private Note createNote(int duration, MidiPitch pitch) {
		Note n = new Note();
		n.setDuration(duration);
		n.setMidiNumber(pitch.getMidiNumber());
		n.setAlteration(pitch.getAlteration());
		return n;
	}

	private Rest createRest(int duration) {
		Rest r = new Rest(duration, 0);
		return r;
	}

	public Bar getBar(MusicalSymbol barSymbol) {
		Bar bar = new Bar();

		if (barSymbol.equals(SymbolRegistry.BARLINE_SINGLE)) {
			// default è già il singolo, non serve fare nulla
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_DOUBLE)) {
			bar.setDoubleBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_REPEAT_START)) {
			bar.setBeginRepeatBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_REPEAT_END)) {
			bar.setEndRepeatBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_FINAL)) {
			bar.setEndBar();
		} else {
			throw new IllegalArgumentException("Barline non supportata: " + barSymbol.getName());
		}

		return bar;
	}

}
