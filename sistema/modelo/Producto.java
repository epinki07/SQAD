package sistema.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Producto {
    private String id;
    private String nombre;
    private double precio;
    private LocalDate fechaCaducidad;
    private String lote;
    private String ubicacion;

    protected Producto(String id, String nombre, double precio,
            LocalDate fechaCaducidad, String lote, String ubicacion) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Falta el id.");
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Falta el nombre.");
        if (precio <= 0)
            throw new IllegalArgumentException("El precio tiene que ser mayor a 0.");
        if (fechaCaducidad == null)
            throw new IllegalArgumentException("Falta la fecha de caducidad.");
        if (lote == null || lote.trim().isEmpty())
            throw new IllegalArgumentException("Falta el lote.");
        if (ubicacion == null || ubicacion.trim().isEmpty())
            throw new IllegalArgumentException("Falta la ubicacion.");

        this.id = id.trim();
        this.nombre = nombre.trim();
        this.precio = precio;
        this.fechaCaducidad = fechaCaducidad;
        this.lote = lote.trim();
        this.ubicacion = ubicacion.trim();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public String getLote() { return lote; }
    public String getUbicacion() { return ubicacion; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Falta el nombre.");
        this.nombre = nombre.trim();
    }

    public void setPrecio(double precio) {
        if (precio <= 0)
            throw new IllegalArgumentException("El precio tiene que ser mayor a 0.");
        this.precio = precio;
    }

    public long diasParaCaducar() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);
    }

    public boolean estaCaducado() {
        return LocalDate.now().isAfter(fechaCaducidad);
    }

    protected boolean estaProximoACaducar(int dias) {
        return !estaCaducado() && diasParaCaducar() <= dias;
    }

    public abstract EstadoCalidad evaluarEstado();

    public abstract TipoProducto getTipoProducto();

    public abstract Alerta generarAlerta();

    public abstract String toCSV();

    protected String mensajeBaseAlerta(EstadoCalidad estado) {
        String aviso = "Revisar " + getNombre() + " (" + getId() + "). ";
        if (estado == EstadoCalidad.CADUCADO)
            return aviso + "Caduco el " + getFechaCaducidad() + ".";
        if (estaProximoACaducar(2))
            aviso += "Caduca pronto (" + getFechaCaducidad() + "). ";
        return aviso;
    }

    protected NivelAlerta calcularNivelAlerta(EstadoCalidad estado) {
        if (estado == EstadoCalidad.EN_RIESGO) return NivelAlerta.MEDIA;
        if (estado == EstadoCalidad.CRITICO) return NivelAlerta.ALTA;
        if (estado == EstadoCalidad.CADUCADO) return NivelAlerta.CRITICA;
        throw new IllegalStateException("No se puede calcular nivel para estado: " + estado);
    }
}
