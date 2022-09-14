# tpe1pod-g9

Trabajo Práctico 1 para Programación de Objetos Distribuidos. ITBA 2Q 2022.



## Instalación

Ejecutar el siguiente comando de maven luego de clonar el proyecto

```bash
mvn clean install
```
Luego, en los directorios server/target y client/target se encuentran archvos .tar.gz que deben ser descomprimidos. Las carpetas obtenidas contienen los scripts para ejecutar las distintas partes del proyecto.

TODO: REVISAR 
```bash
tar -xzvf server/target/tpe1-pod-bin.tar.gz
tar -xzvf client/target/tpe1-pod-bin.tar.gz
```


## Uso

Otorgar permisos a los scripts obtenidos al descomprimir el .tar.gz en el server, realizado en el paso anterior.

```bash
TODO: REVISAR 
cd tpe1-pod/server/main/assembly/overlay
chmod 777 ./run-registry.sh
chmod 777 ./run-server.sh
```

Luego encender el Rmi Registry ejecutando el script run-registry.sh. El puerto por default es 1099.

```bash
./run-registry.sh [PORT]
```
Luego, encender el server, mediante el otro script de esta misma carpeta carpeta. El puerto por default es 1099.

```bash
./run-server.sh [PORT]
```

Para ejecutar los clientes debemos acceder al directorio generado al descomprimir su archivo al inicio y asignar los permisos pertinentes.

```bash
TODO: REVISAR 
cd client/target/tpe1-pod
chmod 777 run-admin.sh
chmod 777 run-client.sh
chmod 777 run-notification.sh
chmod 777 run-seatAssign.sh
chmod 777 run-seatMap.sh
```
Luego los clientes disponibles junto a su modo de uso son los siguientes:

```bash
 ./run-admin.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
[ -DinPath=filename | -Dflight=flightCode ]

```
donde:
- xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de
administración de vuelos.
- actionName es el nombre de la acción a realizar. sus posibles valores son:
    - models: Agrega una lote de modelos de aviones.
    - flights: Agrega un lote de vuelos.
    - status: Consulta el estado del vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente
    - confirm: Confirma el vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente 
    - cancel: Cancela el vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente
    - reticketing: Fuerza el cambio de tickets de vuelos cancelados por tickets de vuelos alternativos


```bash
 ./run-seatAssign -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
-Dflight=flightCode [ -Dpassenger=name | -Drow=num | -Dcol=L |
-DoriginalFlight=originFlightCode ]
```
donde:
- xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de asignación de asientos.

- actionName es el nombre de la acción a realizar. Cuyos valores pueden ser:
    - status: Deberá imprimir en pantalla si el asiento de fila num y columna L del vuelo de código flightCode está libre u ocupado luego de invocar a la acción
    - assign: Asigna al pasajero name al asiento libre de fila num y columna L del vuelo de código flightCode.
    - move: Mueve al pasajero name de un asiento asignado en el vuelo de código flightCode a un asiento libre del mismo vuelo, ubicado en la fila num y columna L.
    - alternatives: Listar los vuelos alternativos al vuelo de código flightCode para el pasajero name. Para cada categoría de asiento en cada vuelo alternativo se debe listar
        - El código del aeropuerto destino
        - El código del vuelo
        - La cantidad de asientos asignables de la categoría
        - La categoría de los asientos asignables
    - changeTicket: Cambia el ticket del pasajero name de un vuelo de código originFlightCode a otro vuelo alternativo de código flightCode
```bash
 ./run-notifcations -DserverAddress=xx.xx.xx.xx:yyyy -Dflight=flightCode -Dpassenger=name

```
donde:
- xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de
notificaciones del vuelo
- flightCode: el código del vuelo
- name: el nombre del pasajero

```bash
./run-seatMap -DserverAddress=xx.xx.xx.xx:yyyy -Dflight=flightCode [-Dcategory=catName | -Drow=rowNumber ] -DoutPath=output.csv
```
donde:
- xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de consulta del mapa de asientos.
- Si no se indica -Dcategory ni -Drow se resuelve la Consulta 1
- Si se indica -Dcategory, catName es el nombre de la categoría de asiento elegida para resolver la Consulta 2
- Si se indica -Drow, rowNumber es el número de la fila de asientos elegida para resolver la Consulta 3
- Si no se indica -Dflight la consulta falla
- Si se indican ambos -Dcategory y -Drow la consulta falla
- output.csv es el path del archivo de salida con los resultados de la consulta elegida


