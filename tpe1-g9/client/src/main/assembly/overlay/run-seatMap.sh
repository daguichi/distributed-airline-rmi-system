#!/bin/bash

#usage ./run-seatMap -DserverAddress=xx.xx.xx.xx:yyyy -Dflight=flightCode [-Dcategory=catName | -Drow=rowNumber ] -DoutPath=output.csv
java "$@" -cp 'lib/jars/*' 'ar.edu.itba.pod.client.SeatMapClient'