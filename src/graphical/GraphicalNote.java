package graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import musicEvent.Note;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Score;
import scoreWriter.StaffInfo;
import scoreWriter.SymbolRegistry;

public class GraphicalNote extends GraphicalObject implements StaffInfo {

	private MusicalSymbol symbol;
	private final Note note;
	private final int duration;

	public GraphicalNote(Note n) {
		this.note = n;
		duration = n.getDuration();
		symbol = setSymbol();
		}

	@Override
	protected MusicalSymbol setSymbol() {
	    return switch (duration) {
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
		if (voice == 1) {
			glyph = symbol.getGlyphUp();
		} else {
			glyph = symbol.getGlyphDown();
		}

		setBounds(g, glyph);
		setColor(g, voice);
		g.drawString(glyph, getX(), getY());

		{
			Font oldFont = g.getFont();
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
			g.drawString(note.getMidiNumber()+","+note.getAlteration(), getX(), 120);
			g.setFont(oldFont);
		}
		
		Score score = gScore.getScore();
		KeySignature ks = score.getKeySignature(note.getStaffIndex(), note.getTick());
		int midi = note.getMidiNumber();
		int alt  = note.getAlteration();

		// Se l'alterazione NON è implicita dalla chiave, va disegnata
		if (!ks.isInKey(midi, alt)) {

		    String accidentalGlyph = null;

		    switch (alt) {
		        case -2 -> accidentalGlyph = "\uE264"; // doppio bemolle
		        case -1 -> accidentalGlyph = "\uE260"; // bemolle
		        case  0 -> accidentalGlyph = "\uE261"; // bequadro
		        case  1 -> accidentalGlyph = "\uE262"; // diesis
		        case  2 -> accidentalGlyph = "\uE263"; // doppio diesis
		    }

		    if (accidentalGlyph != null) {
		        g.drawString(accidentalGlyph, getX() - 10, getY());
		    }
		}

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
		note.setStaffPosition(p);
	}

	@Override
	public int getStaffPosition() {
		return note.getStaffPosition();
	}

	public Note getNote() {
		return note;
	}

	@Override
	public Note getModelObject() {
		return note;
	}

    @Override
    public void moveTo(int x, int y) {
        super.moveTo(x, y); // sposta la nota

        // notifica tutte le curve collegate
        for (CurvedConnection c : getModelObject().getCurvedConnections()) {
            GraphicalObject gObj = super.getGraphicalScore().getGraphicalObject(c);

            if (gObj instanceof GraphicalCurvedConnection gCurve) {
                gCurve.updateFromNotes();
            }
        }
    }
}
