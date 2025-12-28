package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.GraphicalObject;

public class SelectionManager {

    private final List<GraphicalObject> selected = new ArrayList<>();

    public void select(GraphicalObject o) {
        if (!selected.contains(o)) {
            selected.add(o);
            o.select(true);
        } else {
        	selected.remove(o);
        	o.select(false);
        }
    }

    public void deselect(GraphicalObject o) {
        if (selected.remove(o)) {
            o.select(false);
        }
    }

    public void deselectAll() {
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
