package scoreWriter;

import java.util.ArrayList;

public class SelectionManager {

    private ArrayList<ArrayList<GraphicalObject>> selectedPerStaff;

    public SelectionManager() {
    	selectedPerStaff = new ArrayList<>();
    }
    
    public SelectionManager(int staffCount) {
        selectedPerStaff = new ArrayList<>();
        for (int i = 0; i < staffCount; i++) {
            selectedPerStaff.add(new ArrayList<>());
        }
    }

    public void addStaff() {
        selectedPerStaff.add(new ArrayList<>());
    }

    public void select(GraphicalObject o, int staffIndex) {
        ArrayList<GraphicalObject> list = selectedPerStaff.get(staffIndex);

        if (!list.contains(o)) {
            list.add(o);
            o.select(true);
        }
    }

    public void deselect(GraphicalObject o, int staffIndex) {
        ArrayList<GraphicalObject> list = selectedPerStaff.get(staffIndex);

        if (list.remove(o)) {
            o.select(false);
        }
    }

    public void deselectAll() {
        for (ArrayList<GraphicalObject> staff : selectedPerStaff) {
            for (GraphicalObject o : staff)
                o.select(false);
            staff.clear();
        }
    }

    public ArrayList<GraphicalObject> getSelected(int staffIndex) {
        return selectedPerStaff.get(staffIndex);
    }

    public ArrayList<GraphicalObject> getAllSelected() {
    	ArrayList<GraphicalObject> all = new ArrayList<>();
        for (ArrayList<GraphicalObject> staff : selectedPerStaff)
            all.addAll(staff);
        return all;
    }

    public void moveSelectionToStaff(GraphicalObject o, int oldStaff, int newStaff) {
        if (selectedPerStaff.get(oldStaff).remove(o)) {
            selectedPerStaff.get(newStaff).add(o);
        }
    }
    
    public ArrayList<GraphicalNote> getSelectedNotes(int staffIndex) {
    	ArrayList<GraphicalNote> result = new ArrayList<>();
        for (GraphicalObject o : selectedPerStaff.get(staffIndex)) {
            if (o instanceof GraphicalNote) {
                result.add((GraphicalNote) o);
            }
        }
        result.sort(new CompareXPos());
        return result;
    }
    
    /**
     * Restituisce tutte le note selezionate nello staff specificato,
     * ordinate in base alla loro posizione X.
     *
     * @param staffNumber indice dello staff
     * @return lista di GraphicalNote selezionate nello staff
     */
    public ArrayList<GraphicalNote> getSelectedNotesFromStaff(int staffNumber) {
        ArrayList<GraphicalNote> notes = new ArrayList<>();
        ArrayList<GraphicalObject> selectedObjects = getSelected(staffNumber);

        for (GraphicalObject o : selectedObjects) {
            if (o instanceof GraphicalNote) {
                notes.add((GraphicalNote) o);
            }
        }

        notes.sort(new CompareXPos());
        return notes;
    }

    
    public int getNumberOfStaves() {
    	return selectedPerStaff.size();
    }
    
}