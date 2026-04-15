package sistema.servicio;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import sistema.modelo.Producto;

public class IteradorInventario implements Iterator<Producto> {
    private final List<Producto> productos;
    private int pos;

    public IteradorInventario(List<Producto> productos) {
        this.productos = productos;
        this.pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < productos.size();
    }

    @Override
    public Producto next() {
        if (!hasNext()) throw new NoSuchElementException("No hay mas productos.");
        return productos.get(pos++);
    }
}
