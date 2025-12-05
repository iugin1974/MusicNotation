package scoreWriter;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Lyrics {
	private List<Lyric> lyrics;

	public Lyrics() {
		lyrics = new ArrayList<>();
	}

	public void addSyllable(Lyric lyric) {
		lyrics.add(lyric);
	}

	public List<Lyric> getLyrics() {
		return lyrics;
	}

	public void draw(Graphics g, FontMetrics fm, int offsetY) {
		for (Lyric lyric : lyrics) {
			lyric.draw(g, fm, offsetY);
		}
	}
	
	public int length() {
		return lyrics.size();
	}
	
	public Lyric getLyric(int i) {
		return lyrics.get(i);
	}
}
