package ar.edu.itba.pod.exceptions;

public class PassengerAlreadyInFlightException extends RuntimeException {
    public PassengerAlreadyInFlightException(String passengerName, String flightCode) {
        super("Passenger " + passengerName + " has a seat in flight " + flightCode);
    }
}
