package graphical;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import musicInterface.MusicObject;

public class ScoreLayout {
	private ScoreLayout() {}
	
    private static Map<MusicObject, Point> positions = new HashMap<>();

    public static void setPosition(MusicObject o, Point p) {
        positions.put(o, p);
    }

    public static Point getPosition(MusicObject o) {
        return positions.get(o);
    }
}
