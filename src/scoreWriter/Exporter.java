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
			System.out.println(go.toString());
		}
	}

	private void parseNote(GraphicalNote go) {
		// TODO Auto-generated method stub
		
	}

}
