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
import Measure.TimeSignature;
import musicEvent.Note;
import musicEvent.Rest;
import notation.Clef;
import notation.KeySignature;
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
	private Map<MusicObject, GraphicalObject> objects = new HashMap<>();
	protected final LedgerLinesRenderer ledgerRenderer = new LedgerLinesRenderer();

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private final int TOP_MARGIN = 50;
	private final int LINE_NUMBER = 5;
	private final int DISTANCE_BETWEEN_LINES = 10;
	private int width;
	private StaffActionListener staffActionListener;

	public GraphicalScore(Score score, StaffActionListener staffActionListener) {
		this.score = score;
		this.staffActionListener = staffActionListener;
	}

	public GraphicalObject hitTest(int x, int y) {

	    // 1️⃣ oggetti grafici (note, pause, ecc.) – dal più in alto al più in basso
	    List<GraphicalObject> objs =
	            new ArrayList<>(objects.values());

	    for (int i = objs.size() - 1; i >= 0; i--) {
	        GraphicalObject obj = objs.get(i);
	        GraphicalObject hit = obj.hitTest(x, y);
	        if (hit != null)
	            return hit;
	    }

	    // 2️⃣ pentagrammi
	    for (GraphicalStaff staff : staves) {
	        GraphicalObject hit = staff.hitTest(x, y);
	        if (hit != null)
	            return hit;
	    }

	    return null;
	}


	public boolean removeObject(GraphicalObject go) {
	    if (go == null)
	        return false;

	    MusicObject mo = go.getModelObject();
	    if (mo == null)
	        return false;

	    GraphicalObject removed = objects.remove(mo);
	    if (removed != null) {
	        return true;
	    }
	    return false;
	}

	
	public boolean removeObject(MusicObject mo) {
	    if (mo == null)
	        return false;

	    GraphicalObject removed = objects.remove(mo);
	    if (removed != null) {
	        return true;
	    }
	    return false;
	}

	
	public void setWidth(int w) {
		width = w;
	}

	public void createGraphicalStaff(int id, ScoreEvent e, int width) {
		int yPos = calculateNextY();
		Staff staff = e.getStaff();
		GraphicalStaff s = new GraphicalStaff(staff, id, 0, yPos, width, LINE_NUMBER, DISTANCE_BETWEEN_LINES);
		s.setActionListener(staffActionListener);
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
	    for (GraphicalObject obj : objects.values()) {
	        obj.draw(g);
	    }
	}

	public boolean hasStaves() {
		return score.getAllStaves().size() > 0;
	}

	public List<GraphicalStaff> getStaves() {
		return staves;
	}

	public int getStaffCount() {
		return staves.size();
	}
	
	public GraphicalStaff getStaff(int i) {
		return staves.get(i);
	}
	
	public GraphicalStaff getStaffAtPos(int x, int y) {
		for (GraphicalStaff s : staves) {
			if (s.contains(x, y)) return s;
		}
		return null;
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
		GraphicalStaff s;
		switch (e.getType()) {

		case Type.NOTE_ADDED:
			gObj = new GraphicalNote((Note) e.getMusicObject(), this);
			gObj.setXY(x, y);
			break;

		case Type.REST_ADDED:
			gObj = new GraphicalRest((Rest) e.getMusicObject());
			break;

		case Type.BARLINE_ADDED:
			gObj = new GraphicalBar((Bar) e.getMusicObject());
			gObj.setXY(x, y);
			break;
			
		case Type.CLEF_ADDED:
			gObj = new GraphicalClef((Clef) e.getMusicObject());
			gObj.setXY(x, y);
			break;
			
		case Type.KEY_SIGNATURE_ADDED:
			s = getStaffAtPos(x, y);
			if (s == null) return null;
			gObj = new GraphicalKeySignature(x, s, (KeySignature) e.getMusicObject());
			break;
			
		case Type.TIME_SIGNATURE_ADDED:
			s = getStaffAtPos(x, y);
			if (s == null) return null;
			gObj = new GraphicalTimeSignature((TimeSignature) e.getMusicObject(), s, x);
			break;
			
		default:
			break;
		}

		objects.put(e.getMusicObject(), gObj);
		return gObj;
	}
	
	public GraphicalObject getObject(MusicObject o) {
		return objects.get(o);
	}

}
