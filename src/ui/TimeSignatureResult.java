package ui;

public class TimeSignatureResult {
	private final int num;
	private final int den;
	private final boolean allStaves;

	public TimeSignatureResult(int num, int den, boolean allStaves) {
		this.num = num;
		this.den = den;
		this.allStaves = allStaves;
	}

	public int getNumberator() {
		return num;
	}

	public int getDenumerator() {
		return den;
	}
	public boolean isAllStaves() {
		return allStaves;
	}
}