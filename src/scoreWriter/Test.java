package scoreWriter;

import scoreWriter.MusicalSymbol.Type;

public class Test {


	// String name, String iconPath, String glyph, Type type, int midiOffset
	public static void main(String[] args) {
		GraphicalNote n = new GraphicalNote(null);
		GraphicalClef c = new GraphicalClef(SymbolRegistry.CLEF_BASS);
		int midi = calculateMidiNumber(n, c);
		System.out.println(midi);
	}

	private static int calculateMidiNumber(GraphicalNote n, GraphicalClef c) {
	    int[] scale = {0, 2, 4, 5, 7, 9, 11}; // C D E F G A B
	    int position = 0;

	    // degree con supporto per numeri negativi
	    int degree = Math.floorMod(position, 7);      // 0..6
	    int octaveShift = Math.floorDiv(position, 7); // può essere negativo

	    return c.getMidiOffset() + scale[degree] + (octaveShift * 12) + n.getAlteration();
	}
	
}