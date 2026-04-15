package sistema.modelo;

import java.time.LocalDate;

public class Alerta {
    private final String id;
    private final String mensaje;
    private final NivelAlerta nivel;
    private final LocalDate fecha;
    private final String productoId;

    public Alerta(String id, String mensaje, NivelAlerta nivel, LocalDate fecha, String idProducto) {
        this.id = validarTexto(id, "id");
        this.mensaje = validarTexto(mensaje, "mensaje");
        if (nivel == null) throw new IllegalArgumentException("Falta el nivel de la alerta.");
        this.nivel = nivel;
        if (fecha == null) throw new IllegalArgumentException("Falta la fecha.");
        this.fecha = fecha;
        this.productoId = validarTexto(idProducto, "producto");
    }

    public String getId() { return id; }
    public String getMensaje() { return mensaje; }
    public NivelAlerta getNivel() { return nivel; }
    public LocalDate getFecha() { return fecha; }
    public String getProductoId() { return productoId; }

    @Override
    public String toString() {
        return "Alerta: id=" + id + ", nivel=" + nivel + ", fecha=" + fecha + ", producto=" + productoId
                + ", mensaje=" + mensaje;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException("Falta " + campo + ".");
        if (valor.contains(","))
            throw new IllegalArgumentException(campo + " no puede llevar comas.");
        return valor.trim();
    }
}
