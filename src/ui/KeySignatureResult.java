package ui;

public class KeySignatureResult {
    private final boolean allStaves;
    private int alterations;
    private int mode;

    public KeySignatureResult(int alterations, int mode, boolean allStaves) {
        this.alterations = alterations;
        this.mode = mode;
        this.allStaves = allStaves;
    }

    public boolean isAllStaves() {
        return allStaves;
    }

	public int getAlterations() {
		return alterations;
	}

	public int getMode() {
		return mode;
	}
}