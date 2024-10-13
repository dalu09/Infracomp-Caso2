import java.util.ArrayList;
import java.util.HashMap;

public class PageTable {
    private HashMap<Integer, Integer> pagesTable = new HashMap<>();
    private ArrayList<Integer> frames = new ArrayList<>();
    private ArrayList<Integer> counters = new ArrayList<>();
    private int maxFrames;

    public PageTable(int maxFrames) {
        this.maxFrames = maxFrames;
    }

    // Carga una página en la memoria
    public boolean loadPage(int page) {
        if (pagesTable.containsKey(page)) {
            // Si la página ya está en la memoria, es un hit
            System.out.println("Hit: Página " + page);
            return true;
        } else {
            // Si no está, es un fallo de página
            System.out.println("Falló de página: " + page);
            if (frames.size() < maxFrames) { // Si hay espacio en los marcos
                frames.add(page);
                pagesTable.put(page, frames.size() - 1);  // Mapeamos la página al marco
            } else {
                replacePage(page); // Reemplaza una página si no hay espacio
            }
            return false;
        }
    }

    // Reemplaza una página en la memoria usando el algoritmo NRU
    public void replacePage(int page) {
        int paginaReemplazar = selectPageToReplace();
        System.out.println("Reemplazando página " + paginaReemplazar + " con " + page);
        frames.set(frames.indexOf(paginaReemplazar), page); // Reemplaza la página en el marco
        pagesTable.put(page, pagesTable.get(paginaReemplazar));  // Mapea la nueva página
        pagesTable.remove(paginaReemplazar);  // Elimina la vieja página
    }

    // Método que clasifica las páginas en categorías para NRU
    public void categorizePages() {
        // Implementación del algoritmo NRU para clasificar las páginas
    }

    // Selecciona la página a reemplazar en la memoria
    public int selectPageToReplace() {
        return frames.get(0); // Por ahora, devuelve la primera página en memoria (modificar según NRU)
    }
}
