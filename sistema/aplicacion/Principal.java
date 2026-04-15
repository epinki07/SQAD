package sistema.aplicacion;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import sistema.excepciones.ExcepcionHumedadInvalida;
import sistema.excepciones.ExcepcionProductoDuplicado;
import sistema.excepciones.ExcepcionProductoNoEncontrado;
import sistema.excepciones.ExcepcionTemperaturaInvalida;
import sistema.servicio.Inventario;
import sistema.modelo.Alerta;
import sistema.modelo.Producto;
import sistema.modelo.ProductoCongelado;
import sistema.modelo.ProductoRefrigerado;
import sistema.modelo.ProductoSeco;
import sistema.modelo.TipoProducto;
import sistema.persistencia.ArchivoProductos;

public class Principal {
    private static final String ARCHIVO_DATOS = "productos.csv";
    private static final Inventario inventario = new Inventario(new ArchivoProductos(ARCHIVO_DATOS));
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        cargarArchivo();

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = teclado.nextLine().trim();

            switch (opcion) {
                case "1":
                    agregarProducto();
                    break;
                case "2":
                    buscarProducto();
                    break;
                case "3":
                    filtrarPorTipo();
                    break;
                case "4":
                    verProductosEnRiesgo();
                    break;
                case "5":
                    eliminarProducto();
                    break;
                case "6":
                    verTodo();
                    break;
                case "7":
                    guardarCambios();
                    break;
                case "8":
                    guardarCambios();
                    continuar = false;
                    System.out.println("Programa cerrado.");
                    break;
                default:
                    System.out.println("Esa opcion no existe. Elige una del 1 al 8.");
                    break;
            }
        }

        teclado.close();
    }

    private static void cargarArchivo() {
        try {
            inventario.cargarDesdePersistencia();
            System.out.println("Archivo cargado. Hay " + inventario.listarProductos().size() + " producto(s).");

            List<String> avisos = inventario.getAvisosCarga();
            if (!avisos.isEmpty()) {
                System.out.println("No se cargaron estas lineas:");
                for (String aviso : avisos) {
                    System.out.println("- " + aviso);
                }
            }
        } catch (IOException e) {
            System.out.println("No pude abrir el archivo. Empiezo con una lista vacia. " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== Control de calidad de productos =====");
        System.out.println("1. Agregar producto");
        System.out.println("2. Buscar producto por id");
        System.out.println("3. Ver productos por tipo");
        System.out.println("4. Ver productos en riesgo");
        System.out.println("5. Eliminar producto");
        System.out.println("6. Mostrar todos los productos");
        System.out.println("7. Guardar datos");
        System.out.println("8. Salir");
        System.out.print("Elige una opcion: ");
    }

    private static void agregarProducto() {
        try {
            TipoProducto tipo = pedirTipo();
            String id = pedirTexto("Id: ");
            String nombre = pedirTexto("Nombre: ");
            double precio = pedirNumero("Precio: ");
            LocalDate fechaCaducidad = pedirFecha("Fecha de caducidad (AAAA-MM-DD): ");
            String lote = pedirTexto("Lote: ");
            String ubicacion = pedirTexto("Ubicacion: ");

            Producto nuevo = crearProducto(tipo, id, nombre, precio, fechaCaducidad, lote, ubicacion);
            inventario.agregarProducto(nuevo);
            inventario.guardarCambios();

            System.out.println("Producto guardado:");
            System.out.println(nuevo);
            mostrarAlerta(nuevo.generarAlerta());
        } catch (ExcepcionProductoDuplicado e) {
            System.out.println("No pude guardar el producto: " + e.getMessage());
        } catch (ExcepcionTemperaturaInvalida e) {
            System.out.println("Revisa la temperatura: " + e.getMessage());
        } catch (ExcepcionHumedadInvalida e) {
            System.out.println("Revisa la humedad: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ese numero no es valido.");
        } catch (DateTimeParseException e) {
            System.out.println("La fecha debe ir como AAAA-MM-DD.");
        } catch (IllegalArgumentException e) {
            mostrarErrorDato(e);
        } catch (IOException e) {
            System.out.println("El producto quedo en memoria, pero no pude guardar el archivo: " + e.getMessage());
        }
    }

    private static void buscarProducto() {
        try {
            String id = pedirTexto("Escribe el id del producto: ");
            Producto producto = inventario.buscarPorId(id);
            System.out.println("Producto encontrado:");
            System.out.println(producto);
        } catch (ExcepcionProductoNoEncontrado e) {
            System.out.println("No encontre un producto con ese id: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarErrorDato(e);
        }
    }

    private static void filtrarPorTipo() {
        try {
            TipoProducto tipo = pedirTipo();
            List<Producto> lista = inventario.filtrarPorTipo(tipo);
            if (lista.isEmpty()) {
                System.out.println("No tengo productos de tipo " + tipo.getNombreVisible() + ".");
                return;
            }

            System.out.println("Lista de productos tipo " + tipo.getNombreVisible() + ":");
            imprimirLista(lista);
        } catch (IllegalArgumentException e) {
            mostrarErrorDato(e);
        }
    }

    private static void verProductosEnRiesgo() {
        List<Producto> lista = inventario.filtrarEnRiesgo();
        if (lista.isEmpty()) {
            System.out.println("Por ahora no hay productos en riesgo.");
            return;
        }

        System.out.println("Productos en riesgo:");
        imprimirLista(lista);
        mostrarAlertas(inventario.listarAlertas());
    }

    private static void eliminarProducto() {
        try {
            String id = pedirTexto("Escribe el id que quieres borrar: ");
            inventario.eliminarProducto(id);
            inventario.guardarCambios();
            System.out.println("Producto eliminado.");
        } catch (ExcepcionProductoNoEncontrado e) {
            System.out.println("No pude borrar el producto: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarErrorDato(e);
        } catch (IOException e) {
            System.out.println("Se borro de la lista, pero no pude guardar el archivo: " + e.getMessage());
        }
    }

    private static void verTodo() {
        Iterator<Producto> recorrido = inventario.iterator();
        if (!recorrido.hasNext()) {
            System.out.println("No hay productos guardados.");
            return;
        }

        System.out.println("Estos son todos los productos:");
        while (recorrido.hasNext()) {
            System.out.println("- " + recorrido.next());
        }
    }

    private static void guardarCambios() {
        try {
            inventario.guardarCambios();
            System.out.println("Cambios guardados en " + ARCHIVO_DATOS + ".");
        } catch (IOException e) {
            System.out.println("No pude guardar los datos: " + e.getMessage());
        }
    }

    private static Producto crearProducto(TipoProducto tipo, String id, String nombre, double precio,
            LocalDate fechaCaducidad, String lote, String ubicacion)
            throws ExcepcionTemperaturaInvalida, ExcepcionHumedadInvalida {
        switch (tipo) {
            case REFRIGERADO:
                return new ProductoRefrigerado(id, nombre, precio, fechaCaducidad, lote, ubicacion,
                        pedirNumero("Temperatura actual (C): "), pedirNumero("Temperatura maxima permitida (C): "));
            case CONGELADO:
                return new ProductoCongelado(id, nombre, precio, fechaCaducidad, lote, ubicacion,
                        pedirNumero("Temperatura actual (C): "), pedirNumero("Temperatura minima permitida (C): "));
            case SECO:
                return new ProductoSeco(id, nombre, precio, fechaCaducidad, lote, ubicacion,
                        pedirNumero("Humedad actual (%): "), pedirNumero("Humedad maxima permitida (%): "));
            default:
                throw new IllegalArgumentException("Ese tipo no existe.");
        }
    }

    private static TipoProducto pedirTipo() {
        System.out.println("Tipos:");
        System.out.println("1. Refrigerado");
        System.out.println("2. Congelado");
        System.out.println("3. Seco");
        System.out.print("Elige el tipo: ");
        String opcion = teclado.nextLine().trim();

        switch (opcion) {
            case "1":
                return TipoProducto.REFRIGERADO;
            case "2":
                return TipoProducto.CONGELADO;
            case "3":
                return TipoProducto.SECO;
            default:
                throw new IllegalArgumentException("Ese tipo no existe.");
        }
    }

    private static void imprimirLista(List<Producto> lista) {
        for (Producto producto : lista) {
            System.out.println("- " + producto);
        }
    }

    private static void mostrarAlerta(Alerta alerta) {
        if (alerta != null) {
            System.out.println("Alerta: " + alerta.getMensaje());
        }
    }

    private static void mostrarAlertas(List<Alerta> alertas) {
        if (alertas.isEmpty()) {
            return;
        }

        System.out.println("Alertas:");
        for (Alerta alerta : alertas) {
            System.out.println("- " + alerta);
        }
    }

    private static void mostrarErrorDato(IllegalArgumentException e) {
        System.out.println("Revisa el dato: " + e.getMessage());
    }

    private static String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    private static double pedirNumero(String mensaje) {
        System.out.print(mensaje);
        return Double.parseDouble(teclado.nextLine().trim());
    }

    private static LocalDate pedirFecha(String mensaje) {
        System.out.print(mensaje);
        return LocalDate.parse(teclado.nextLine().trim());
    }
}
