package sistema.servicio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sistema.contratos.RepositorioProductos;
import sistema.excepciones.ExcepcionProductoDuplicado;
import sistema.excepciones.ExcepcionProductoNoEncontrado;
import sistema.modelo.Alerta;
import sistema.modelo.EstadoCalidad;
import sistema.modelo.Producto;
import sistema.modelo.TipoProducto;

public class Inventario implements Iterable<Producto> {
    private List<Producto> productos = new ArrayList<>();
    private RepositorioProductos repositorio;
    private List<String> avisosCarga = new ArrayList<>();

    public Inventario(RepositorioProductos repositorio) {
        this.repositorio = repositorio;
    }

    public void cargarDesdePersistencia() throws IOException {
        productos.clear();
        productos.addAll(repositorio.cargar());
        avisosCarga = repositorio.getAvisosUltimaCarga();
    }

    public void guardarCambios() throws IOException {
        repositorio.guardar(productos);
    }

    public void agregarProducto(Producto producto) throws ExcepcionProductoDuplicado {
        if (producto == null)
            throw new IllegalArgumentException("Falta el producto.");
        for (Producto p : productos) {
            if (p.getId().equalsIgnoreCase(producto.getId()))
                throw new ExcepcionProductoDuplicado("Ya habia un producto con ese id: " + producto.getId());
        }
        productos.add(producto);
    }

    public Producto buscarPorId(String id) throws ExcepcionProductoNoEncontrado {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Escribe el id.");
        for (Producto p : productos) {
            if (p.getId().equalsIgnoreCase(id.trim()))
                return p;
        }
        throw new ExcepcionProductoNoEncontrado("No encontre un producto con id " + id.trim() + ".");
    }

    public void eliminarProducto(String id) throws ExcepcionProductoNoEncontrado {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Escribe el id.");
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equalsIgnoreCase(id.trim())) {
                productos.remove(i);
                return;
            }
        }
        throw new ExcepcionProductoNoEncontrado("No encontre el producto " + id.trim() + ".");
    }

    public List<Producto> filtrarPorTipo(TipoProducto tipo) {
        List<Producto> encontrados = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getTipoProducto() == tipo)
                encontrados.add(p);
        }
        return encontrados;
    }

    public List<Producto> filtrarEnRiesgo() {
        List<Producto> enRiesgo = new ArrayList<>();
        for (Producto p : productos)
            if (p.evaluarEstado() != EstadoCalidad.OPTIMO) enRiesgo.add(p);
        return enRiesgo;
    }

    public List<Alerta> listarAlertas() {
        List<Alerta> alertas = new ArrayList<>();
        for (Producto p : productos) {
            Alerta alerta = p.generarAlerta();
            if (alerta != null) alertas.add(alerta);
        }
        return alertas;
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public List<String> getAvisosCarga() {
        return avisosCarga;
    }

    @Override
    public Iterator<Producto> iterator() {
        return new IteradorInventario(productos);
    }
}
