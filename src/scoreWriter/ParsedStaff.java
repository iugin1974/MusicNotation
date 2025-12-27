package scoreWriter;

import java.util.List;

import graphical.GraphicalClef;
import graphical.GraphicalObject;
import notation.Staff;

public class ParsedStaff {
    public final List<List<GraphicalObject>> voices; // tutte le voci + wide combinate
    public final Staff staff;

    public ParsedStaff(Staff staff, List<List<GraphicalObject>> voices) {
        this.staff = staff;
        this.voices = voices;
    }
    
    public Staff getGraphicalStaff() {
    	return staff;
    }
    
    public boolean startsWithClef() {
    	if (voices.get(0).isEmpty()) return false;
    	return voices.get(0).get(0) instanceof GraphicalClef;
    }
}
