package gui;

import classes.Constants;
import classes.Match;
import classes.Team;
import classes.Tournament;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MatchController {

    @FXML
    private Label tournamentLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label firstTeamLabel;

    @FXML
    private Label firstScoreLabel;

    @FXML
    private Label secondScoreLabel;

    @FXML
    private Label secondTeamLabel;

    public void setMatchData(Match m) throws ExecutionException, InterruptedException {
        String team1 = Objects.requireNonNull(FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION)
                .document(m.getHomeTeamId()).get().get().toObject(Team.class)).getName();
        String team2 = Objects.requireNonNull(FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION)
                .document(m.getAwayTeamId()).get().get().toObject(Team.class)).getName();
        Tournament tournament = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .document(m.getTournamentID()).get().get().toObject(Tournament.class);
        assert tournament != null;
        tournamentLabel.setText(tournament.getName());
        firstTeamLabel.setText(team1);
        secondTeamLabel.setText(team2);
        if (m.isPlayed()){
            firstScoreLabel.setText(String.valueOf(m.getFirstScore()));
            secondScoreLabel.setText(String.valueOf(m.getSecondScore()));
        }
        dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(m.getDate()));
    }
}
