package graphical;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import musicEvent.Note;
import musicInterface.MusicObject;
import notation.Lyric;
import scoreWriter.StaffInfo;

public class GraphicalLyrics {
	
	Map<MusicObject, GraphicalObject> objects;
	private GraphicalScore gScore;
	
	public GraphicalLyrics(Map<MusicObject, GraphicalObject> objects, GraphicalScore gScore) {
		this.objects = objects;
		this.gScore = gScore;
	}

	private List<GraphicalNote> getNotes(Map<MusicObject, GraphicalObject> objects) {
		List<GraphicalNote> notes = new ArrayList<>();
		for (GraphicalObject o : objects.values()) {
			if (o instanceof GraphicalNote) notes.add((GraphicalNote)o);
		}
		return notes;
	}
	
	public void draw(Graphics g) {
		List<GraphicalNote> notes = getNotes(objects);
		if (notes.isEmpty()) return;
		drawLyrics(g, notes);
		drawLines(g, notes);
		
	}

	private void drawLyrics(Graphics g, List<GraphicalNote> notes) {
		for (GraphicalNote gn : notes) {
			Note note = gn.getNote();
			if (!note.hasLyric()) continue;
			int baseX = gn.getCenterX();          // centro nota
		    int staffIndex = note.getStaffIndex();
		    int baseY = gScore.getStaff(staffIndex).getYPosOfLine(0) + 20;     // sotto il pentagramma

		    Font oldFont = g.getFont();
		    Font fontLyric = new Font("SansSerif", Font.PLAIN, 12);
		    g.setFont(fontLyric);
		    FontMetrics fm = g.getFontMetrics();

		    // Disegna UNA riga per ogni stanza
		    for (int stanza = 0; stanza < note.getNumberOfStanzas(); stanza++) {
		        Lyric lyric = note.getLyric(stanza);
		        if (lyric == null)
		            continue;

		        String text = lyric.getSyllable().getText();
		        if (text == null || text.isEmpty())
		            continue;

		        int textWidth = fm.stringWidth(text);
		        int y = baseY + stanza * (fm.getHeight() + 4);

		        g.drawString(text, baseX - textWidth / 2, y);
		    }
		    g.setFont(oldFont);
		}
	}
	
	private void drawLines(Graphics g, List<GraphicalNote> notes) {
		for (int i = 0; i < notes.size() - 1; i++) {
			GraphicalNote gn1 = notes.get(i);
			Note n1 = gn1.getNote();
			if (!n1.hasLyricExtender() && !n1.hasSyllableDivision()) continue;
			GraphicalNote gn2 = notes.get(i + 1);
			int x1 = gn1.getX();
			int x2 = gn2.getX();
			int staffIndex = n1.getStaffIndex();
		    int baseY = gScore.getStaff(staffIndex).getYPosOfLine(0) + 20;     // sotto il pentagramma
			if (n1.hasLyricExtender()) drawLyricExtender(g, x1, x2, baseY);
			if (n1.hasSyllableDivision()) drawSyllableDivision(g, x1, x2, baseY);
		}
	}
	
	private void drawSyllableDivision(Graphics g, int x1, int x2, int y) {
		for (int x = x1 + 20; x < x2 - 20; x+=50) {
			g.drawLine(x, y, x + 10, y);
		}
		
	}

	private void drawLyricExtender(Graphics g, int x1, int x2, int y) {
		g.drawLine(x1 + 20, y, x2 - 20, y);
		
	}
}
