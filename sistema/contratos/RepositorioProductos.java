package sistema.contratos;

import java.io.IOException;
import java.util.List;

import sistema.modelo.Producto;

public interface RepositorioProductos {
    List<Producto> cargar() throws IOException;

    void guardar(List<Producto> productos) throws IOException;

    List<String> getAvisosUltimaCarga();
}
