package services;

import java.util.List;

import graphical.GraphicalClef;
import musicEvent.NoteEvent;
import notation.Clef;
import notation.KeySignature;
import notation.Score;
import scoreWriter.MidiPitch;
import scoreWriter.StaffMapper;

public class ClefChangeService {

	private final Score score;

    public ClefChangeService(Score score) {
        this.score = score;
    }
    
	public void commitClefChange(GraphicalClef gClef) {
		Clef clef = gClef.getModelObject();
		clef.setTick(gClef.getX());
		List<NoteEvent> list = score.getNotesAffectedByClef(clef);
		recalculateMidi(list, clef);
	}
	
	private void recalculateMidi(List<NoteEvent> list, Clef clef) {
		for (NoteEvent n : list) {
			int staffIndex = clef.getStaffIndex();
			KeySignature ks = score.getKeySignature(staffIndex, n.getTick());
			MidiPitch midi = StaffMapper.staffPositionToMidi(n.getStaffPosition(), clef, ks);
			n.setMidiNumber(midi.getMidiNumber());
			n.setAlteration(midi.getAlteration());
		}

	}
	
}
