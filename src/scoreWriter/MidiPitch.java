package scoreWriter;

public final class MidiPitch {
    private final int midiNumber;
    private final int alteration;

    public MidiPitch(int midiNumber, int alteration) {
        this.midiNumber = midiNumber;
        this.alteration = alteration;
    }

    public int getMidiNumber() {
        return midiNumber;
    }

    public int getAlteration() {
        return alteration;
    }
    
    @Override
    public String toString() {
    	return "["+midiNumber+", "+alteration+"]";
    }
}
