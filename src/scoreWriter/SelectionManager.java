package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.GraphicalObject;

public class SelectionManager {

	private final List<GraphicalObject> selected = new ArrayList<>();

	public void select(GraphicalObject o, boolean add) {
		boolean alreadySelected = selected.contains(o);
		if (alreadySelected) {
			// toggle off
			selected.remove(o);
			o.select(false);
		} else {
			if (!add)
				clearSelection(); // se non voglio aggiungere, rimuovo tutto
			selected.add(o);
			o.select(true);
		}
	}

	public void deselect(GraphicalObject o) {
		if (selected.remove(o)) {
			o.select(false);
		}
	}

	public void clearSelection() {
		for (GraphicalObject o : selected) {
			o.select(false);
		}
		selected.clear();
	}

	public List<GraphicalObject> getSelected() {
		return selected;
	}

	public boolean hasSelectedObjects() {
		return selected != null && selected.size() > 0;
	}
		
}
