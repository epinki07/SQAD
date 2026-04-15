package sistema.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import sistema.excepciones.ExcepcionDatoCsvInvalido;
import sistema.excepciones.ExcepcionTemperaturaInvalida;

public class ProductoCongelado extends Producto {
    private double temperaturaActual;
    private double temperaturaMinimaPermitida;

    public ProductoCongelado(String id, String nombre, double precio, LocalDate fechaCaducidad,
            String lote, String ubicacion, double temperaturaActual, double temperaturaMinimaPermitida)
            throws ExcepcionTemperaturaInvalida {
        super(id, nombre, precio, fechaCaducidad, lote, ubicacion);
        if (temperaturaActual < -80 || temperaturaActual > 10)
            throw new ExcepcionTemperaturaInvalida("La temperatura del congelado debe ir de -80 a 10 C.");
        if (temperaturaMinimaPermitida < -40 || temperaturaMinimaPermitida > -5)
            throw new ExcepcionTemperaturaInvalida("La temperatura minima debe ir de -40 a -5 C.");
        this.temperaturaActual = temperaturaActual;
        this.temperaturaMinimaPermitida = temperaturaMinimaPermitida;
    }

    public double getTemperaturaActual() { return temperaturaActual; }
    public double getTemperaturaMinimaPermitida() { return temperaturaMinimaPermitida; }

    public void setTemperaturaActual(double t) throws ExcepcionTemperaturaInvalida {
        if (t < -80 || t > 10)
            throw new ExcepcionTemperaturaInvalida("La temperatura del congelado debe ir de -80 a 10 C.");
        this.temperaturaActual = t;
    }

    @Override
    public EstadoCalidad evaluarEstado() {
        if (estaCaducado()) return EstadoCalidad.CADUCADO;

        double exceso = temperaturaActual - temperaturaMinimaPermitida;
        if (exceso > 5) return EstadoCalidad.CRITICO;
        if (exceso > 0 || estaProximoACaducar(2)) return EstadoCalidad.EN_RIESGO;
        return EstadoCalidad.OPTIMO;
    }

    @Override
    public TipoProducto getTipoProducto() {
        return TipoProducto.CONGELADO;
    }

    @Override
    public Alerta generarAlerta() {
        EstadoCalidad estado = evaluarEstado();
        if (estado == EstadoCalidad.OPTIMO) return null;

        String msg = mensajeBaseAlerta(estado);
        if (estado != EstadoCalidad.CADUCADO && temperaturaActual > temperaturaMinimaPermitida)
            msg += "Temp actual " + temperaturaActual + " C, minimo " + temperaturaMinimaPermitida + " C.";

        return new Alerta("AL-" + getId(), msg.trim(), calcularNivelAlerta(estado), LocalDate.now(), getId());
    }

    @Override
    public String toCSV() {
        return "CONGELADO," + getId() + "," + getNombre() + "," + getPrecio() + ","
                + getFechaCaducidad() + "," + getLote() + "," + getUbicacion() + ","
                + temperaturaActual + "," + temperaturaMinimaPermitida;
    }

    public static ProductoCongelado fromCSV(String linea)
            throws ExcepcionDatoCsvInvalido, ExcepcionTemperaturaInvalida {
        String[] partes = linea.split(",", -1);
        if (partes.length != 9)
            throw new ExcepcionDatoCsvInvalido("Linea CONGELADO mal formada, se esperaban 9 campos y habia " + partes.length + ".");
        try {
            return new ProductoCongelado(
                partes[1].trim(), partes[2].trim(),
                Double.parseDouble(partes[3].trim()),
                LocalDate.parse(partes[4].trim()),
                partes[5].trim(), partes[6].trim(),
                Double.parseDouble(partes[7].trim()),
                Double.parseDouble(partes[8].trim())
            );
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ExcepcionDatoCsvInvalido("Dato invalido en CONGELADO: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Congelado [" + getId() + "] " + getNombre()
                + " | precio: " + getPrecio()
                + " | vence: " + getFechaCaducidad()
                + " | estado: " + evaluarEstado()
                + " | temp: " + temperaturaActual + " C (min " + temperaturaMinimaPermitida + " C)";
    }
}
