package graphical;

import notation.CurvedConnection;
import notation.Tie;

public class GraphicalTie extends GraphicalCurvedConnection {

	protected GraphicalTie(CurvedConnection model) {
		super(model);
	}
	
	 public Tie getTie() {
	        return (Tie) model;
	    }

	 @Override
	 protected MusicalSymbol setSymbol() {
		return null;
	 }

}
