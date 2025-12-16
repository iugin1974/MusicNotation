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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import model.Lyric;
import model.MusicalSymbol;
import musicEvent.Note;
import scoreWriter.CurvedConnection;
import scoreWriter.Slur;
import scoreWriter.StaffInfo;
import scoreWriter.Tie;

public class GraphicalNote extends GraphicalObject implements StaffInfo {

	private MusicalSymbol symbol;
	private Slur slur;
	private final Note note;
	private boolean slurStart = false;
	private boolean slurEnd = false;
	private boolean tieStart = false;
	private boolean tieEnd = false;
	private int voice = 1;
	private Tie tie;
	private int staffIndex;
	private int staffPosition; // 0 MI, 1 FA, ecc.
	private Map<Integer, Lyric> lyrics = null;

	public GraphicalNote(MusicalSymbol symbol, Note n) {
		this.symbol = symbol;
		this.note = n;
		setup();
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

	public void setVoice(int voice) {
		this.voice = voice;
	}

	public void setSlur(Slur slur) {
		this.slur = slur;
	}

	public void setTie(Tie tie) {
		this.tie = tie;
	}

	public Slur getSlur() {
		return slur;
	}

	public Tie getTie() {
		return tie;
	}

	public CurvedConnection getCurvedConnection() {
		if (tie != null)
			return tie;
		return slur; // che può essere null. Quindi ritorna null se entrambi non esistono
	}

	public boolean isCurveStart() {
		return isTiedStart() || isSlurStart();
	}

	public boolean isCurveEnd() {
		return isTiedEnd() || isSlurEnd();
	}

	@Override
	public void draw(Graphics g) {
		String glyph;
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
		
		setColor(g);
		if (isSelected()) {
			g.setColor(Color.RED);
		}
		g.drawString(glyph, getX(), getY());
		setColor(g);
	}

	private void setColor(Graphics g) {
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
		GraphicalNote n = new GraphicalNote(symbol, note);
		n.setX(getX());
		n.setY(getY());
		n.setBounds(getBounds());
		return n;
	}

	public MusicalSymbol getSymbol() {
		return symbol;
	}

	public void slurStart() {
		slurStart = true;
	}

	public void slurEnd() {
		slurEnd = true;
	}

	public void slurNone() {
		slurStart = slurEnd = false;
	}

	public void tieStart() {
		tieStart = true;
	}

	public void tieEnd() {
		tieEnd = true;
	}

	public void tieNone() {
		tieStart = tieEnd = false;
	}

	public boolean isSlurStart() {
		return slurStart;
	}

	public boolean isSlurEnd() {
		return slurEnd;
	}

	public boolean isTiedStart() {
		return tieStart;
	}

	public boolean isTiedEnd() {
		return tieEnd;
	}

	@Override
	public void setStaffIndex(int i) {
		staffIndex = i;		
	}

	@Override
	public int getStaffIndex() {		
		return staffIndex;
	}

	@Override
	public void setStaffPosition(int p) {
		staffPosition = p;
	}

	@Override
	public int getStaffPosition() {
		return staffPosition;
	}
	
	public void addLyric(Lyric lyric) {
		if (lyrics == null) lyrics = new HashMap<>();
		lyrics.put(lyric.getStanza(), lyric);
	}
	
	public Lyric getLyric(int stanza) {
		return lyrics.get(stanza);
	}
	
	public int getNumberOfStanzas() {
		if (lyrics == null) return 0;
		return lyrics.size();
	}
	
	public boolean hasLyric() {
		return lyrics != null;
	}

	public void removeLyric() {
		lyrics = null;
	}

	public void removeLyric(int stanza) {
	    if (lyrics != null) {
	        lyrics.remove(stanza);

	        // Se la nota non ha più lyric → mette a null
	        if (lyrics.isEmpty()) {
	            lyrics = null;
	        }
	    }
	}

	public Note getNote() {
		return note;
	}
}
