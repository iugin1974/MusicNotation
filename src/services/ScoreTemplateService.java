package services;

import notation.Clef;
import notation.Score;
import notation.Staff;

public class ScoreTemplateService {

	private final Score score;

	public ScoreTemplateService(Score score) {
		this.score = score;
	}

	public void createPianoTemplate() {
		createStaffWithClef(Clef.treble());
		createStaffWithClef(Clef.bass());
	}

	public void createOrganTemplate() {
		createStaffWithClef(Clef.treble());
		createStaffWithClef(Clef.bass());
		createStaffWithClef(Clef.bass());
	}

	public void createChoirSATBTemplate() {
		createStaffWithClef(Clef.treble());
		createStaffWithClef(Clef.treble());
		createStaffWithClef(Clef.treble8());
		createStaffWithClef(Clef.bass());
	}

	private void createStaffWithClef(Clef clef) {
		Staff staff = score.addStaff();
		score.addObject(clef, score.getStaffIndex(staff), 0);
	}
}
