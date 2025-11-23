package scoreWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialGrid {

    private final int cellSize; // es: 20 px
    private final Map<Integer, ArrayList<GraphicalNote>> grid = new HashMap<Integer, ArrayList<GraphicalNote>>();

    public SpatialGrid(int cellSize) {
        this.cellSize = cellSize;
    }

    private int getCell(int x) {
        return x / cellSize;
    }

    /** Aggiunge una nota nella cella corretta */
    public void add(GraphicalNote n) {
        int cell = getCell(n.getX());
        ArrayList<GraphicalNote> list = grid.get(cell);

        if (list == null) {
            list = new ArrayList<GraphicalNote>();
            grid.put(cell, list);
        }

        list.add(n);
    }

    /** Rimuove la nota dalla cella corretta */
    public void remove(GraphicalNote n) {
        int cell = getCell(n.getX());
        ArrayList<GraphicalNote> list = grid.get(cell);

        if (list != null) {
            list.remove(n);
            if (list.isEmpty()) {
                grid.remove(cell);
            }
        }
    }

    /** Aggiorna la cella quando la nota si sposta */
    public void updatePosition(GraphicalNote n, int oldX, int newX) {
        int oldCell = getCell(oldX);
        int newCell = getCell(newX);

        if (oldCell == newCell)
            return;

        // Rimuovi dalla cella vecchia
        ArrayList<GraphicalNote> oldList = grid.get(oldCell);
        if (oldList != null) {
            oldList.remove(n);
            if (oldList.isEmpty()) {
                grid.remove(oldCell);
            }
        }

        // Aggiungi alla cella nuova
        ArrayList<GraphicalNote> newList = grid.get(newCell);
        if (newList == null) {
            newList = new ArrayList<GraphicalNote>();
            grid.put(newCell, newList);
        }

        newList.add(n);
    }

    /** Restituisce le note nella cella vicina (cella, cella-1, cella+1) */
    public List<GraphicalNote> getNearby(int x) {
        int cell = getCell(x);

        ArrayList<GraphicalNote> result = new ArrayList<GraphicalNote>();

        for (int c = cell - 1; c <= cell + 1; c++) {
            ArrayList<GraphicalNote> list = grid.get(c);
            if (list != null) {
                result.addAll(list);
            }
        }

        return result;
    }
}
