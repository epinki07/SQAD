package sistema.modelo;

public enum TipoProducto {
    REFRIGERADO("Refrigerado"),
    CONGELADO("Congelado"),
    SECO("Seco");

    private final String nombreVisible;

    TipoProducto(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public static TipoProducto fromTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Falta escribir el tipo de producto.");
        }

        String valor = texto.trim();
        for (TipoProducto tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("No ubico el tipo: " + texto);
    }
}
