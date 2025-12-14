package scoreWriter;

import graphical.GraphicalClef;
import graphical.GraphicalNote;

public class MidiCalculator {

	public static int calculateMidiNumber(GraphicalNote n, GraphicalClef clef) {
		if (clef == null) {
			System.out.println("Export ist no possible. No clef");
			return -1;
		}
	    int[] scale = clef.getSemitoneMap();
	    int position = n.getStaffPosition();

	    // degree con supporto per numeri negativi
	    int degree = Math.floorMod(position, 7);      // 0..6
	    int octaveShift = Math.floorDiv(position, 7); // può essere negativo

	    return clef.getMidiOffset() + scale[degree] + (octaveShift * 12) + n.getNote().getAlteration();
	}
}
