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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import musicEvent.Note;

public class GraphicalNote extends Note implements GraphicalObject, StaffInfo {

	private MusicalSymbol symbol;
	private Slur slur;
	private boolean slurStart = false;
	private boolean slurEnd = false;
	private boolean tieStart = false;
	private boolean tieEnd = false;
	private int voice = 1;
	private final GraphicalHelper helper = new GraphicalHelper();
	private Tie tie;
	private int staffIndex;
	private int staffPosition; // 0 DO, 1 RE, 2 MI
	private Map<Integer, Lyric> lyrics = null;

	public GraphicalNote(MusicalSymbol symbol) {
		super(0); // crea una nota con midi 0
		this.symbol = symbol;
		duration = symbol.getDuration();
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

	public void setMidiNumber(int midi) {
		this.midiNumber = midi;
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

		Rectangle bounds = new Rectangle(helper.getX(), helper.getY() - ascent, width, height);
		helper.setBounds(bounds);
		if (voice == 1) {
			g.setColor(Color.BLACK);
		} else if (voice == 2) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.GRAY);
		}
		
		if (helper.isSelected()) {
			g.setColor(Color.RED);
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
		helper.setX(x);
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
		n.setBounds(getBounds());
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

}
