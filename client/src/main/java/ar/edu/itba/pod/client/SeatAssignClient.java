package ar.edu.itba.pod.client;

import ar.edu.itba.pod.model.AlternativeFlight;
import ar.edu.itba.pod.model.Seat;
import ar.edu.itba.pod.service.SeatAdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;

public class SeatAssignClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatAssignClient.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("Seat Assign Client starting...\n");
        //Non nullable params
        String actionName = Optional.ofNullable(System.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        String flightCode = Optional.ofNullable(System.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        String serverAddress = Optional.ofNullable(System.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new);


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
            if ("status".equals(actionName) || "assign".equals(actionName) || "move".equals(actionName)) {
                int row = Integer.parseInt(System.getProperty("row"));
                if (row <= 0) {
                    throw new IllegalArgumentException("Row must be greater than 0");
                }
                row -= 1;
                char col = System.getProperty("col").charAt(0);
                String passengerName;
                switch (actionName) {
                    case "status":
                        Seat seat = seatAdministrationService.isAvailable(flightCode, row, col);
                        StringBuilder sb = new StringBuilder();
                        sb.append("Seat ").append(seat.getRow()+1).append(seat.getColumn()).append(" is ");
                        if (seat.isAvailable()) {
                            sb.append("FREE");
                        } else {
                            sb.append("ASSIGNED to ").append(seat.getTicket().get().getPassengerName());
                        }
                        sb.append(".");
                        System.out.println(sb);
                        break;
                    case "assign":
                        passengerName = System.getProperty("passenger");
                        seatAdministrationService.assignSeat(flightCode, passengerName, row, col);
                        break;
                    case "move":
                        passengerName = System.getProperty("passenger");
                        seatAdministrationService.changeSeat(flightCode, passengerName, row, col);
                        break;
                }
            } else if ("alternatives".equals(actionName)) {
                String passengerName = System.getProperty("passenger");
                List<AlternativeFlight> alts = seatAdministrationService.getAlternativeFlights(flightCode, passengerName);
                for (AlternativeFlight alt : alts) {
                    System.out.println(alt.getDestinationCode() + " | " + alt.getFlightCode() +
                            " | " + alt.getAvailableSeats() + " " + alt.getCategory());
                }
            } else if ("changeTicket".equals(actionName)) {
                String passengerName = System.getProperty("passenger");
                String originalFlightCode = System.getProperty("originalFlight");
                seatAdministrationService.changeFlight(originalFlightCode, flightCode, passengerName);
            } else {
                throw new IllegalArgumentException("Action is not a valid option\n");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

}
