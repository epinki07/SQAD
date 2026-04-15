# Sistema de Control de Calidad de Productos

## 1. Qué hace el sistema

Es un programa de consola en Java para llevar el registro de productos de un almacén de alimentos. Maneja tres tipos: refrigerados, congelados y secos. Para cada uno revisa si la temperatura (o humedad, en el caso de los secos) está dentro del rango permitido y si la fecha de caducidad sigue vigente. Si algo está fuera de rango, genera una alerta.

Lo que se puede hacer desde el menú:
- Agregar productos al inventario
- Buscar uno por su ID
- Ver todos o filtrar por tipo
- Ver cuáles tienen algún problema
- Eliminar un producto
- Guardar y cargar todo desde un archivo CSV para que nada se pierda al cerrar

---

## 2. Excepciones del sistema

| Excepción | Paquete | Cuándo se lanza |
|---|---|---|
| `ExcepcionTemperaturaInvalida` | `sistema.excepciones` | Temperatura fuera del rango permitido para ese tipo |
| `ExcepcionHumedadInvalida` | `sistema.excepciones` | Humedad del producto seco fuera del 0–100% |
| `ExcepcionProductoDuplicado` | `sistema.excepciones` | Ya existe un producto con ese ID |
| `ExcepcionProductoNoEncontrado` | `sistema.excepciones` | No se encontró el producto que se busca o elimina |
| `ExcepcionDatoCsvInvalido` | `sistema.excepciones` | Línea del CSV rota — campos de más, número mal escrito, fecha inválida, etc. |
| `IllegalArgumentException` | `java.lang` | Campo vacío o precio negativo |
| `IOException` | `java.io` | Problema al leer o escribir el archivo |

---

## 3. Cómo se guardan los datos

Todo va a un archivo CSV llamado `productos.csv` en la carpeta desde donde se ejecuta el programa.

### Formato

```
tipoProducto,id,nombre,precio,fechaCaducidad,lote,ubicacion,medicionActual,limiteReferencia
REFRIGERADO,P001,Leche,25.5,2025-06-01,LOTE-A,Pasillo-1,4.0,8.0
CONGELADO,P002,Pollo,89.9,2025-08-15,LOTE-B,Pasillo-2,-15.0,-18.0
SECO,P003,Arroz,18.0,2026-01-10,LOTE-C,Pasillo-3,30.0,60.0
```

**Las 9 columnas:**

| # | Campo | Qué contiene |
|---|---|---|
| 1 | `tipoProducto` | `REFRIGERADO`, `CONGELADO` o `SECO` |
| 2 | `id` | ID único del producto |
| 3 | `nombre` | Nombre del producto |
| 4 | `precio` | Precio |
| 5 | `fechaCaducidad` | Fecha en formato `AAAA-MM-DD` |
| 6 | `lote` | Número de lote |
| 7 | `ubicacion` | Dónde está en el almacén |
| 8 | `medicionActual` | Temperatura (°C) o humedad (%) según el tipo |
| 9 | `limiteReferencia` | El límite máximo o mínimo permitido |

El estado de calidad no se guarda porque se calcula en el momento con los datos actuales y la fecha de hoy. Guardarlo implicaría que podría quedar desactualizado.

Si alguna línea del CSV está mal formada, el programa la salta, sigue cargando el resto y le avisa al usuario qué líneas fallaron.

---

## 4. Por qué está organizado así

```
sistema/
├── modelo/        → Las clases de los productos y sus enums
├── servicio/      → El inventario y el iterador
├── persistencia/  → La lectura y escritura del CSV
├── contratos/     → La interfaz del repositorio
├── excepciones/   → Las excepciones propias del sistema
└── aplicacion/    → El main y el menú
```

| Decisión | Por qué |
|---|---|
| `Producto` es abstracta | No existe un "producto genérico" en este sistema. Refrigerado, congelado y seco son cosas distintas con reglas distintas. |
| `fromCSV()` en cada clase del modelo | Si lo pusiera en `ArchivoProductos`, esa clase tendría que conocer los campos internos de cada tipo. No le corresponde. |
| La interfaz `RepositorioProductos` | `Inventario` no tiene por qué saber que los datos vienen de un archivo. Si eso cambia, solo se toca la persistencia. |
| `Inventario` en `servicio` | No es un dato del dominio ni tampoco persiste nada. Es quien coordina operaciones sobre la lista. |
| `IteradorInventario` | Lo pedía el proyecto. Sirve para recorrer con for-each sin exponer la lista interna. |

---

## 5. Lo que costó trabajo

Lo del CSV fue lo que más me revolvió. Tenía el `fromCSV()` en `ArchivoProductos` y funcionaba, pero algo se sentía mal. Después de un rato caí en que persistencia estaba metiendo las manos en el modelo — sabía qué campos tiene cada tipo de producto y eso no le toca. Moverlo a cada clase fue más trabajo pero ya no había esa dependencia rara.

El iterador no lo vi difícil de escribir, sino de entender para qué sirve. Implementarlo fue básicamente llevar un contador, pero primero tuve que convencerme de que valía la pena en lugar de exponer la lista directo.

Lo que me quedó del proyecto es que separar en paquetes no es solo organización visual. Cuando algo fallaba y estaba todo junto era un desastre rastrearlo. Con capas separadas al menos sabes dónde buscar.
