package ar.edu.itba.pod.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException {
        final Servant servant = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(servant, 0);

        int port = 1099;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                logger.error("Invalid port argument");
            }
        }

        final Registry registry = LocateRegistry.getRegistry("localhost", port);
        registry.rebind("flight_administration", remote);
        registry.rebind("flight_notification", remote);
        registry.rebind("seat_administration", remote);
        registry.rebind("seat_map", remote);
    }
}
