package graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;

import musicEvent.NoteEvent;
import musicInterface.MusicObject;
import notation.CurvedConnection;

public class GraphicalCurvedConnection extends GraphicalObject {

    protected GraphicalNote startNote, endNote;
    protected CurvedConnection model;

    private int x1, y1;

    public GraphicalCurvedConnection(GraphicalScore gScore, CurvedConnection model) {
        this.model = model;
        NoteEvent n1 = model.getStart();
        NoteEvent n2 = model.getEnd();
        startNote = (GraphicalNote) gScore.getObject(n1);
        endNote = (GraphicalNote) gScore.getObject(n2);
        setX(startNote.getX());
        setY(startNote.getY());
        x1 = endNote.getX();
        y1 = endNote.getY();
    }

    public void updateFromNotes() {
        if (startNote == null || endNote == null) {
			return;
		}

        setX(startNote.getX());
        setY(startNote.getY());
        setX1(endNote.getX());
        setY1(endNote.getY());
    }

    public void setX1Y1(int x, int y) { x1 = x; y1 = y; }
    public int getX1() { return x1; }
    public void setX1(int x1) { this.x1 = x1; }
    public int getY1() { return y1; }
    public void setY1(int y1) { this.y1 = y1; }

    @Override
    public GraphicalObject cloneObject() {
        return null; // TODO implement if necessary
    }

    // -----------------------------
    //            DRAW
    // -----------------------------
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        double dx = x1 - getX();
        int x = getX();
        int y = getY();
        double height = Math.abs(dx) * 0.25;

        double c1x = x + dx * 0.25;
        double c2x = x + dx * 0.75;

        int voiceIndex = startNote.getModelObject().getVoiceIndex();
        double direction = voiceIndex == 1 ? -1 : +1;
        double c1y = y  + direction * height;
        double c2y = y1 + direction * height;

        CubicCurve2D curve = new CubicCurve2D.Double();
        curve.setCurve(x, y, c1x, c1y, c2x, c2y, x1, y1);

        setBounds(curve.getBounds());

        Stroke old = g2.getStroke();
        g2.setStroke(new BasicStroke(1.4f));

        g2.setColor(isSelected() ? Color.RED : Color.BLACK);
        g2.draw(curve);

        g2.setStroke(old);
    }

    public boolean hasNote(GraphicalNote n) {
    	return (startNote == n || endNote == n);
    }

    public MusicalSymbol getSymbol() {
        return null;
    }

    @Override
	public MusicObject getModelObject() {
		return model;
	}

	@Override
	protected MusicalSymbol setSymbol() {
		return null;
	}
}
