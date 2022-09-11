package ar.edu.itba.pod.client;

import ar.edu.itba.pod.model.Airplane;
import ar.edu.itba.pod.model.AirplaneWrapper;
import ar.edu.itba.pod.model.Category;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.service.FlightAdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import java.util.stream.Stream;

public class AdminClient {

    private final static Logger logger= LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws RemoteException, NotBoundException {
        String serverAddress, serverPort;

        try {
            serverAddress = parseParameter(args,"-DserverAddress");
        } catch (IllegalArgumentException exc) {
            System.out.println("Error: You must provide the server address");
            return;
        }

        try {
            serverPort = serverAddress.substring(serverAddress.indexOf(':')+1);
            serverAddress = serverAddress.substring(0,serverAddress.indexOf(':'));
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("Error: you must provide a port");
            return;
        }

        String action = parseParameter(args,"-Daction");

        final Registry registry = LocateRegistry.getRegistry(serverAddress, Integer.parseInt(serverPort));
        final FlightAdministrationService service = (FlightAdministrationService) registry.lookup("flight_administration");

        try {
            execAction(args, action, service);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void execAction(String[] args, String action, FlightAdministrationService service) throws RemoteException {
        if ("models".equals(action) || "flights".equals(action)) {
            String inPath = parseParameter(args, "-DinPath");
            switch (action) {
                case "models":
                    for (AirplaneWrapper airplane : parseAirplanes(inPath)) {
                        service.addPlaneModel(airplane.getModelName(), airplane.getSections());
                        logger.info("Added airplane model: " + airplane.getModelName());
                    }
                    logger.info("Successfully added all models");
                    break;
                case "flights":
                    break;
            }
        } else if ("status".equals(action) || "confirm".equals(action) || "cancel".equals(action)) {

        } else if ("reticketing".equals(action)) {

        }
    }

    private static String parseParameter(String[] args, String parameter) {
        return Stream.of(args).filter(arg -> arg.contains(parameter))
                .map(arg -> arg.substring(arg.indexOf("=")+ 1))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Must provide " + parameter + "=<value> param")
                );
    }

    private static List<AirplaneWrapper> parseAirplanes(String inPath) {
        int added = 0;
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
                added++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Added {} airplanes", added);
        return ret;
    }

//    private static List<FlightWrapper> parseFlights(String inPath) {
//
//    }
}
