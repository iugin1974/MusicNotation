package scoreWriter;

import java.util.ArrayList;

public class Test {

	static int lineNumber = 5;
	static int distanza = 10;
	static int y = 50;

	public static void main(String[] args) {
		ArrayList<Integer> l = getSnapPoints();
		for (Integer i : l)
		System.out.println(i);

	}

	static public int getLineY(int line) {
		// inverte la numerazione delle linee (la 5 viene considerata la 0)
		int l = lineNumber - line;
		return y + (l * distanza);
	}

	static public ArrayList<Integer> getSnapPoints() {
		ArrayList<Integer> snapPoints = new ArrayList<>();
		// da due tagli sotto a due tagli sopra
		int bottomLine = getLineY(0) + (distanza * 2);
		int topLine = getLineY(lineNumber) - (distanza * 2);
		System.out.println(bottomLine+" "+topLine);
		for (int i = topLine; i <= bottomLine; i += distanza / 2) {
			snapPoints.add(i);
		}
		return snapPoints;
	}
}
