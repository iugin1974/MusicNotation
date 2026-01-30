package services;

import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import musicEvent.Note;
import musicEvent.NoteEvent;
import musicInterface.MusicObject;
import notation.Clef;
import notation.Score;
import notation.Tie;

public class ObjectMoveService {

	private final Score score;
	private final NotePitchService notePitchService;
	private final ClefChangeService clefChangeService;

	public ObjectMoveService(Score score, NotePitchService notePitchService, ClefChangeService clefChangeService) {
		this.score = score;
		this.notePitchService = notePitchService;
		this.clefChangeService = clefChangeService;
	}

	public void commitMove(GraphicalObject obj) {
		MusicObject mo = obj.getModelObject();
		if (mo == null)
			return;

		score.changeTick(mo, obj.getX());

		if (mo instanceof Note note && obj instanceof GraphicalNote gNote) {
			notePitchService.commitNotePitch(gNote);
			for (NoteEvent n : score.getConnectionGroup(note, Tie.class)) {
				n.setMidiNumber(note.getMidiNumber());
			}
		} else if (mo instanceof Clef && obj instanceof GraphicalClef gClef) {
			clefChangeService.commitClefChange(gClef);
		}
	}
}
