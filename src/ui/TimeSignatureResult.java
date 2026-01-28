package ui;

public class TimeSignatureResult {
	private final int num;
	private final int den;

	public TimeSignatureResult(int num, int den) {
		this.num = num;
		this.den = den;
	}

	public int getNumberator() {
		return num;
	}

	public int getDenumerator() {
		return den;
	}
}