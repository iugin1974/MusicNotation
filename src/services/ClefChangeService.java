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
		int staffIndex = clef.getStaffIndex();
		int tick1 = clef.getTick();
		Clef nextClef = score.getNextObjectOfType(staffIndex, tick1, Clef.class);
		int tick2 = nextClef.getTick();
		List<NoteEvent> list = score.getAllNotesBetween(staffIndex, tick1, tick2);
		recalculateMidi(list, clef);
	}
	
	private void recalculateMidi(List<NoteEvent> list, Clef clef) {
		for (NoteEvent n : list) {
			int staffIndex = clef.getStaffIndex();
			KeySignature ks = score.getLastObjectOfType(staffIndex, n.getTick(), KeySignature.class);
			MidiPitch midi = StaffMapper.staffPositionToMidi(n.getStaffPosition(), clef, ks);
			n.setMidiNumber(midi.getMidiNumber());
			n.setAlteration(midi.getAlteration());
		}

	}
	
}
