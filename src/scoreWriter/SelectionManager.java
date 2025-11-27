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

        // Ordina le note in base alla loro X
     //   result.sort(Comparator.comparingInt(GraphicalObject::getX));

        return result;
    }
    
    public int getStaffNumber() {
    	return selectedPerStaff.size();
    }
}
