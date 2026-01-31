package graphical;

import java.util.ArrayList;
import java.util.List;

import scoreWriter.SymbolRegistry;

public class MusicalSymbol {
	private final String name;
	private final String iconPath;
	private final String glyphUp;
	private final String glyph;
	private final String glyphDown;
	private int duration = -1;
	private Type type;
	private int midiOffset = -1;
	private int[] semitoneMap = null;

	public enum Type {
		NOTE, REST, BARLINE, CLEF
	}

	// note
	public MusicalSymbol(String name, String iconPath, String glyphUp, String glyphDown, Type type, int duration) {
		this.name = name;
		this.iconPath = iconPath;
		this.glyphUp = glyphUp;
		this.glyph = "";
		this.glyphDown = glyphDown;
		this.type = type;
		this.duration = duration;

	}

	// pause
	public MusicalSymbol(String name, String iconPath, String glyph, Type type, int duration) {
		this.name = name;
		this.iconPath = iconPath;
		this.glyphUp = null;
		this.glyph = glyph;
		this.type = type;
		this.duration = duration;
		this.glyphDown = null;

	}

	// stanghette di battuta
	public MusicalSymbol(String name, String iconPath, String glyph, Type type) {
		this.name = name;
		this.iconPath = iconPath;
		this.glyphUp = null;
		this.type = type;
		this.glyph = glyph;
		this.glyphDown = null;

	}

	// chiavi
	public MusicalSymbol(String name, String iconPath, String glyph, Type type, int midiOffset, int[] semitoneMap) {
		this.name = name;
		this.iconPath = iconPath;
		this.glyphUp = null;
		this.glyph = glyph;
		this.type = type;
		this.midiOffset = midiOffset;
		this.glyphDown = null;
		this.semitoneMap = semitoneMap;

	}

	public String getName() {
		return name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getGlyphUp() {
		return glyphUp;
	}

	public String getGlyphDown() {
		return glyphDown;
	}

	public String getGlyph() {
		return glyph;
	}

	public Type getType() {
		return type;
	}

	public int getDuration() {
		return duration;
	}

	public int getMidiOffset() {
		return midiOffset;
	}

	public int[] getSemitoneMap() {
		return semitoneMap;
	}

	/**
	 * Ritorna il simbolo del tipo <i>type</i> e durata <i>duration</i>, oppure
	 * <i>null</i> se non trovato.
	 * @param type
	 * @param duration
	 * @return
	 */
	public static MusicalSymbol getByDuration(Type type, int duration) {
		if (type != Type.NOTE && type != Type.REST) return null;
		for (MusicalSymbol m : getByType(type)) {
			if (m.getDuration() == duration) {
				return m;
			}
		}
		return null;
	}
	
	public static List<MusicalSymbol> getByType(Type type) {
	    List<MusicalSymbol> result = new ArrayList<>();
	    for (MusicalSymbol m : SymbolRegistry.getAllSymbols()) {
	        if (m.getType() == type) {
	            result.add(m);
	        }
	    }
	    return result;
	}

}
