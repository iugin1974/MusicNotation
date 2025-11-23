package scoreWriter;

import java.util.ArrayList;

import musicLily.LilyNote;

public class Exporter {

	private ArrayList<ArrayList<GraphicalObject>> staffList;
	private GUI gui;
	private GraphicalClef clef;
	
	public Exporter(GUI gui) {
		this.gui = gui;
	}

	public void setStaffs(ArrayList<ArrayList<GraphicalObject>> staffList) {
		this.staffList = staffList;		
	}
	
	public void parse() {
		for (int i = 0; i< staffList.size(); i++) {
			System.out.println("Staff "+i+":");
			parseStaff(i);
		}
	}
	
	public void parseStaff(int staffNumber) {
		ArrayList<GraphicalObject> staff = staffList.get(staffNumber);
		for (GraphicalObject go : staff) {
			if (go instanceof GraphicalClef) parseClef((GraphicalClef)go);
			if (go instanceof GraphicalNote) parseNote((GraphicalNote)go, staffNumber);
			if (go instanceof GraphicalBar) parseBar((GraphicalBar)go);
		}
	}

	private void parseClef(GraphicalClef go) {
		clef = go;
		if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE) { 
			System.out.println("treble");
		}
		else if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE_8) { 
			System.out.println("treble8");
		}
		else if (go.getSymbol() == SymbolRegistry.CLEF_BASS) {
			System.out.println("bass");
		}
		// TODO continua
		
	}

	private void parseNote(GraphicalNote go, int staffNumber) {
		int midi = calculateMidiNumber(staffNumber, go);
		go.setMidiNumber(midi);
		LilyNote ln = new LilyNote(go);
		System.out.print(ln.getNamedNote()+" ");
		
	}

	private void parseBar(GraphicalBar b) {
		if (b.getSymbol().equals(SymbolRegistry.SINGLE_BARLINE)) System.out.println("|");
		else if (b.getSymbol().equals(SymbolRegistry.DOUBLE_BARLINE)) System.out.println("\\bar \"||\"");
	}
	
	private int calculateMidiNumber(int staffNumber, GraphicalNote n) {
	    int[] scale = clef.getSemitoneMap();
	    GraphicalStaff s = gui.getStaff(staffNumber);
	    int position = s.getPosInStaff(n);

	    // degree con supporto per numeri negativi
	    int degree = Math.floorMod(position, 7);      // 0..6
	    int octaveShift = Math.floorDiv(position, 7); // può essere negativo

	    return clef.getMidiOffset() + scale[degree] + (octaveShift * 12) + n.getAlteration();
	}
	
}
