package model;

public class KeySignature {

	private int numberOfAlterations;
	private int typeOfAlterations;
	private Mode mode = Mode.MAJOR;
	// Indici delle posizioni verticali per le alterazioni della tonalità
	// ordinati secondo l’ordine delle alterazioni nella chiave musicale.
	// I valori corrispondono agli indici dell’array restituito da
	// staff.getYPosOfLinesAndSpacesExtended(extraLinesAbove, extraLinesBelow).

	// Diesis: F#, C#, G#, D#, A#, E#, B# (ordine standard)
	private int[] keySignatureSharpsIndex = { 8, 5, 9, 6, 3, 7, 4 };

	// Bemolli: Bb, Eb, Ab, Db, Gb, Cb, Fb (ordine standard)
	// L’ordine dei bemolli nella chiave parte da Si♭ e segue l’ordine delle
	// tonalità
	private int[] keySignatureFlatsIndex = { 4, 7, 3, 6, 2, 5, 1 };

	public enum Mode {
		MAJOR, MINOR, DORIAN
	}

	public KeySignature(int numberOfAlterations, int typeOfAlterations, Mode mode) {
		this.numberOfAlterations = numberOfAlterations;
		this.typeOfAlterations = typeOfAlterations;
		this.mode = mode;

	}
	
	public int getNumberOfAlterations() {
		return numberOfAlterations;
	}

	public int getTypeOfAlterations() {
		return typeOfAlterations;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public int[] getSharpsIndex() {
		return keySignatureSharpsIndex;
	}
	
	public int[] getFlatsIndex() {
		return keySignatureFlatsIndex;
	}
}
