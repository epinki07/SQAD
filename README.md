# SQAD

Sistema de control de calidad para almacenes de alimentos, desarrollado en Java con separacion por capas. Gestiona productos refrigerados, congelados y secos, persiste inventario en CSV y emite alertas cuando temperatura, humedad o caducidad salen de rango.

## Contexto

SQAD nacio como extension tecnica de Food Sense durante Invent for the Planet 2026. La idea fue llevar el problema de monitoreo de alimentos a un sistema de consola con reglas de negocio, persistencia y manejo de errores propio.

## Funcionalidades

- Alta, busqueda, listado y gestion de productos.
- Clasificacion por tipo de producto.
- Validacion de temperatura y humedad segun categoria.
- Alertas por riesgo de calidad y caducidad.
- Persistencia en `productos.csv`.
- Excepciones de dominio para datos invalidos, duplicados y productos inexistentes.

## Estructura

```text
SQAD/
├── sistema/
│   ├── aplicacion/       # Entrada de consola
│   ├── contratos/        # Interfaces de repositorio
│   ├── excepciones/      # Errores de dominio
│   ├── modelo/           # Entidades y enums
│   ├── persistencia/     # Lectura/escritura CSV
│   └── servicio/         # Inventario e iteradores
├── productos.csv
└── DOCUMENTO_TECNICO.md
```

## Ejecucion

```bash
javac -d out $(find sistema -name "*.java")
java -cp out sistema.aplicacion.Principal
```

## Stack

Java, POO, arquitectura por capas, persistencia CSV.

## Derechos

Codigo publicado para revision profesional. Sin licencia de reutilizacion; todos los derechos reservados.
