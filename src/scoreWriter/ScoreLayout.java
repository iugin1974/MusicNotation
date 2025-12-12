package scoreWriter;

public final class ScoreLayout {

    private ScoreLayout() {} // niente istanze

    public static final int STAFF_LINE_SPACING = 20;
    public static final int STAFF_MARGIN_LEFT = 60;
    public static final int STAFF_MARGIN_RIGHT = 40;

    public static final int NOTEHEAD_SIZE = 22;
    public static final int STEM_LENGTH = 35;

    public static final int KEY_SIGNATURE_GAP = 12;
    public static final int CLEF_SIZE = 48;

    public static final int LYRICS_OFFSET = 30;

    // Se un giorno vuoi lo zoom:
    public static double SCALE = 1.0;

    public static int scaled(int base) {
        return (int) (base * SCALE);
    }
}
