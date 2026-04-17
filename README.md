# SQAD — Sistema de Control de Calidad para Almacenes de Alimentos

Sistema de gestion de inventarios de alimentos desarrollado en Java con arquitectura por capas. Gestiona productos refrigerados, congelados y secos, y emite alertas automaticas cuando los parametros de temperatura, humedad o caducidad estan fuera de rango.

## Por que existe

Mientras trabajaba en Food Sense durante IFTP2026, el problema del control de alimentos me parecia mas amplio de lo que la competencia pedia. Construi SQAD por cuenta propia para llevar esa logica hasta un sistema real con capas, persistencia y reglas de negocio propias. Nadie me lo pidio; queria ver hasta donde llegaba.

## Que hace

- Clasifica productos por categoria (refrigerados, congelados, secos)
- Controla temperatura, humedad y fechas de caducidad por categoria
- Emite alertas cuando algun parametro sale del rango definido
- Persiste los datos en CSV sin base de datos externa

## Parametros por categoria

| Categoria | Temperatura | Humedad |
|-----------|-------------|---------|
| Refrigerados | 0 a 5 C | 40-60% |
| Congelados | -18 C o menos | 50-70% |
| Secos | 10 a 21 C | 30-50% |

## Como correrlo

```bash
git clone https://github.com/epinki07/SQAD.git
cd SQAD
javac -d out src/**/*.java
java -cp out com.sqad.Main
```

## Estructura

```
SQAD/
├── src/
│   └── com.sqad/
│       ├── Main.java
│       ├── model/
│       ├── repository/
│       ├── service/
│       └── util/
└── data/
    └── productos.csv
```

## Tech Stack

Java 11+, arquitectura por capas (presentacion, servicio, repositorio), persistencia en CSV.

## Autor

Diego Ramirez Magana — [LinkedIn](https://www.linkedin.com/in/diego-ramirez-maga%C3%B1a-b15022298/) | [GitHub](https://github.com/epinki07) | dramirezmagana@gmail.com
