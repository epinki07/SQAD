package sistema.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import sistema.excepciones.ExcepcionDatoCsvInvalido;
import sistema.excepciones.ExcepcionHumedadInvalida;

public class ProductoSeco extends Producto {
    private double humedadActual;
    private double humedadMaximaPermitida;

    public ProductoSeco(String id, String nombre, double precio, LocalDate fechaCaducidad,
            String lote, String ubicacion, double humedadActual, double humedadMaximaPermitida)
            throws ExcepcionHumedadInvalida {
        super(id, nombre, precio, fechaCaducidad, lote, ubicacion);
        if (humedadActual < 0 || humedadActual > 100)
            throw new ExcepcionHumedadInvalida("La humedad actual tiene que estar entre 0 y 100%.");
        if (humedadMaximaPermitida <= 0 || humedadMaximaPermitida > 100)
            throw new ExcepcionHumedadInvalida("La humedad maxima tiene que ser mayor que 0 y hasta 100%.");
        this.humedadActual = humedadActual;
        this.humedadMaximaPermitida = humedadMaximaPermitida;
    }

    public double getHumedadActual() { return humedadActual; }
    public double getHumedadMaximaPermitida() { return humedadMaximaPermitida; }

    public void setHumedadActual(double h) throws ExcepcionHumedadInvalida {
        if (h < 0 || h > 100)
            throw new ExcepcionHumedadInvalida("La humedad actual tiene que estar entre 0 y 100%.");
        this.humedadActual = h;
    }

    @Override
    public EstadoCalidad evaluarEstado() {
        if (estaCaducado()) return EstadoCalidad.CADUCADO;

        double exceso = humedadActual - humedadMaximaPermitida;
        if (exceso > 10) return EstadoCalidad.CRITICO;
        if (exceso > 0 || estaProximoACaducar(2)) return EstadoCalidad.EN_RIESGO;
        return EstadoCalidad.OPTIMO;
    }

    @Override
    public TipoProducto getTipoProducto() {
        return TipoProducto.SECO;
    }

    @Override
    public Alerta generarAlerta() {
        EstadoCalidad estado = evaluarEstado();
        if (estado == EstadoCalidad.OPTIMO) return null;

        String msg = mensajeBaseAlerta(estado);
        if (estado != EstadoCalidad.CADUCADO && humedadActual > humedadMaximaPermitida)
            msg += "Humedad actual " + humedadActual + "%, maximo " + humedadMaximaPermitida + "%.";

        return new Alerta("AL-" + getId(), msg.trim(), calcularNivelAlerta(estado), LocalDate.now(), getId());
    }

    @Override
    public String toCSV() {
        return "SECO," + getId() + "," + getNombre() + "," + getPrecio() + ","
                + getFechaCaducidad() + "," + getLote() + "," + getUbicacion() + ","
                + humedadActual + "," + humedadMaximaPermitida;
    }

    public static ProductoSeco fromCSV(String linea)
            throws ExcepcionDatoCsvInvalido, ExcepcionHumedadInvalida {
        String[] partes = linea.split(",", -1);
        if (partes.length != 9)
            throw new ExcepcionDatoCsvInvalido("Linea SECO mal formada, se esperaban 9 campos y habia " + partes.length + ".");
        try {
            return new ProductoSeco(
                partes[1].trim(), partes[2].trim(),
                Double.parseDouble(partes[3].trim()),
                LocalDate.parse(partes[4].trim()),
                partes[5].trim(), partes[6].trim(),
                Double.parseDouble(partes[7].trim()),
                Double.parseDouble(partes[8].trim())
            );
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ExcepcionDatoCsvInvalido("Dato invalido en SECO: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Seco [" + getId() + "] " + getNombre()
                + " | precio: " + getPrecio()
                + " | vence: " + getFechaCaducidad()
                + " | estado: " + evaluarEstado()
                + " | humedad: " + humedadActual + "% (max " + humedadMaximaPermitida + "%)";
    }
}
