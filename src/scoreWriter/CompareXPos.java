package scoreWriter;

public class CompareXPos implements java.util.Comparator<GraphicalObject> {
	@Override
	public int compare(GraphicalObject o1, GraphicalObject o2) {
		if (o1.getX() > o2.getX())
			return 1;
		if (o1.getX() < o2.getX())
			return -1;
		return 0;
	}
}