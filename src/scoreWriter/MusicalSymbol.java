package scoreWriter;

public class MusicalSymbol {
    private final String name;
    private final String iconPath;
    private final String glyphUp;
    private final String glyphDown;
    private final boolean hasStem;
    private final boolean isRest;
    private final boolean isBarline;
    private int duration;

    public MusicalSymbol(String name, String iconPath,
                         String glyphUp, String glyphDown,
                         boolean hasStem, boolean isRest, boolean isBarline,
                         int duration) {
        this.name = name;
        this.iconPath = iconPath;
        this.glyphUp = glyphUp;
        this.glyphDown = glyphDown;
        this.hasStem = hasStem;
        this.isRest = isRest;
        this.isBarline = isBarline;
        this.duration = duration;
    }

    public String getName() { return name; }
    public String getIconPath() { return iconPath; }
    public String getGlyphUp() { return glyphUp; }
    public String getGlyphDown() { return glyphDown; }
    public boolean hasStem() { return hasStem; }
    public boolean isRest() { return isRest; }
    public boolean isBarline() { return isBarline; }
    public int getDuration() { return duration; }
}
