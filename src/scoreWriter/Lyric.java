package scoreWriter;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class Lyric {
	    private Syllable syllable;
	    private GraphicalNote parentNote;
	    private int staff, voice, stanza;

	    public Lyric(Syllable syllable, GraphicalNote note, int staff, int voice, int stanza) {
	        this.syllable = syllable;
	        this.parentNote = note;
	        this.staff = staff;
	        this.voice = voice;
	        this.stanza = stanza;
	        note.addLyric(this);
	    }

	    public Syllable getSyllable() { return syllable; }
	    public GraphicalNote getParentNote() { return parentNote; }
	    public int getStaff() { return staff; }
	    public int getVoice() { return voice; }
	    public int getStanza() { return stanza; }
	    
	    public void draw(Graphics g, FontMetrics fm, int y) {
	        // posizione derivata dalla nota
	        int x = parentNote.getX();          

	        String text = syllable.getText();
	        if (text == null) return;

	        // centratura grossolana (opzionale)
	        int textWidth = fm.stringWidth(text);
	        int drawX = x - (textWidth / 2);

	        g.drawString(text, drawX, y);
	    }
}
