package ar.edu.itba.pod.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReticketWrapper implements Serializable {
    private int ticketsChanged = 0;
    private final List<ReticketWrapper.TicketInfo> noAlternativeTickets;


    public ReticketWrapper() {
        noAlternativeTickets = new ArrayList<>();
    }

    public void incrementTickets() {
        ticketsChanged++;
    }
    
    public void addNoAlternativeTicket(ReticketWrapper.TicketInfo ticketInfo) {
        noAlternativeTickets.add(ticketInfo);
    }

    public static class TicketInfo implements Serializable {
        private final String flightCode;
        private final String passengerName;

        public TicketInfo(String flightCode, String passengerName) {
            this.flightCode = flightCode;
            this.passengerName = passengerName;
        }

        public String getFlightCode() {
            return flightCode;
        }

        public String getPassengerName() {
            return passengerName;
        }

        @Override
        public String toString() {
            return "Cannot find alternative flight for " + passengerName + " with Ticket " + flightCode;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ticketsChanged).append(" tickets were changed.\n");
        for (ReticketWrapper.TicketInfo ticketInfo : noAlternativeTickets) {
            sb.append(ticketInfo.toString()).append("\n");
        }
        return sb.toString();
    }
}
