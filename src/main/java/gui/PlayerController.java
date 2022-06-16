package gui;

import classes.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerController {

    @FXML
    private Label tournamentLabel;

    @FXML
    private Label teamLabel;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label noGoalsLabel;

    public void setPlayerData(Player p, String tourName, String teamName) {
        tournamentLabel.setText(tourName);
        playerNameLabel.setText(p.getName());
        teamLabel.setText(teamName);
        noGoalsLabel.setText(p.getGoals()+" goals!");
    }
}
