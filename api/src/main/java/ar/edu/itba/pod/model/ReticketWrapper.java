package ar.edu.itba.pod.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReticketWrapper implements Serializable {
    private int ticketsChanged = 0;
    private List<ReticketWrapper.TicketInfo> noAlternativeTickets;


    public ReticketWrapper() {
        noAlternativeTickets = new ArrayList<>();
    }

    public int getTicketsChanged() {
        return ticketsChanged;
    }
    

    public List<ReticketWrapper.TicketInfo> getNoAlternativeTickets() {
        return noAlternativeTickets;
    }

    public void incrementTickets() {
        ticketsChanged++;
    }
    
    public void addNoAlternativeTicket(ReticketWrapper.TicketInfo ticketInfo) {
        noAlternativeTickets.add(ticketInfo);
    }

    public static class TicketInfo implements Serializable {
        private String flightCode;
        private String passengerName;

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
