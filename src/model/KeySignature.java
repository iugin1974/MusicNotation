package model;

import musicEvent.Modus;

public class KeySignature {

	private int numberOfAlterations;
	private int typeOfAlterations;
	private Modus modus = Modus.MAJOR_SCALE;
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

	public KeySignature(int numberOfAlterations, int typeOfAlterations, Modus modus) {
		this.numberOfAlterations = numberOfAlterations;
		this.typeOfAlterations = typeOfAlterations;
		this.modus = modus;
	}

	public int getNumberOfAlterations() {
		return numberOfAlterations;
	}

	public int getTypeOfAlterations() {
		return typeOfAlterations;
	}

	public Modus getModus() {
		return modus;
	}

	public int[] getSharpsIndex() {
		return keySignatureSharpsIndex;
	}

	public int[] getFlatsIndex() {
		return keySignatureFlatsIndex;
	}
}
