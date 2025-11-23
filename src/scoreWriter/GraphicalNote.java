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

public class GraphicalNote extends Note implements GraphicalObject {

	private MusicalSymbol symbol;


	public GraphicalNote(MusicalSymbol symbol) {
		super(0); // crea una nota con midi 0
		this.symbol = symbol;
		setup();
	}

	public static final int STEM_UP = 1;
	public static final int STEM_DOWN = -1;
	public static final int ONE_VOICE = 0;
	public static final int VOICE_ONE = 1;
	public static final int VOICE_TWO = 2;
	private int voice = ONE_VOICE;
	
	private int stemDirection = STEM_UP;
	private Rectangle bounds;
	private final GraphicalHelper helper = new GraphicalHelper();


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



	public void setMidiNumber(int midi) {
		this.midiNumber = midi;
	}
	
	public void setStemDirection(int direction) {
		stemDirection = direction;
	}

	@Override
	public void draw(Graphics g) {		
		String glyph = symbol.getGlyphUp();
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(glyph);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int height = ascent + descent;

        Rectangle bounds = new Rectangle(helper.getX(), helper.getY() - ascent, width, height);
        helper.setBounds(bounds);
        if (helper.isSelected()) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
        g.drawString(glyph, helper.getX(), helper.getY());
	}

	@Override
	public void setXY(int x, int y) {
		helper.setXY(x, y);
	}

	@Override
	public int getX() {
		return helper.getX();
	}

	@Override
	public void setX(int x) {
		helper.setX(x);;
	}

	@Override
	public int getY() {
		return helper.getY();
	}

	@Override
	public void setY(int y) {
		helper.setY(y);
	}

	@Override
	public boolean isSelected() {
		return helper.isSelected();
	}

	@Override
	public void select(boolean selected) {
		helper.select(selected);
	}

	@Override
	public boolean contains(int x, int y) {
        return helper.contains(x, y);
    }

	@Override
	public void moveTo(int x, int y) {
		helper.moveTo(x, y);
		
	}

	@Override
	public void moveBy(int dx, int dy) {
		 helper.moveBy(dx, dy);
	}

	@Override
	public GraphicalObject cloneObject() {
		GraphicalNote n = new GraphicalNote(symbol);
		n.setMidiNumber(getMidiNumber());
		n.setX(getX());
		n.setY(getY());
		n.setDuration(getDuration());
		n.setDots(getDots());
		setBounds(getBounds());
		return n;
	}



	@Override
	public void setBounds(Rectangle bounds) {
	helper.setBounds(bounds);
	}

	@Override
	public Rectangle getBounds() {
		return helper.getBounds();
	}



	@Override
	public MusicalSymbol getSymbol() {
		return symbol;
	}
}
