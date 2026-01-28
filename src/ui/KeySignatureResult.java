package ui;

public class KeySignatureResult {
    private int alterations;
    private int mode;

    public KeySignatureResult(int alterations, int mode) {
        this.alterations = alterations;
        this.mode = mode;
    }
    
	public int getAlterations() {
		return alterations;
	}

	public int getMode() {
		return mode;
	}
}