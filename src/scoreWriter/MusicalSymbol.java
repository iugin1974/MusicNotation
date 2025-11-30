package scoreWriter;

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
	};

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
	public MusicalSymbol(String name, String glyph, Type type, int duration) {
		this.name = name;
		this.iconPath = null;
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
}
