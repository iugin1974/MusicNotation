package model;

public class Clef {
    public enum ClefType { TREBLE, BASS, TREBLE_8, ALTO, TENOR, SOPRANO, BARITONE }

    private final ClefType type;

    public Clef(ClefType type) {
        this.type = type;
    }

    public ClefType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Clef{" + "type=" + type + '}';
    }
}
