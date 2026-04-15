package sistema.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import sistema.excepciones.ExcepcionDatoCsvInvalido;
import sistema.excepciones.ExcepcionTemperaturaInvalida;

public class ProductoRefrigerado extends Producto {
    private double temperaturaActual;
    private double temperaturaMaximaPermitida;

    public ProductoRefrigerado(String id, String nombre, double precio, LocalDate fechaCaducidad,
            String lote, String ubicacion, double temperaturaActual, double temperaturaMaximaPermitida)
            throws ExcepcionTemperaturaInvalida {
        super(id, nombre, precio, fechaCaducidad, lote, ubicacion);
        if (temperaturaActual < -40 || temperaturaActual > 40)
            throw new ExcepcionTemperaturaInvalida("La temperatura del refrigerado debe ir de -40 a 40 C.");
        if (temperaturaMaximaPermitida < -15 || temperaturaMaximaPermitida > 20)
            throw new ExcepcionTemperaturaInvalida("La temperatura maxima permitida debe ir de -15 a 20 C.");
        this.temperaturaActual = temperaturaActual;
        this.temperaturaMaximaPermitida = temperaturaMaximaPermitida;
    }

    public double getTemperaturaActual() { return temperaturaActual; }
    public double getTemperaturaMaximaPermitida() { return temperaturaMaximaPermitida; }

    public void setTemperaturaActual(double t) throws ExcepcionTemperaturaInvalida {
        if (t < -40 || t > 40)
            throw new ExcepcionTemperaturaInvalida("La temperatura del refrigerado debe ir de -40 a 40 C.");
        this.temperaturaActual = t;
    }

    @Override
    public EstadoCalidad evaluarEstado() {
        if (estaCaducado()) return EstadoCalidad.CADUCADO;

        double exceso = temperaturaActual - temperaturaMaximaPermitida;
        if (exceso > 2) return EstadoCalidad.CRITICO;
        if (exceso > 0 || estaProximoACaducar(2)) return EstadoCalidad.EN_RIESGO;
        return EstadoCalidad.OPTIMO;
    }

    @Override
    public TipoProducto getTipoProducto() {
        return TipoProducto.REFRIGERADO;
    }

    @Override
    public Alerta generarAlerta() {
        EstadoCalidad estado = evaluarEstado();
        if (estado == EstadoCalidad.OPTIMO) return null;

        String msg = mensajeBaseAlerta(estado);
        if (estado != EstadoCalidad.CADUCADO && temperaturaActual > temperaturaMaximaPermitida)
            msg += "Temp actual " + temperaturaActual + " C, maximo " + temperaturaMaximaPermitida + " C.";

        return new Alerta("AL-" + getId(), msg.trim(), calcularNivelAlerta(estado), LocalDate.now(), getId());
    }

    @Override
    public String toCSV() {
        return "REFRIGERADO," + getId() + "," + getNombre() + "," + getPrecio() + ","
                + getFechaCaducidad() + "," + getLote() + "," + getUbicacion() + ","
                + temperaturaActual + "," + temperaturaMaximaPermitida;
    }

    public static ProductoRefrigerado fromCSV(String linea)
            throws ExcepcionDatoCsvInvalido, ExcepcionTemperaturaInvalida {
        String[] partes = linea.split(",", -1);
        if (partes.length != 9)
            throw new ExcepcionDatoCsvInvalido("Linea REFRIGERADO mal formada, se esperaban 9 campos y habia " + partes.length + ".");
        try {
            return new ProductoRefrigerado(
                partes[1].trim(), partes[2].trim(),
                Double.parseDouble(partes[3].trim()),
                LocalDate.parse(partes[4].trim()),
                partes[5].trim(), partes[6].trim(),
                Double.parseDouble(partes[7].trim()),
                Double.parseDouble(partes[8].trim())
            );
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ExcepcionDatoCsvInvalido("Dato invalido en REFRIGERADO: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Refrigerado [" + getId() + "] " + getNombre()
                + " | precio: " + getPrecio()
                + " | vence: " + getFechaCaducidad()
                + " | estado: " + evaluarEstado()
                + " | temp: " + temperaturaActual + " C (max " + temperaturaMaximaPermitida + " C)";
    }
}
