package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import ar.edu.itba.pod.service.FlightNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class NotificationClient {

    private final static Logger logger= LoggerFactory.getLogger(NotificationClient.class);

    public static void main(String[] args) {
        //Non nullable params
        String serverAddress = Optional.ofNullable(System.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new);
        String flightCode = Optional.ofNullable(System.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        String passengerName = Optional.ofNullable(System.getProperty("passenger")).orElseThrow(IllegalArgumentException::new);

        String[] address = serverAddress.split(":");
        String host = address[0];
        String port = address[1];

        final Registry registry;
        final FlightNotificationService flightNotificationService;

        try{
            registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            flightNotificationService=(FlightNotificationService) registry.lookup("flight_notification");
            final NotificationEventCallback notificationEventCallback = new NotificationEventCallbackImpl();
            UnicastRemoteObject.exportObject(notificationEventCallback, 0);
            flightNotificationService.registerPassenger(flightCode, passengerName, notificationEventCallback);
        } catch (Exception error) {
            logger.error(error.getMessage());
        }
    }
}
