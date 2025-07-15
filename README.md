# Sistema de Administración de Vuelos y Asientos ✈️

> **Sistema distribuido para la gestión integral de vuelos, asientos y notificaciones de aerolíneas implementado con Java RMI**

Trabajo Práctico 1 para **Programación de Objetos Distribuidos**. ITBA 2Q 2022.

## 📋 Descripción

Sistema distribuido que permite la administración completa de vuelos comerciales, incluyendo gestión de modelos de aeronaves, asignación de asientos, notificaciones en tiempo real y generación de reportes. Implementado con arquitectura cliente-servidor usando Java RMI para comunicación distribuida.

## 🛠️ Tecnologías

- **Java 8** - Lenguaje de programación principal
- **Java RMI** - Comunicación distribuida entre cliente y servidor
- **Maven 3** - Gestión de dependencias y automatización de build
- **SLF4J + Log4j** - Sistema de logging
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para pruebas unitarias
- **Hazelcast** - Clustering y distribución (opcional)

## 🏗️ Arquitectura

El proyecto está organizado en tres módulos principales:

- **`api/`** - Interfaces remotas, modelos de datos y excepciones
- **`server/`** - Implementación del servidor RMI y lógica de negocio
- **`client/`** - Aplicaciones cliente para diferentes funcionalidades

## 🚀 Instalación

### Prerrequisitos
- Java 8 o superior
- Maven 3.6+

### Pasos de instalación

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd tpe1-pod
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Descomprimir artefactos generados**
   ```bash
   tar -xzvf server/target/tpe1-g9-server-1.0-SNAPSHOT-bin.tar.gz
   tar -xzvf client/target/tpe1-g9-client-1.0-SNAPSHOT-bin.tar.gz
   ```

4. **Usar script automatizado (recomendado)**
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
   > 💡 **Tip**: El script `build.sh` realiza automáticamente todos los pasos anteriores (compilación, descompresión y permisos)

## 📖 Uso

### Configuración inicial

Otorgar permisos de ejecución a los scripts:

```bash
cd tpe1-g9-server-1.0-SNAPSHOT
chmod u+x ./run-registry.sh
chmod u+x ./run-server.sh

cd ../tpe1-g9-client-1.0-SNAPSHOT
chmod u+x run-admin.sh
chmod u+x run-notifications.sh
chmod u+x run-seatAssign.sh
chmod u+x run-seatMap.sh
```

### Iniciar servicios

1. **Iniciar RMI Registry**
   ```bash
   cd tpe1-g9-server-1.0-SNAPSHOT
   ./run-registry.sh [PORT]  # Puerto por defecto: 1099
   ```

2. **Iniciar servidor**
   ```bash
   ./run-server.sh [PORT]  # Puerto por defecto: 1099
   ```

## 🖥️ Aplicaciones Cliente

### 👨‍💼 Cliente de Administración (`run-admin`)

Gestiona modelos de aeronaves y vuelos.

```bash
./run-admin.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [-DinPath=filename | -Dflight=flightCode]
```

**Parámetros:**
- `xx.xx.xx.xx:yyyy` - Dirección IP y el puerto donde está publicado el servicio de administración de vuelos
- `actionName` - Nombre de la acción a realizar

**Acciones disponibles:**
- `models` - Agrega un lote de modelos de aviones
- `flights` - Agrega un lote de vuelos
- `status` - Consulta el estado del vuelo de código `flightCode`. Imprime en pantalla el estado del vuelo luego de invocar la acción o el error correspondiente
- `confirm` - Confirma el vuelo de código `flightCode`. Imprime en pantalla el estado del vuelo luego de invocar la acción o el error correspondiente
- `cancel` - Cancela el vuelo de código `flightCode`. Imprime en pantalla el estado del vuelo luego de invocar la acción o el error correspondiente
- `reticketing` - Fuerza el cambio de tickets de vuelos cancelados por tickets de vuelos alternativos

### 💺 Cliente de Asignación de Asientos (`run-seatAssign`)

Maneja la asignación y movimiento de asientos.

```bash
./run-seatAssign.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -Dflight=flightCode 
[-Dpassenger=name | -Drow=num | -Dcol=L | -DoriginalFlight=originFlightCode]
```

**Parámetros:**
- `xx.xx.xx.xx:yyyy` - Dirección IP y el puerto donde está publicado el servicio de asignación de asientos

**Acciones disponibles:**
- `status` - Imprime en pantalla si el asiento de fila `num` y columna `L` del vuelo de código `flightCode` está libre u ocupado luego de invocar la acción
- `assign` - Asigna al pasajero `name` al asiento libre de fila `num` y columna `L` del vuelo de código `flightCode`
- `move` - Mueve al pasajero `name` de un asiento asignado en el vuelo de código `flightCode` a un asiento libre del mismo vuelo, ubicado en la fila `num` y columna `L`
- `alternatives` - Listar los vuelos alternativos al vuelo de código `flightCode` para el pasajero `name`. Para cada categoría de asiento en cada vuelo alternativo se debe listar:
  - El código del aeropuerto destino
  - El código del vuelo
  - La cantidad de asientos asignables de la categoría
  - La categoría de los asientos asignables
- `changeTicket` - Cambia el ticket del pasajero `name` de un vuelo de código `originFlightCode` a otro vuelo alternativo de código `flightCode`

### 🔔 Cliente de Notificaciones (`run-notifications`)

Recibe notificaciones en tiempo real sobre cambios en vuelos.

```bash
./run-notifications.sh -DserverAddress=xx.xx.xx.xx:yyyy -Dflight=flightCode -Dpassenger=name
```

**Parámetros:**
- `xx.xx.xx.xx:yyyy` - Dirección IP y el puerto donde está publicado el servicio de notificaciones del vuelo
- `flightCode` - El código del vuelo
- `name` - El nombre del pasajero

### 🗺️ Cliente de Mapas de Asientos (`run-seatMap`)

Genera reportes y mapas de asientos en formato CSV.

```bash
./run-seatMap.sh -DserverAddress=xx.xx.xx.xx:yyyy -Dflight=flightCode 
[-Dcategory=catName | -Drow=rowNumber] -DoutPath=output.csv
```

**Parámetros:**
- `xx.xx.xx.xx:yyyy` - Dirección IP y el puerto donde está publicado el servicio de consulta del mapa de asientos
- `flightCode` - Código del vuelo (obligatorio)
- `catName` - Nombre de la categoría de asiento elegida para resolver la Consulta 2
- `rowNumber` - Número de la fila de asientos elegida para resolver la Consulta 3
- `output.csv` - Path del archivo de salida con los resultados de la consulta elegida

**Consultas disponibles:**
- **Sin `-Dcategory` ni `-Drow`**: Se resuelve la **Consulta 1** (mapa completo del vuelo)
- **Con `-Dcategory`**: Se resuelve la **Consulta 2** (asientos filtrados por categoría)
- **Con `-Drow`**: Se resuelve la **Consulta 3** (información de fila específica)

> ⚠️ **Restricciones importantes:**
> - Si no se indica `-Dflight` la consulta falla
> - Si se indican ambos `-Dcategory` y `-Drow` la consulta falla

## 🏷️ Parámetros Generales

**Categorías de asientos disponibles:**
- `ECONOMY` - Clase económica
- `PREMIUM_ECONOMY` - Clase económica premium  
- `BUSINESS` - Clase ejecutiva

**Formato de coordenadas de asientos:**
- Las filas se numeran desde 0
- Las columnas se identifican con letras desde 'A'

## 🧪 Testing

Ejecutar tests unitarios:

```bash
mvn test
```

## 📝 Logs

Los logs se configuran mediante `log4j.xml` en cada módulo y proporcionan información detallada sobre:
- Operaciones del servidor
- Conexiones de clientes
- Errores y excepciones
- Estado de transacciones

## 🤝 Contribución

Este proyecto fue desarrollado como parte de un trabajo práctico académico para el curso de Programación de Objetos Distribuidos del ITBA.

---

