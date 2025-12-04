package scoreWriter;

import java.awt.Graphics;

public class LedgerLinesRenderer {

	public void drawLedgerLines(Graphics g, Pointer pointer, GraphicalStaff staff) {
		drawLedgerLines(g, pointer.getX(), pointer.getY(), staff);
	}
    public void drawLedgerLines(Graphics g, GraphicalNote note, GraphicalStaff staff) {
    	drawLedgerLines(g, note.getX(), note.getY(), staff);
    }
    
    public void drawLedgerLines(Graphics g, int x, int y, GraphicalStaff staff) {
    	if (staff == null) return;
        int top = staff.getLineY(5);     // es: posizione verticale della linea superiore
        int bottom = staff.getLineY(1);

        int spacing = staff.getDistance(); // distanza tra linee

        // sopra il pentagramma
        for (; top >= y; top -= spacing) {
            g.drawLine(x - 6, top, x + 16, top);
        }

        // sotto il pentagramma
        for (; bottom <= y; bottom += spacing) {
            g.drawLine(x - 6, bottom, x + 16, bottom);
        }
    }
}
