package ar.edu.itba.pod.client;

import ar.edu.itba.pod.model.Category;
import ar.edu.itba.pod.model.Row;
import ar.edu.itba.pod.model.Ticket;
import ar.edu.itba.pod.service.SeatMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;


public class SeatMapClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatMapClient.class);

    private static void writeOutputToCSV(String fileName, List<Row> flightRows) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for(Row row : flightRows) {
            for(Ticket ticket : row.getTickets()){
                writer.append(String.format("%s %s %s ,", ticket.getRow(), ticket.getColumn(),
                        ticket.getPassengerName().toCharArray()[0]));
            }
            writer.append(String.format("%s\n", row.getCategory()));
        }
        writer.close();
    }

    public static void main(String[] args) {
        logger.info("Seat Map Client starting ...");

        //Non nullable params
        String flightCode = Optional.ofNullable(System.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        String fileName = Optional.ofNullable(System.getProperty("outPath")).orElseThrow(IllegalArgumentException::new);
        String serverAddress = Optional.ofNullable(System.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new);

        //nullable params
        String category = System.getProperty("category");
        String row = System.getProperty("row");

        if(category != null && row != null) {
            logger.error("Enter category OR row, not both\n");
            throw new IllegalArgumentException();
        }

        String[] address = serverAddress.split(":");
        String host = address[0];
        String port = address[1];

        final SeatMapService seatMapService;
        final Registry registry;

        try {
            registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            seatMapService = (SeatMapService) registry.lookup("seat_map");
        }
        catch (Exception error) {
            logger.error(error.toString());
            return ;
        }

        List<Row> rowList;
        try {
            if(category != null) {
                rowList = seatMapService.getFlightMapByCategory(flightCode, Category.valueOf(category));
            }
            else if(row != null) {
                rowList = seatMapService.getFlightMapByRow(flightCode, Integer.parseInt(row));
            }
            else
                rowList = seatMapService.getFlightMap(flightCode);
            if(!rowList.isEmpty())
                writeOutputToCSV(fileName, rowList);
            else logger.info("There is no Seat Map matching query conditions\n");
        }
        catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
