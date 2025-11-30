package scoreWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialGrid {

    private final int cellSize; // es: 20 px
    private final Map<Integer, ArrayList<GraphicalObject>> grid = new HashMap<Integer, ArrayList<GraphicalObject>>();

    public SpatialGrid(int cellSize) {
        this.cellSize = cellSize;
    }

    private int getCell(int x) {
        return x / cellSize;
    }

    /** Aggiunge una nota nella cella corretta */
    public void add(GraphicalObject n) {
        int cell = getCell(n.getX());
        ArrayList<GraphicalObject> list = grid.get(cell);

        if (list == null) {
            list = new ArrayList<GraphicalObject>();
            grid.put(cell, list);
        }

        list.add(n);
    }

    /** Rimuove la nota dalla cella corretta */
    public void remove(GraphicalObject n) {
        int cell = getCell(n.getX());
        ArrayList<GraphicalObject> list = grid.get(cell);

        if (list != null) {
            list.remove(n);
            if (list.isEmpty()) {
                grid.remove(cell);
            }
        }
    }

    /** Aggiorna la cella quando la nota si sposta */
    public void updatePosition(GraphicalObject n, int oldX, int newX) {
        int oldCell = getCell(oldX);
        int newCell = getCell(newX);

        if (oldCell == newCell)
            return;

        // Rimuovi dalla cella vecchia
        ArrayList<GraphicalObject> oldList = grid.get(oldCell);
        if (oldList != null) {
            oldList.remove(n);
            if (oldList.isEmpty()) {
                grid.remove(oldCell);
            }
        }

        // Aggiungi alla cella nuova
        ArrayList<GraphicalObject> newList = grid.get(newCell);
        if (newList == null) {
            newList = new ArrayList<GraphicalObject>();
            grid.put(newCell, newList);
        }

        newList.add(n);
    }

    /** Restituisce le note nella cella vicina (cella, cella-1, cella+1) */
    public List<GraphicalObject> getNearby(int x) {
        int cell = getCell(x);

        ArrayList<GraphicalObject> result = new ArrayList<GraphicalObject>();

        for (int c = cell - 1; c <= cell + 1; c++) {
            ArrayList<GraphicalObject> list = grid.get(c);
            if (list != null) {
                result.addAll(list);
            }
        }

        return result;
    }
}
