package services;

import graphical.GraphicalNote;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import musicEvent.Note;
import notation.Clef;
import notation.KeySignature;
import notation.Score;
import scoreWriter.MidiPitch;
import scoreWriter.StaffMapper;

public class NotePitchService {

	private final Score score;
	private final GraphicalScore graphicalScore;

	public NotePitchService(Score score, GraphicalScore graphicalScore) {
		this.score = score;
		this.graphicalScore = graphicalScore;
	}
	
	public void commitNotePitch(GraphicalNote gNote) {
		Note note = gNote.getModelObject();

		GraphicalStaff s = graphicalScore.getStaffAtPos(gNote.getX(), gNote.getY());
		if (s == null) {
			return;
		}

		int staffPosition = s.getPosInStaff(gNote);
		note.setStaffPosition(staffPosition);

		int staffIndex = graphicalScore.getStaffIndex(s);
		int tick = note.getTick();

		Clef clef = score.getLastClef(staffIndex, tick);
		KeySignature ks = score.getKeySignature(staffIndex, tick);

		MidiPitch midi = StaffMapper.staffPositionToMidi(gNote.getStaffPosition(), clef, ks);

		note.setMidiNumber(midi.getMidiNumber());
	}
}
