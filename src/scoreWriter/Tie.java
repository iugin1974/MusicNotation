package scoreWriter;

public class Tie  extends CurvedConnection {
		
		@Override
		public void setNotes(GraphicalNote n1, GraphicalNote n2) {
			n1.setTie(this);
			n2.setTie(this);
			n1.tieStart();
			n2.tieEnd();
			setX(n1.getX());
			setY(n1.getY());
			setX1(n2.getX());
			setY1(n2.getY());
	}
}
