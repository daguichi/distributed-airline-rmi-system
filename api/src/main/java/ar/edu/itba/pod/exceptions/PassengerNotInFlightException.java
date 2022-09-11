package ar.edu.itba.pod.exceptions;

public class PassengerNotInFlightException extends RuntimeException {
    public PassengerNotInFlightException(String passengerName, String flightCode) {
        super("Passenger " + passengerName + " is not in flight " + flightCode);
    }
}
