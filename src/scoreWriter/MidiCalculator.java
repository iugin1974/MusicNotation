package scoreWriter;

import graphical.GraphicalClef;
import graphical.GraphicalNote;
import model.KeySignature;
import musicEvent.Modus;

public class MidiCalculator {

	public static int calculateMidiNumber(GraphicalNote n, GraphicalClef clef, KeySignature ks) {
		if (clef == null) {
			System.out.println("Export ist no possible. No clef");
			return -1;
		}
	    int[] scale = clef.getSemitoneMap();
	    int position = n.getStaffPosition();
	    // Aggiungi lo shift per partire da Do centrale come 0
	    position += 2;  // Aggiungi lo shift (staffPosition 0 = Do centrale)
	    // degree con supporto per numeri negativi
	    int degree = Math.floorMod(position, 7);      // 0..6
	    int octaveShift = Math.floorDiv(position, 7); // può essere negativo

	    if (ks == null) ks = new KeySignature(0, 0, Modus.MAJOR_SCALE);
	    int typeOfAlterations = ks.getTypeOfAlterations();
	    int[] keySignatureIndex;
	    if (typeOfAlterations == 1) keySignatureIndex = ks.getSharpsIndex();
	    else keySignatureIndex = ks.getFlatsIndex();
	    return clef.getMidiOffset() + scale[degree] + (octaveShift * 12) + n.getNote().getAlteration();
	}
}
