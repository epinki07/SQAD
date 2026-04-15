package sistema.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sistema.contratos.RepositorioProductos;
import sistema.excepciones.ExcepcionDatoCsvInvalido;
import sistema.excepciones.ExcepcionHumedadInvalida;
import sistema.excepciones.ExcepcionTemperaturaInvalida;
import sistema.modelo.Producto;
import sistema.modelo.ProductoCongelado;
import sistema.modelo.ProductoRefrigerado;
import sistema.modelo.ProductoSeco;

public class ArchivoProductos implements RepositorioProductos {

    private final String ruta;
    private List<String> avisos = new ArrayList<>();

    public ArchivoProductos(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Producto> cargar() throws IOException {
        List<Producto> lista = new ArrayList<>();
        avisos.clear();

        File archivo = new File(ruta);
        if (!archivo.exists()) return lista;

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numLinea++;
                if (linea.trim().isEmpty()) continue;
                if (numLinea == 1 && linea.toLowerCase().startsWith("tipoproducto")) continue;

                try {
                    lista.add(parsearLinea(linea));
                } catch (Exception e) {
                    avisos.add("Linea " + numLinea + " ignorada: " + e.getMessage());
                }
            }
        }

        return lista;
    }

    @Override
    public void guardar(List<Producto> productos) throws IOException {
        File archivo = new File(ruta);
        if (archivo.getParentFile() != null && !archivo.getParentFile().exists())
            archivo.getParentFile().mkdirs();

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            escritor.write("tipoProducto,id,nombre,precio,fechaCaducidad,lote,ubicacion,medicionActual,limiteReferencia");
            escritor.newLine();

            for (Producto p : productos) {
                escritor.write(p.toCSV());
                escritor.newLine();
            }
        }
    }

    @Override
    public List<String> getAvisosUltimaCarga() {
        return new ArrayList<>(avisos);
    }

    private Producto parsearLinea(String linea)
            throws ExcepcionDatoCsvInvalido, ExcepcionTemperaturaInvalida, ExcepcionHumedadInvalida {
        String tipo = linea.split(",", 2)[0].trim().toUpperCase();

        if (tipo.equals("REFRIGERADO")) return ProductoRefrigerado.fromCSV(linea);
        if (tipo.equals("CONGELADO"))   return ProductoCongelado.fromCSV(linea);
        if (tipo.equals("SECO"))        return ProductoSeco.fromCSV(linea);

        throw new ExcepcionDatoCsvInvalido("Tipo desconocido: " + tipo);
    }
}
