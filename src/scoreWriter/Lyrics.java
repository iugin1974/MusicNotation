package scoreWriter;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import model.Lyric;

public class Lyrics {
	private List<Lyric> lyrics = new ArrayList<>();

	public void addLyric(Lyric l) {
		lyrics.add(l);
	}

	public List<Lyric> getLyrics() {
		return lyrics;
	}
	
	public void removeLyrics(int staff, int voice, int stanza) {
		for (int i = lyrics.size() - 1; i >= 0; i--) {
			Lyric l = lyrics.get(i);
			if (l.getStaff() == staff && l.getVoice() == voice && l.getStanza() == stanza) {
				lyrics.remove(i);
				l.getParentNote().removeLyric(stanza); // rimuove dalla nota
			}
		}
	}

	public List<Lyric> getLyrics(int staff, int voice, int stanza) {
		List<Lyric> result = new ArrayList<>();
		for (Lyric l : lyrics) {
			if (l.getStaff() == staff && l.getVoice() == voice && l.getStanza() == stanza) {
				result.add(l);
			}
		}
		return result;
	}

	public void draw(Graphics g, FontMetrics fm, int offsetY, int staffIndex, int voiceNumber, int stanza) {
		for (Lyric l : lyrics) {
			// disegna solo quelle corrispondenti a staff, voice e stanza
			if (l.getStaff() == staffIndex && l.getVoice() == voiceNumber && l.getStanza() == stanza) {
				l.draw(g, fm, offsetY);
			}
		}
	}
}
