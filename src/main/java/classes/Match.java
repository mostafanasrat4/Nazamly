package classes;

import java.util.Date;
import java.util.UUID;

public class Match implements Comparable<Match> {
    private final String id;
    private String homeTeamId;
    private String awayTeamId;
    private String tournamentID;
    private boolean isPlayed;
    private int noOfSoldTickets;
    private int noOfAvailableTickets;
    private int ticketPrice = 50;
    private int revenue;
    private int firstScore;
    private int secondScore;
    private Date date;
    private int week;
    private String declare;

    public Match() {
        this.id = UUID.randomUUID().toString();
    }

    public Match(String homeTeamId, String awayTeamId) {
        this();
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public String getDeclare() {
        return declare;
    }

    public void setDeclare(String declare) {
        this.declare = declare;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getId() {
        return id;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public int getNoOfSoldTickets() {
        return noOfSoldTickets;
    }

    public void setNoOfSoldTickets(int noOfSoldTickets) {
        this.noOfSoldTickets = noOfSoldTickets;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(int firstScore) {
        this.firstScore = firstScore;
    }

    public int getSecondScore() {
        return secondScore;
    }

    public void setSecondScore(int secondScore) {
        this.secondScore = secondScore;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNoOfAvailableTickets() {
        return noOfAvailableTickets;
    }

    public void setNoOfAvailableTickets(int noOfAvailableTickets) {
        this.noOfAvailableTickets = noOfAvailableTickets;
    }

    @Override
    public int compareTo(Match match) {
        return this.date.compareTo(match.date);
    }
}
