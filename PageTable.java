import java.util.ArrayList;
import java.util.HashMap;

public class PageTable {
    private HashMap<Integer, Integer> pagesTable = new HashMap<>();
    private ArrayList<Integer> frames = new ArrayList<>();
    private int maxFrames;

    public PageTable(int maxFrames) {
        this.maxFrames = maxFrames;
    }

    // Carga una página en la memoria
    public boolean loadPage(int page) {
        if (pagesTable.containsKey(page)) {
            System.out.println("Hit: Página " + page);
            return true;
        } else {
            System.out.println("Falló de página: " + page);
            if (frames.size() < maxFrames) {
                frames.add(page);
                pagesTable.put(page, frames.size() - 1);
            } else {
                replacePage(page);
            }
            return false;
        }
    }

    // Reemplaza una página en la memoria usando el algoritmo NRU
    public void replacePage(int page) {
        int paginaReemplazar = selectPageToReplace();
        System.out.println("Reemplazando página " + paginaReemplazar + " con " + page);
        frames.set(frames.indexOf(paginaReemplazar), page);
        pagesTable.put(page, pagesTable.get(paginaReemplazar));
        pagesTable.remove(paginaReemplazar);
    }

    // Clasifica las páginas para el algoritmo NRU
    public void categorizePages() {
        // Implementar NRU
    }

    public int selectPageToReplace() {
        return frames.get(0); 
    }
}
