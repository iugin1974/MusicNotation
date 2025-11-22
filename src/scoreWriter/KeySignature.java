package scoreWriter;

public enum KeySignature {
    TREBLE, BASS, ALTO, TENOR;
    
    public int getMidiOffset() {
        return switch (this) {
            case TREBLE -> 60; // C4 = Do centrale
            case BASS -> 48;   // C3
            case ALTO -> 57;   // A3
            case TENOR -> 52;  // E3
        };
    }
}

