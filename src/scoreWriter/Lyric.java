package scoreWriter;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class Lyric {
    private String syllable = null;
    private boolean isExtender = false;   // per i "_" di Lylipond/Musescore
    private boolean isHyphen = false;
    private GraphicalNote parentNote; // a quale nota appartiene
    
    public Lyric(String syllable, GraphicalNote parentNote) {
    	this.parentNote = parentNote;
    	 // Riconoscimento extender/melisma
    	if (syllable.equals("_")) {
    	    this.isExtender = true;
    	} else if (syllable.equals("--")) {
    	    this.isHyphen = true;
    	} else {
    	    this.syllable = syllable;
    	}
    }
    
    public String getSyllable() {
    	return syllable;
    }
    
    public boolean isExtender() {
    	return isExtender;
    }
    
    public boolean isHyphen() {
    	return isHyphen;
    }
    
    public void draw(Graphics g, FontMetrics fm, int y) {
        // posizione derivata dalla nota
        int x = parentNote.getX();          

        // centratura grossolana (opzionale)
        int textWidth = fm.stringWidth(syllable);
        int drawX = x - (textWidth / 2);

        g.drawString(syllable, drawX, y);
    }
}
