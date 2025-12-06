package scoreWriter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;

public abstract class CurvedConnection implements GraphicalObject {

    private final GraphicalHelper helper = new GraphicalHelper();

    protected GraphicalNote startNote;
    protected GraphicalNote endNote;

    private int x1, y1;
    boolean slurAbove = true;

    public abstract void assignToNotes(GraphicalNote startNote, GraphicalNote endNote);

    // -----------------------------
    //     GETTER PER LE NOTE
    // -----------------------------
    public GraphicalNote getStartNote() {
        return startNote;
    }

    public GraphicalNote getEndNote() {
        return endNote;
    }

    public void removeFromNotes() {
        if (this instanceof Slur) {
            if (startNote != null) {
                startNote.setSlur(null);
                startNote.slurNone();
            }
            if (endNote != null) {
                endNote.setSlur(null);
                endNote.slurNone();
            }
        } else if (this instanceof Tie) {
            if (startNote != null) {
                startNote.setTie(null);
                startNote.tieNone();
            }
            if (endNote != null) {
                endNote.setTie(null);
                endNote.tieNone();
            }
        }
    }

    // -----------------------------
    //       COORDINATE
    // -----------------------------
    @Override
    public void setXY(int x, int y) { helper.setXY(x, y); }

    @Override
    public int getX() { return helper.getX(); }

    @Override
    public void setX(int x) { helper.setX(x); }

    @Override
    public int getY() { return helper.getY(); }

    @Override
    public void setY(int y) { helper.setY(y); }

    public int getX1() { return x1; }

    public void setX1(int x1) { this.x1 = x1; }

    public int getY1() { return y1; }

    public void setY1(int y1) { this.y1 = y1; }

    // -----------------------------
    //   SELEZIONE E MOVIMENTO
    // -----------------------------
    @Override
    public boolean isSelected() { return helper.isSelected(); }

    @Override
    public void select(boolean selected) { helper.select(selected); }

    @Override
    public boolean contains(int x, int y) { return helper.contains(x, y); }

    @Override
    public void moveTo(int x, int y) { helper.moveTo(x, y); }

    @Override
    public void moveBy(int dx, int dy) { helper.moveBy(dx, dy); }

    @Override
    public void setBounds(Rectangle bounds) { helper.setBounds(bounds); }

    @Override
    public Rectangle getBounds() { return helper.getBounds(); }

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

        double dx = x1 - helper.getX();
        int x = helper.getX();
        int y = helper.getY();
        double height = Math.abs(dx) * 0.25;

        double c1x = x + dx * 0.25;
        double c2x = x + dx * 0.75;

        double direction = slurAbove ? -1 : +1;
        double c1y = y  + direction * height;
        double c2y = y1 + direction * height;

        CubicCurve2D curve = new CubicCurve2D.Double();
        curve.setCurve(x, y, c1x, c1y, c2x, c2y, x1, y1);

        setBounds(curve.getBounds());

        Stroke old = g2.getStroke();
        g2.setStroke(new BasicStroke(1.4f));

        g2.setColor(helper.isSelected() ? Color.RED : Color.BLACK);
        g2.draw(curve);

        g2.setStroke(old);
    }

    @Override
    public MusicalSymbol getSymbol() {
        return null;
    }
}
