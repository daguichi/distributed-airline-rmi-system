package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import ar.edu.itba.pod.exceptions.AirplaneAlreadyExistsException;
import ar.edu.itba.pod.exceptions.FlightAlreadyExistsException;
import ar.edu.itba.pod.exceptions.NoSuchAirplaneException;
import ar.edu.itba.pod.exceptions.NoSuchFlightException;
import ar.edu.itba.pod.model.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Airport {
    private static Airport instance = null;

    private final Map<String, Airplane> airplanes = new HashMap<>();
    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Map<String, List<NotificationEventCallback>>> subscribers;

    private final ReentrantReadWriteLock airplanesLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock flightsLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock subscribersLock = new ReentrantReadWriteLock(true);

    private Airport() {
        subscribers = new HashMap<>();
    }

    public static Airport getInstance() {
        if(instance == null)
            instance = new Airport();
        return instance;
    }

    public Airplane getAirplane(String name) {
        Airplane airplane;
        airplanesLock.readLock().lock();
        try {
            airplane = airplanes.get(name);
            if(airplane == null)
                throw new NoSuchAirplaneException(name);
        }
        finally {
            airplanesLock.readLock().unlock();
        }
        return airplane;
    }

    public Flight getFlight(String flightCode) {
        Flight flight;
        flightsLock.readLock().lock();
        try {
            flight = flights.get(flightCode);
            if(flight == null)
                throw new NoSuchFlightException(flightCode);
        }
        finally {
            flightsLock.readLock().unlock();
        }
        return flight;
    }

    List<NotificationEventCallback> getCallbacks(String flightCode, String passengerName) {
        List<NotificationEventCallback> toNotify;
        subscribersLock.readLock().lock();
        try {
            toNotify = subscribers.getOrDefault(flightCode, new HashMap<>()).getOrDefault(
                    passengerName, Collections.emptyList());
        }
        finally {
            subscribersLock.readLock().unlock();
        }
        return toNotify;
    }

    public List<String> getSubscribers(String flightCode) {
        List<String> subscribersList;
        subscribersLock.readLock().lock();
        try {
            subscribersList = new ArrayList<>(subscribers.getOrDefault(
                    flightCode, Collections.emptyMap()).keySet());
        }
        finally {
            subscribersLock.readLock().unlock();
        }
        return subscribersList;
    }

    public void addAirplane(Airplane airplane) {
        airplanesLock.writeLock().lock();
        try {
            if(airplanes.containsKey(airplane.getName()))
                throw new AirplaneAlreadyExistsException(airplane.getName());
            airplanes.put(airplane.getName(),airplane);
        }
        finally {
            airplanesLock.writeLock().unlock();
        }
    }

    public void addFlight(Flight flight) {
        flightsLock.writeLock().lock();
        try {
            if(flights.containsKey(flight.getFlightCode()))
                throw new FlightAlreadyExistsException(flight.getFlightCode());
            flights.put(flight.getFlightCode(),flight);
        }
        finally {
            flightsLock.writeLock().unlock();
        }
    }

    public void addSubscriber(String flightCode, String passengerName, NotificationEventCallback callback) {
        subscribersLock.writeLock().lock();
        try {
            subscribers.putIfAbsent(flightCode, new HashMap<>());
            subscribers.get(flightCode).putIfAbsent(passengerName, new ArrayList<>());
            subscribers.get(flightCode).get(passengerName).add(callback);
        }
        finally {
            subscribersLock.writeLock().unlock();
        }
    }

    public List<Flight> getCancelledFlights() {
        flightsLock.readLock().lock();
        List<Flight> cancelledFlights;
        try {
            cancelledFlights = flights.values().stream()
                    .filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED))
                    .sorted(Comparator.comparing(Flight::getFlightCode)).collect(Collectors.toList());
        }
        finally {
            flightsLock.readLock().unlock();
        }
        return cancelledFlights;
    }

    public List<Flight> getAlternativeFlights(String destinationCode) {
        flightsLock.readLock().lock();
        List<Flight> alternativeFlights;
        try {
            alternativeFlights = flights.values().stream().filter(
                    flight -> flight.getDestinationCode().equals(destinationCode)).filter(
                    flight -> flight.getStatus().equals(FlightStatus.PENDING)
            ).collect(Collectors.toList());
        }
        finally {
            flightsLock.readLock().unlock();
        }
        return alternativeFlights;
    }

    public List<Flight> getAlternativeFlights(String flightCode, String destinationCode, String passengerName) {
        flightsLock.readLock().lock();
        List<Flight> alternativeFlights;
        try {
            alternativeFlights = flights.values().stream().filter(
                            flight -> flight.getDestinationCode().equals(destinationCode)).filter(
                            flight -> flight.getStatus().equals(FlightStatus.PENDING)).filter(flight -> !flight.getFlightCode().
                            equals(flightCode)
                    ).filter( flight -> flight.getTickets().stream().noneMatch(t -> t.getPassengerName().equals(passengerName))).
                    collect(Collectors.toList());
        }
        finally {
            flightsLock.readLock().unlock();
        }
        return alternativeFlights;
    }

}
