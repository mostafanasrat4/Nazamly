package gui;

import classes.*;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class CreateTournamentController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label generalError;
    @FXML
    private Label typeError;
    @FXML
    private Spinner<Integer> teamsNoSpinner;
    int currentSpinnerValue;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label dateErrorLabel;
    @FXML
    private TextField tournamentNameField;
    @FXML
    private TextField newTeamField;
    @FXML
    private ComboBox<String> newTeamCombobox;
    @FXML
    private Button addFieldButton;
    @FXML
    private Button addComboButton;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> cupTeamsNo;
    @FXML
    private Label teamErrorLabel;
    @FXML
    private Label tournamentNameError;
    @FXML
    private Button createTournamentButton;
    @FXML
    private Label newTournamentSummaryLabel;
    @FXML
    private TextField ticketNumberPerMatchField;
    @FXML
    private TextField ticketPriceField;
    @FXML
    private Label teamsLabel;

    private final List<String> availableTournaments = new ArrayList<>();
    private final List<Team> dataBaseTeams = new ArrayList<>();
    private final List<String> teams = new ArrayList<>();
    private final List<Team> addedTeamsField = new ArrayList<>();
    private final List<Team> addedTeamsCombo = new ArrayList<>();
    private String teamsLabelString;
    private int count = 0;

    public void switchToHomePage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void switchToManagePage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("manageTournament.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void startDateSelected() {
        endDatePicker.setDisable(false);
    }

    public void checkEndifLater() {

        if (endDatePicker.getValue().compareTo(startDatePicker.getValue()) < 0) {
            dateErrorLabel.setOpacity(1);
        } else {
            dateErrorLabel.setOpacity(0);
        }

    }

    public void createNewTournament() {

        if (typeComboBox.getValue() == null) {
            typeError.setOpacity(1);
            typeError.setTextFill(Color.RED);
        } else
            typeError.setOpacity(0);
        if (!availableTournaments.contains(tournamentNameField.getText())) {
            if (!tournamentNameField.getText().equals("") && startDatePicker.getValue().compareTo(endDatePicker.getValue()) <= 0 &&
                    count == currentSpinnerValue) {
                List<Team> tourTeams = new ArrayList<>();
                Tournament tournament =
                        new Tournament(tournamentNameField.getText(),
                                LoginController.user.getId(),
                                Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                tournament.setNoOfTeams(count);
                FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                        .set(tournament);

                for (Team team : addedTeamsField) {
                    tourTeams.add(team);
                    FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team.getId())
                            .set(team);
                    FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                            .collection(Constants.TEAMS_COLLECTION).document(team.getId()).set(team);
                    FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team.getId())
                            .collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId()).set(tournament);
                }

                for (Team team : addedTeamsCombo) {
                    tourTeams.add(team);
                    FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                            .collection(Constants.TEAMS_COLLECTION).document(team.getId()).set(team);
                    FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team.getId())
                            .collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId()).set(tournament);
                }
                tournament.scheduleMatches(tourTeams, tournament.getId(), Integer.parseInt(ticketNumberPerMatchField.getText()),
                        Integer.parseInt(ticketPriceField.getText()), typeComboBox.getValue().equals("Home only") ? 1 : 2);
                newTournamentSummaryLabel.setTextFill(Color.GREEN);
                generalError.setOpacity(1);
                generalError.setTextFill(Color.GREEN);
                generalError.setText("Tournament created successfully");
                createTournamentButton.setDisable(true);
            } else if (count != currentSpinnerValue) {
                generalError.setOpacity(1);
                generalError.setTextFill(Color.RED);
                generalError.setText("Number of teams added is not equal to number of teams chosen");
            } else if (startDatePicker.getValue().compareTo(endDatePicker.getValue()) <= 0) {
                generalError.setOpacity(1);
                generalError.setTextFill(Color.RED);
                generalError.setText("End date cannot be after start date");
            }
        } else {
            tournamentNameError.setOpacity(1);
        }

    }

    public void addTeamCombo() {
        if (teams.contains(newTeamCombobox.getValue())) {
            teamErrorLabel.setOpacity(1);
        } else {
            teamErrorLabel.setOpacity(0);
            addComboButton.setDisable(true);
            teamsLabelString = teamsLabelString + "\n" + (count + 1) + " - " + newTeamCombobox.getValue();
            teamsLabel.setText(teamsLabelString);
            teams.add(newTeamCombobox.getValue());
            for (Team team : dataBaseTeams) {
                if (team.getName().equals(newTeamCombobox.getValue())) addedTeamsCombo.add(team);
            }
            newTeamCombobox.getItems().remove(newTeamCombobox.getValue());
            count++;
        }
    }

    public void addTeamField() {
        if (teams.contains(newTeamField.getText())) {
            teamErrorLabel.setOpacity(1);
        } else {
            teamErrorLabel.setOpacity(0);
            addFieldButton.setDisable(true);
            teamsLabelString = teamsLabelString + "\n" + (count + 1) + " - " + newTeamField.getText();
            teamsLabel.setText(teamsLabelString);
            teams.add(newTeamField.getText());
            addedTeamsField.add(new Team(newTeamField.getText()));
            newTeamField.setText("");
            count++;
        }
        newTeamField.clear();
    }

    public void comboTeamChosen() {
        addComboButton.setDisable(false);
    }

    public void fieldTeamWritten() {
        addFieldButton.setDisable(false);
    }

    public void reset() throws ExecutionException, InterruptedException {

        java.util.List<QueryDocumentSnapshot> documents = FirestoreClient.getFirestore()
                .collection(Constants.TOURNAMENTS_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot queryDocumentSnapshot : documents) {
            availableTournaments.add(queryDocumentSnapshot.toObject(Tournament.class).getName());
        }

        java.util.List<QueryDocumentSnapshot> documents2 = FirestoreClient.getFirestore()
                .collection(Constants.TEAMS_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot queryDocumentSnapshot : documents2) {
            dataBaseTeams.add(queryDocumentSnapshot.toObject(Team.class));
            newTeamCombobox.getItems().add(queryDocumentSnapshot.toObject(Team.class).getName());
        }
        teams.clear();
        teamsLabel.setText(" ");
        teamsLabelString = "";
        typeComboBox.getItems().addAll(
                "Home only", "Home and Away"
        );
        cupTeamsNo.getItems().addAll("2", "4", "8", "16", "32");
    }

    //spinnerMethod
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20);
        valueFactory.setValue(2);
        teamsNoSpinner.setValueFactory(valueFactory);
        currentSpinnerValue = teamsNoSpinner.getValue();
        teamsNoSpinner.valueProperty().addListener((observable, oldValue, newValue) -> currentSpinnerValue = teamsNoSpinner.getValue());

    }


}
