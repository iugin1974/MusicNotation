package graphical;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import musicInterface.MusicObject;
import javax.swing.JPanel;
import javax.swing.plaf.synth.SynthPopupMenuUI;

import Measure.Bar;
import musicEvent.Note;
import musicEvent.Rest;
import notation.Score;
import notation.ScoreEvent;
import notation.ScoreEvent.Type;
import notation.ScoreListener;
import notation.Staff;
import scoreWriter.ScoreWriter;

public class GraphicalScore {

	private Score score;
	private ScoreWriter controller;
	private List<GraphicalStaff> staves = new ArrayList<>();
	private List<GraphicalObject> objects = new ArrayList<>();

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private final int TOP_MARGIN = 50;
	private final int LINE_NUMBER = 5;
	private final int DISTANCE_BETWEEN_LINES = 10;
	private int width;

	public GraphicalScore(Score score) {
		this.score = score;
	}

	public GraphicalObject hitTest(int x, int y) {
		for (GraphicalStaff staff : staves) {
			GraphicalObject hit = staff.hitTest(x, y);
			if (hit != null)
				return hit;
		}
		return null; // niente colpito
	}

	public void setWidth(int w) {
		width = w;
	}

	public void createGraphicalStaff(int id, ScoreEvent e, int width) {
		int yPos = calculateNextY();
		Staff staff = (Staff) e.getSource();
		GraphicalStaff s = new GraphicalStaff(staff, id, 0, yPos, width, LINE_NUMBER, DISTANCE_BETWEEN_LINES);
		staves.add(s);
	}

	private int calculateNextY() {
		int yPos = TOP_MARGIN;
		for (GraphicalStaff s : staves) {
			yPos += s.getHeight() + DISTANCE_BETWEEN_STAVES;
		}
		return yPos;
	}

	public void draw(Graphics g) {
		for (GraphicalStaff s : staves) {
			s.draw(g);
		}
		for (GraphicalObject obj : objects) {
			obj.draw(g);
		}
	}

	public boolean hasStaves() {
		return score.getAllStaves().size() > 0;
	}

	public List<GraphicalStaff> getStaves() {
		return staves;
	}

	public int getStaffIndex(GraphicalStaff s) {
		for (int i = 0; i < staves.size(); i++) {
			if (staves.get(i) == s)
				return i;
		}
		return -1;
	}

	public GraphicalObject createGraphicalObject(ScoreEvent e, int x, int y) {

		GraphicalObject gObj = null;

		switch (e.getType()) {

		case Type.NOTE_ADDED:
			gObj = new GraphicalNote((Note) e.getSource());
			gObj.setXY(x, y);
			break;

		case Type.REST_ADDED:
			gObj = new GraphicalRest((Rest) e.getSource());
			break;

		case Type.BARLINE_ADDED:
			gObj = new GraphicalBar((Bar) e.getSource());
			gObj.setXY(x, y);
			break;
		default:
			break;
		}

		objects.add(gObj);
		return gObj;
	}

}
