# 🏭 SQAD - Sistema de Control de Calidad para Almacenes de Alimentos

> **Proyecto de iniciativa propia** · Desarrollado en Java con arquitectura por capas

Sistema de control de calidad para gestión de inventarios de alimentos en almacenes. SQAD gestiona productos refrigerados, congelados y secos, emitiendo alertas automáticas por temperatura, humedad y caducidad.

## 🎯 ¿Por qué existe este proyecto?

Durante mi participación en **Invent for the Planet 2026** con el proyecto Food Sense (monitoreo IoT de refrigeradores), identifiqué la oportunidad de llevar lo aprendido un paso más allá. Desarrollé SQAD por iniciativa propia para:

- Profundizar en arquitectura de software empresarial
- Aplicar patrones de diseño en un sistema real
- Demostrar capacidad de auto-aprendizaje y proactividad

## 📋 ¿Qué hace?

- **Gestión por categorías**: Refrigerados, congelados y productos secos
- **Control de parámetros**: Temperatura, humedad y fechas de caducidad
- **Alertas automáticas**: Notificaciones cuando productos están en riesgo
- **Persistencia CSV**: Almacenamiento ligero y portable de datos
- **Arquitectura por capas**: Separación clara de responsabilidades

## 🛠️ Tech Stack

| Lenguaje | Arquitectura | Persistencia |
|----------|--------------|--------------|
| Java 11+ | Capas (Layers) | CSV |
| POO | MVC Pattern | File I/O |
| | Service Layer | |

## 🚀 Cómo correrlo localmente

### Prerrequisitos

```bash
java --version  # JDK 11 o superior
```

### Instalación

```bash
# Clonar repositorio
git clone https://github.com/epinki07/SQAD.git
cd SQAD

# Compilar proyecto
javac -d out src/**/*.java

# Ejecutar aplicación
java -cp out com.sqad.Main
```

### Opción con IDE

```bash
# Abrir en IntelliJ IDEA, Eclipse o VS Code
# Configurar JDK 11+
# Ejecutar clase principal: com.sqad.Main
```

## 📁 Estructura del proyecto

```
SQAD/
├── src/
│   ├── com.sqad/
│   │   ├── Main.java           # Punto de entrada
│   │   ├── model/              # Entidades
│   │   │   ├── Producto.java
│   │   │   ├── Categoria.java
│   │   │   └── Alerta.java
│   │   ├── repository/         # Acceso a datos
│   │   │   └── ProductoRepository.java
│   │   ├── service/            # Lógica de negocio
│   │   │   ├── ControlCalidadService.java
│   │   │   └── AlertaService.java
│   │   └── util/               # Helpers
│   │       └── CSVUtil.java
│   └── data/
│       └── productos.csv       # Datos persistidos
```

## 📊 Características técnicas

### Arquitectura por capas

```
┌─────────────────────────────────────┐
│         Presentación (Main)         │
├─────────────────────────────────────┤
│         Servicio (Service)          │
├─────────────────────────────────────┤
│         Repositorio (Data)          │
├─────────────────────────────────────┤
│         Persistencia (CSV)          │
└─────────────────────────────────────┘
```

### Parámetros monitoreados

| Categoría | Temp. Mínima | Temp. Máxima | Humedad Ideal |
|-----------|--------------|--------------|---------------|
| Refrigerados | 0°C | 5°C | 40-60% |
| Congelados | -18°C o menos | -15°C | 50-70% |
| Secos | 10°C | 21°C | 30-50% |

## 💡 Qué aprendí

- **Arquitectura limpia**: Separación de responsabilidades por capas
- **Patrones de diseño**: Repository, Service, Factory
- **Gestión de archivos**: Lectura/escritura CSV en Java
- **Validaciones de negocio**: Reglas complejas por categoría de producto
- **Iniciativa propia**: Construir sin que me lo asignen

## 🔮 Mejoras futuras

- [ ] Migrar a base de datos SQLite/MySQL
- [ ] Interfaz gráfica con JavaFX
- [ ] Sistema de usuarios y roles
- [ ] Exportar reportes a PDF
- [ ] Dashboard con métricas de calidad

## 🤝 Autor

**Diego Ramirez Magaña**  
Estudiante de Ingeniería en Software y Negocios Digitales  
Tecnológico del Software

- 📧 dramirezmagana@gmail.com
- 🔗 [LinkedIn](https://www.linkedin.com/in/diego-ramirez-maga%C3%B1a-b15022298/)
- 🐙 [GitHub](https://github.com/epinki07)

---

> **Nota**: Este proyecto fue desarrollado por iniciativa propia como extensión técnica de lo aprendido durante Invent for the Planet 2026. Demuestra mi capacidad de auto-aprendizaje y proactividad.
