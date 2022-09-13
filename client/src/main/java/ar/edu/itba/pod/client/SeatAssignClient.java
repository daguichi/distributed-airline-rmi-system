package ar.edu.itba.pod.client;

import ar.edu.itba.pod.service.SeatAdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Optional;

public class SeatAssignClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatAssignClient.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("Seat Assign Client starting...\n");
        //Non nullable params
        String actionName = Optional.ofNullable(System.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        String flightCode = Optional.ofNullable(System.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        String serverAddress = Optional.ofNullable(System.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new);


        //Nullable params
        String passengerName = System.getProperty("passenger");
        int row = Integer.parseInt(System.getProperty("row"));
        if (row <= 0) {
            throw new IllegalArgumentException("Row must be greater than 0");
        }
        row -= 1;
        char col = System.getProperty("col").charAt(0);
        String originalFlightCode = System.getProperty("originalFlight");

        String[] address = serverAddress.split(":");
        String host = address[0];
        String port = address[1];

        final SeatAdministrationService seatAdministrationService;
        final Registry registry;

        try {
            registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            seatAdministrationService = (SeatAdministrationService) registry.lookup("seat_administration");
        }
        catch (Exception error) {
            logger.error(error.getMessage());
            return ;
        }

        try {
            //TODO CHECK PARAMETERS != NULL
            logger.info("action name is " +actionName);
            switch (actionName) {
                case "status":
                    seatAdministrationService.isAvailable(flightCode, row, col);
                    break;
                case "assign":
                    seatAdministrationService.assignSeat(flightCode, passengerName, row, col);
                    break;
                case "move":
                    seatAdministrationService.changeSeat(flightCode, passengerName, row, col);
                    break;
                case "alternatives":
                    seatAdministrationService.getAlternativeFlights(flightCode, passengerName);
                    break;
                case "changeTicket":
                    seatAdministrationService.changeFlight(originalFlightCode, flightCode, passengerName);
                    break;
                default:
                    throw new IllegalArgumentException("Action is not a valid option\n");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

}
