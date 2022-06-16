package classes;

import java.util.UUID;

public class Ticket {
    private final String id;
    private String matchID;
    private String tournamentID;
    private int price;

    public Ticket() {
        id= UUID.randomUUID().toString();
    }

    public Ticket(String matchID, int price) {
        this();
        this.matchID = matchID;
        this.price = price;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getId() {
        return id;
    }

    public String getMatchID() {
        return matchID;
    }

    public int getPrice() {
        return price;
    }

}
