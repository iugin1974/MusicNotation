package graphical;

import notation.CurvedConnection;
import notation.Slur;

public class GraphicalSlur extends GraphicalCurvedConnection {

	protected GraphicalSlur(CurvedConnection model) {
		super(model);
	}

	public Slur getSlur() {
        return (Slur) model;
    }

	@Override
	protected MusicalSymbol setSymbol() {
		return null;
	}

}
