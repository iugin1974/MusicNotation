package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import musicLily.LilyNote;
import scoreWriter.VoiceLayer.VoiceType;

public class Exporter {

	private Score score;
	private GUI gui;
	private GraphicalClef clef;
	
	public Exporter(GUI gui) {
		this.gui = gui;
	}

	public void setScore(Score score) {
		this.score = score;		
	}
	
	public void parse() {
		for (int i = 0; i < score.getStaffCount(); i++) {
			parseStaff(score.getStaff(i), i);
		}
	}
	
	private void parseStaff(Staff staff, int staffNumber) {
		for (VoiceLayer voice : staff.getVoices()) {
			parseVoice(voice, staffNumber);
		}
	}

	private void parseVoice(VoiceLayer voice, int staffNumber) {
		List<GraphicalObject> objs = mixLayers(voice.getVoiceType(), staffNumber);
		for (GraphicalObject go : objs) {
			if (go instanceof GraphicalClef) parseClef((GraphicalClef)go);
			if (go instanceof GraphicalNote) parseNote((GraphicalNote)go, staffNumber);
			if (go instanceof GraphicalBar) parseBar((GraphicalBar)go);
		}
	}
	
	/** combina il layer staff-wide con la voceOne o two */
	private List<GraphicalObject> mixLayers(VoiceType voiceType, int staffNumber) {
		List<GraphicalObject> objects = new ArrayList<>();
		objects.addAll(score.getStaffWideObjects(staffNumber));
		objects.addAll(score.getObjects(staffNumber, voiceType));
		objects.sort(new CompareXPos());
		return objects;
	}

	private void parseClef(GraphicalClef go) {
		clef = go;
		if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE) { 
			System.out.println("\\clef \"treble\"");
		}
		else if (go.getSymbol() == SymbolRegistry.CLEF_TREBLE_8) { 
			System.out.println("\\clef \"treble_8\"");
		}
		else if (go.getSymbol() == SymbolRegistry.CLEF_BASS) {
			System.out.println("\"\\clef \"bass\"\"");
		}
		// TODO continua
		
	}

	private void parseNote(GraphicalNote go, int staffNumber) {
		int midi = calculateMidiNumber(staffNumber, go);
		go.setMidiNumber(midi);
		LilyNote ln = new LilyNote(go);
		System.out.print(ln.getNamedNote()+" ");
		if (go.isSlurStart()) System.out.print("(");
		if (go.isSlurEnd()) System.out.print(")");
		if (go.isTiedStart()) System.out.print("~");
		
	}

	private void parseBar(GraphicalBar b) {
		if (b.getSymbol().equals(SymbolRegistry.SINGLE_BARLINE)) System.out.println("|");
		else if (b.getSymbol().equals(SymbolRegistry.DOUBLE_BARLINE)) System.out.println("\\bar \"||\"");
	}
	
	private int calculateMidiNumber(int staffNumber, GraphicalNote n) {
		if (clef == null) {
			System.out.println("Export ist no possible. No clef");
			return -1;
		}
	    int[] scale = clef.getSemitoneMap();
	    GraphicalStaff s = gui.getStaff(staffNumber);
	    int position = s.getPosInStaff(n);

	    // degree con supporto per numeri negativi
	    int degree = Math.floorMod(position, 7);      // 0..6
	    int octaveShift = Math.floorDiv(position, 7); // può essere negativo

	    return clef.getMidiOffset() + scale[degree] + (octaveShift * 12) + n.getAlteration();
	}
	
}
