package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.GraphicalObject;
import notation.Score;
import notation.Staff;

public class ScoreParser {

	 private final Score score;

	    public ScoreParser(Score score) {
	        this.score = score;
	    }
	    
	    public List<ParsedStaff> parse() {
	        LayerMixer mixer = new LayerMixer(score);
	        List<ParsedStaff> result = new ArrayList<>();

	        for (Staff staff : score.getAllStaves()) {
	            List<List<GraphicalObject>> voices = mixer.mixStaff(staff);
	            result.add(new ParsedStaff(staff, voices));
	        }

	        return result;
	    }
}
