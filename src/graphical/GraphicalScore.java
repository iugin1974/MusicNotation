package graphical;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Measure.TimeSignature;
import musicInterface.MusicObject;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Score;
import notation.ScoreEvent;
import notation.Staff;

public class GraphicalScore {

	private Score score;
	private List<GraphicalStaff> staves = new ArrayList<>();
	private Map<MusicObject, GraphicalObject> objects = new HashMap<>();
//	private List<GraphicalCurvedConnection> listCurvedConnections = new ArrayList<>();
	protected final LedgerLinesRenderer ledgerRenderer = new LedgerLinesRenderer();

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private final int TOP_MARGIN = 50;
	private final int LINE_NUMBER = 5;
	private final int DISTANCE_BETWEEN_LINES = 10;
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
	        if (hit != null) {
				return hit;
			}
	    }

	    // 2️⃣ pentagrammi
	    for (GraphicalStaff staff : staves) {
	        GraphicalObject hit = staff.hitTest(x, y);
	        if (hit != null) {
				return hit;
			}
	    }

	    return null;
	}


	public boolean removeObject(GraphicalObject go) {
	    if (go == null) {
			return false;
		}

	    MusicObject mo = go.getModelObject();
	    if (mo == null) {
			return false;
		}

	    GraphicalObject removed = objects.remove(mo);
	    if (removed != null) {
	        return true;
	    }
	    return false;
	}


	public boolean removeObject(MusicObject mo) {
	    if (mo == null) {
			return false;
		}

	    GraphicalObject removed = objects.remove(mo);
	    if (removed != null) {
	        return true;
	    }
	    return false;
	}

	public void createGraphicalStaff(ScoreEvent e, int width) {
		int yPos = calculateNextY();
		Staff staff = e.getStaff();
		GraphicalStaff s = new GraphicalStaff(staff, 0, yPos, width, LINE_NUMBER, DISTANCE_BETWEEN_LINES);
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
//		for (GraphicalCurvedConnection gc : listCurvedConnections) {
//			gc.draw(g);
//		}
	    for (GraphicalObject obj : objects.values()) {
	        obj.draw(g);
	    }

	    GraphicalLyrics l = new GraphicalLyrics(objects, this);
	    l.draw(g);
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
			if (s.contains(x, y)) {
				return s;
			}
		}
		return null;
	}

	public int getStaffIndex(GraphicalStaff s) {
		for (int i = 0; i < staves.size(); i++) {
			if (staves.get(i) == s) {
				return i;
			}
		}
		return -1;
	}

	public GraphicalObject createGraphicalObject(ScoreEvent e) {
		  MusicObject obj = e.getMusicObject();
		    if (obj == null) {
				return null;
			}
		    int x = obj.getTick();
		    int staffIndex = e.getStaffIndex();
		    
		    // Determina lo staff, se necessario
		    GraphicalStaff s = getStaff(staffIndex);
		        if (s == null) {
					return null; // non c'è staff valido, non creo
				}

		    // Creazione tramite factory centralizzata
		    GraphicalObject gObj = GraphicalObjectFactory.create(obj, this, s, x);

		    // Salvo nella mappa per aggiornamenti futuri
		    objects.put(obj, gObj);

		    return gObj;
	}

	/**
	 * Passato un MusicObject ritorna il GraphicalObject corrispondente
	 * @param o
	 * @return
	 */
	public GraphicalObject getGraphicalObject(MusicObject o) {
		return objects.get(o);
	}

	public GraphicalCurvedConnection getGraphicalObject(CurvedConnection c) {
		return (GraphicalCurvedConnection) objects.get(c);
	}

	public int getDistanceBetweenStaves() {
	    return DISTANCE_BETWEEN_STAVES;
	}

	public int getTopMargin() {
	    return TOP_MARGIN;
	}

	public int getLineNumber() {
	    return LINE_NUMBER;
	}

	public int getDistanceBetweenLines() {
	    return DISTANCE_BETWEEN_LINES;
	}

	public Score getScore() {
		return score;
	}
}
