package classes;

import com.google.firebase.cloud.FirestoreClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

public class Tournament {
    private final String id;
    private String name;
    private String adminID;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private int noOfTeams;

    public Tournament() {
        this.id = UUID.randomUUID().toString();
    }

    public Tournament(String name, String adminID, Date startDate, Date endDate) {
        this();
        this.name = name;
        this.adminID = adminID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public int getNoOfTeams() {
        return noOfTeams;
    }

    public void setNoOfTeams(int noOfTeams) {
        this.noOfTeams = noOfTeams;
    }

    public void scheduleMatches(List<Team> teamsList, String tournamentId, int noOfAvailableTickets, int ticketPrice, int n) {
        int teams = noOfTeams;
        int weeks = (teams - 1) * n;
        int matches = teams / 2;
        int daysBetweenWeeks;

        LocalDate start = getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        daysBetweenWeeks = (noOfTeams == 2) ? 0 : (int) (DAYS.between(start, end)) / (weeks - 1) / n;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < weeks; i++) {
                for (int j = 0; j < matches; j++) {
                    LocalDate d = start.plusDays(i * daysBetweenWeeks);
                    Team team1 = teamsList.get(matches + j);
                    Team team2 = teamsList.get(matches - j - 1);
                    Match match = new Match(team1.getId(), team2.getId());
                    match.setDate(java.sql.Date.valueOf(d));
                    match.setWeek(i + 1);
                    match.setDeclare(team1.getName() + " vs " + team2.getName() + " week " + match.getWeek());
                    match.setTournamentID(tournamentId);
                    match.setNoOfAvailableTickets(noOfAvailableTickets);
                    match.setTicketPrice(ticketPrice);

                    FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(this.id)
                            .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);

                    FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team1.getId())
                            .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);
                    FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team2.getId())
                            .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);

                }
                Team temp = teamsList.get(1);
                teamsList.remove(1);
                teamsList.add(temp);
            }
            Collections.reverse(teamsList);
        }
    }
}