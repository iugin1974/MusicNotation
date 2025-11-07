package scoreWriter;

import java.util.ArrayList;

public class Exporter {

	private ArrayList<ArrayList<GraphicalObject>> staffList;
	
	public void setStaffs(ArrayList<ArrayList<GraphicalObject>> staffList) {
		this.staffList = staffList;		
	}
	
	public void parse() {
		ArrayList<GraphicalObject> staff = staffList.get(0);
		for (GraphicalObject go : staff) {
			if (go instanceof GraphicalNote) parseNote((GraphicalNote) go);
		}
		System.out.println();
	}

	private void parseNote(GraphicalNote go) {
		// TODO Auto-generated method stub
		
	}

}
