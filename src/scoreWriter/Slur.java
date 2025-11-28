package scoreWriter;

public class Slur extends CurvedConnection {
	
	@Override
	public void setNotes(GraphicalNote n1, GraphicalNote n2) {
		n1.setSlur(this);
		n2.setSlur(this);
		n1.slurStart();
		n2.slurEnd();
		setX(n1.getX());
		setY(n1.getY());
		setX1(n2.getX());
		setY1(n2.getY());
	}
}