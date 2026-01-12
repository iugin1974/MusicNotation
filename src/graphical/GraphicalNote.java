package graphical;

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
import notation.Lyric;
import scoreWriter.StaffInfo;
import scoreWriter.SymbolRegistry;

public class GraphicalNote extends GraphicalObject implements StaffInfo {

	private MusicalSymbol symbol;
	private final Note note;
	private int staffPosition; // 0 MI, 1 FA, ecc.

	public GraphicalNote(Note n) {
		this.note = n;
		symbol = setSymbol();
	}

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

	protected MusicalSymbol setSymbol() {
		int dur = note.getDuration(); // 0 = whole, 1 = half, 2 = quarter, ecc.

	    return switch (dur) {
	        case 0 -> SymbolRegistry.WHOLE_NOTE;
	        case 1 -> SymbolRegistry.HALF_NOTE;
	        case 2 -> SymbolRegistry.QUARTER_NOTE;
	        case 3 -> SymbolRegistry.EIGHTH_NOTE;
	        case 4 -> SymbolRegistry.SIXTEENTH_NOTE;
	        case 5 -> SymbolRegistry.THIRTY_SECOND_NOTE;
	        case 6 -> SymbolRegistry.SIXTY_FOURTH_NOTE;
	        default -> SymbolRegistry.QUARTER_NOTE; // fallback
	    };
	}
	
	@Override
	public void draw(Graphics g) {
		String glyph;
		int voice = note.getVoiceIndex();
		if (voice == 1)
			glyph = symbol.getGlyphUp();
		else
			glyph = symbol.getGlyphDown();
		FontMetrics fm = g.getFontMetrics();
		int width = fm.stringWidth(glyph);
		int ascent = fm.getAscent();
		int descent = fm.getDescent();
		int height = ascent + descent;

		Rectangle bounds = new Rectangle(getX(), getY() - ascent, width, height);
		setBounds(bounds);
		
		setColor(g, voice);		
		g.drawString(glyph, getX(), getY());
		
		int staffIndex = note.getStaffIndex();
		gScore.ledgerRenderer.drawLedgerLines(g, this, gScore.getStaff(staffIndex));
	}

	public int getCenterX() {
	    return getX() + getWidth() / 2;
	}

	public int getCenterY() {
	    return getY() + getHeight() / 2;
	}
	
	private void setColor(Graphics g, int voice) {
		if (isSelected()) {
			g.setColor(Color.RED);
			return;
		}
		if (voice == 1) {
			g.setColor(Color.BLACK);
		} else if (voice == 2) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.GRAY);
		}
	}

		@Override
	public GraphicalObject cloneObject() {
		GraphicalNote n = new GraphicalNote(note);
		n.setX(getX());
		n.setY(getY());
		n.setBounds(getBounds());
		n.setStaffPosition(getStaffPosition());
		return n;
	}

	public MusicalSymbol getSymbol() {
		return symbol;
	}

	@Override
	public void setStaffPosition(int p) {
		staffPosition = p;
	}

	@Override
	public int getStaffPosition() {
		return staffPosition;
	}
	
	public Note getNote() {
		return note;
	}

	@Override
	public Note getModelObject() {
		return note;
	}
}
