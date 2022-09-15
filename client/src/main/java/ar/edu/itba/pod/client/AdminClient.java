package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.AirplaneAlreadyExistsException;
import ar.edu.itba.pod.exceptions.FlightAlreadyExistsException;
import ar.edu.itba.pod.exceptions.InvalidSectionException;
import ar.edu.itba.pod.exceptions.NoSuchAirplaneException;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.service.FlightAdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminClient {

    private final static Logger logger= LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws RemoteException, NotBoundException {
        String serverAddress, port, host, action;

        try {
            serverAddress = Optional.ofNullable(System.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exc) {
            logger.error("You must provide the server address");
            return;
        }

        try {
            String[] address = serverAddress.split(":");
            host = address[0];
            port = address[1];
        } catch (StringIndexOutOfBoundsException ex) {
            logger.error("You must provide a port");
            return;
        }
        try {
            action = Optional.ofNullable(System.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exc) {
            logger.error("You must provide an action");
            return;
        }

        final Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
        final FlightAdministrationService service = (FlightAdministrationService) registry.lookup("flight_administration");

        try {
            execAction(action, service);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void execAction(String action, FlightAdministrationService service) throws RemoteException, InterruptedException {
        if ("models".equals(action) || "flights".equals(action)) {
            String inPath = System.getProperty("inPath");
            switch (action) {
                case "models":
                    int counter = 0;
                    for (AirplaneWrapper airplane : parseAirplanes(inPath)) {
                        try {
                            service.addPlaneModel(airplane.getModelName(), airplane.getSections());
                        }
                        catch (InvalidSectionException | AirplaneAlreadyExistsException | RemoteException exception ) {
                            if(exception instanceof InvalidSectionException || exception instanceof  AirplaneAlreadyExistsException) {
                                logger.error("Cannot add model " + airplane.getModelName()+ ".");
                            } else
                                logger.error(exception.getMessage());
                            continue;
                        }
                        counter++;
                    }
                    logger.info(counter + " models added.");
                    break;
                case "flights":
                    int added = 0;
                    for (FlightWrapper flight : parseFlights(inPath)) {
                        try {
                            service.addFlight(flight.getModelName(), flight.getFlightCode(), flight.getDestinationCode(), flight.getTickets());
                        }
                        catch (NoSuchAirplaneException | FlightAlreadyExistsException | RemoteException exception) {
                            if(exception instanceof NoSuchAirplaneException || exception instanceof  FlightAlreadyExistsException) {
                                logger.error("Cannot add flight " + flight.getFlightCode() + ".");
                            } else
                                logger.error(exception.getMessage());
                            continue;
                        }
                        added++;
                    }
                    logger.info(added + " flights added.");
                    break;
            }
        } else if ("status".equals(action) || "confirm".equals(action) || "cancel".equals(action)) {
            String flightCode = System.getProperty( "flight");
            FlightStatus ret;
            switch (action) {
                case "status":
                    ret = service.getFlightStatus(flightCode);
                    logger.info("Flight " + flightCode + " is "+ ret + ".");
                    break;
                case "confirm":
                    ret = service.confirmFlight(flightCode);
                    logger.info("Flight " + flightCode + " is "+ ret + ".");
                    break;
                case "cancel":
                    ret = service.cancelFlight(flightCode);
                    logger.info("Flight " + flightCode + " is "+ ret + ".");
                    break;
            }
        } else if ("reticketing".equals(action)) {
            ReticketWrapper reticket = service.reprogramFlightsTickets();
            logger.info(reticket.toString());
        }
    }

    private static List<AirplaneWrapper> parseAirplanes(String inPath) {
        List<AirplaneWrapper> ret = new ArrayList<>();
        Path path = Paths.get(inPath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] airplaneData = line.split(";");
                String modelName = airplaneData[0];
                String[] sections = airplaneData[1].split(",");
                List<Section> sectionList = new ArrayList<>();
                for (String s : sections) {
                    String[] sectionData = s.split("#");
                    Category category = Category.valueOf(sectionData[0]);
                    int rows = Integer.parseInt(sectionData[1]);
                    int columns = Integer.parseInt(sectionData[2]);
                    Section section = new Section(category, rows, columns);
                    sectionList.add(section);
                }
                ret.add(new AirplaneWrapper(modelName, sectionList));
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return ret;
    }

    private static List<FlightWrapper> parseFlights(String inPath) {
        List<FlightWrapper> ret = new ArrayList<>();
        Path path = Paths.get(inPath);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] flightData = line.split(";");
                String modelName = flightData[0];
                String flightCode = flightData[1];
                String destinationCode = flightData[2];
                String[] ticketsStr = flightData[3].split(",");
                List<Ticket> tickets = new ArrayList<>();
                for (String ticketStr : ticketsStr) {
                    String[] ticketData = ticketStr.split("#");
                    Category category = Category.valueOf(ticketData[0]);
                    String passengerName = ticketData[1];
                    Ticket ticket = new Ticket(passengerName, category);
                    tickets.add(ticket);
                }
                FlightWrapper flight = new FlightWrapper(modelName, flightCode, destinationCode, tickets);
                ret.add(flight);
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return ret;
    }
}
