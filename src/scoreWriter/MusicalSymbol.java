package scoreWriter;

public class MusicalSymbol {
    private final String name;
    private final String iconPath;
    private final String glyphUp;
    private final String glyphDown;
    private int duration;
    private Type type;
    public enum Type { NOTE, REST, BARLINE, CLEF };
    
    public MusicalSymbol(String name, String iconPath,
                         String glyphUp, String glyphDown,
                         Type type,
                         int duration) {
        this.name = name;
        this.iconPath = iconPath;
        this.glyphUp = glyphUp;
        this.glyphDown = glyphDown;
        this.type = type;
        this.duration = duration;
    }

    public String getName() { return name; }
    public String getIconPath() { return iconPath; }
    public String getGlyphUp() { return glyphUp; }
    public String getGlyphDown() { return glyphDown; }
    public Type getType() { return type; }
    public int getDuration() { return duration; }
}
