package scoreWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import musicEvent.Note;

public class GraphicalNote extends Note implements GraphicalObject, Movable {

	private int x, y;
	public static final int STEM_UP = 1;
	public static final int STEM_DOWN = -1;
	private int stemDirection = STEM_UP;
	private boolean selected = false;
	private Rectangle bounds;

	private void setup() {
		InputStream is = getClass().getResourceAsStream("/fonts/Bravura.otf");
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}

	public GraphicalNote(int midi) {
		super(midi);
		setup();
	}

	public GraphicalNote(int midi, int alteration) {
		super(midi, alteration);
		setup();
	}

	public GraphicalNote(int midi, int alteration, int duration) {
		super(midi, alteration, duration);
		setup();
	}

	public GraphicalNote(int midi, int alteration, int duration, int dots) {
		super(midi, alteration, duration, dots);
		setup();
	}

	public void setMidiNumber(int midi) {
		this.midiNumber = midi;
	}
	
	public void setStemDirection(int direction) {
		stemDirection = direction;
	}

	private String getNoteGlyph() {
		boolean up = true;
		if (stemDirection == STEM_DOWN)
			up = false;
		if (duration == 0)
			return "\uE1D2"; // semibreve
		if (duration == 1)
			return up ? "\uE1D3" : "\uE1D4"; // minima
		if (duration == 2)
			return up ? "\uE1D5" : "\uE1D6"; // semiminima
		if (duration == 3)
			return up ? "\uE1D7" : "\uE1D8"; // croma
		if (duration == 4)
			return up ? "\uE1D9" : "\uE1DA"; // semicroma
		if (duration == 5)
			return up ? "\uE1DB" : "\uE1DC"; // biscroma
		if (duration == 6)
			return up ? "\uE1DD" : "\uE1DE"; // semibiscroma
		return null; // durata non riconosciuta
	}

	@Override
	public void draw(Graphics g) {		
		String glyph = getNoteGlyph();
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(glyph);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int height = ascent + descent;

        bounds = new Rectangle(x, y - ascent, width, height);
        if (selected) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
        g.drawString(glyph, x, y);
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void select(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean contains(int x, int y) {
        return bounds != null && bounds.contains(x, y);
    }

	@Override
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
		
	}

	@Override
	public void moveBy(int dx, int dy) {
		 moveTo(this.x + dx, this.y + dy);
	}
}
